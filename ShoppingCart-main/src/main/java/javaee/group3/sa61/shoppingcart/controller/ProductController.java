package javaee.group3.sa61.shoppingcart.controller;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.CartItem;
import javaee.group3.sa61.shoppingcart.model.Category;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.validator.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javaee.group3.sa61.shoppingcart.interfacemethods.CartItemInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.CategoryInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.ProductInterface;
import javaee.group3.sa61.shoppingcart.service.CartItemImplementation;
import javaee.group3.sa61.shoppingcart.service.CategoryImplementation;
import javaee.group3.sa61.shoppingcart.service.ProductImplementation;

/**
 * ProductController handles all product-related web requests
 *
 * @author Yue-Sheng, Huang Jun
 * @date 2025/10/1
 **/

@Controller
public class ProductController {

    @Autowired
    private ProductInterface pservice;
    private CategoryInterface catservice;
    private CartItemInterface cartItemService;

    @Autowired
    private ProductValidator productValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(productValidator);
    }

    @Autowired
    public void setProductService(ProductImplementation pserviceImpl) {
        this.pservice = pserviceImpl;
    }

    @Autowired
    public void setCategoryService(CategoryImplementation catserviceImpl) {
        this.catservice = catserviceImpl;
    }

    @Autowired
    public void setCartItemService(CartItemImplementation cartItemServiceImpl) {
        this.cartItemService = cartItemServiceImpl;
    }

    /**
     * main Product page for customers to purchase items
     *
     * @param model Spring Model to pass data to the view
     * @return 'products' view template
     * @author Yue-Sheng
     * @date 2025/09/30
     */
    @GetMapping("/products")
    public String getProductPage(Model model) {
        model.addAttribute("categories", catservice.findAll());
        List<Product> availableProducts = pservice.findAll()
                .stream()
                .filter(product -> product.getStockQty() > 0)
                .collect(Collectors.toList());
        model.addAttribute("products", availableProducts);
        return "products";
    }

    /**
     * Individual product page which shows more information.
     *
     * @param model Spring Model to pass data to the view
     * @param id    Unique identifier of a product
     * @return 'product-details' view template
     * @ author Huang Jun, Yue-Sheng
     */
    @GetMapping("/products/{id}")
    public String getProductDetailsPage(@PathVariable("id") int id,
                                        Model model) {
        try {
            Product product = pservice.findProductById(id);
            if (product.getStockQty() <= 0) {
                return "redirect:/products";
            }
            model.addAttribute("product", product);
        } catch (IllegalArgumentException ex) {
            return "redirect:/products";
        }
        return "product-details";
    }

    /**
     * For admin to maintain products, including adding, editing, and deleting products.
     *
     * @param model Spring Model to pass data to the view
     * @return 'admin-products' view template
     * @author Huang Jun
     * @date 2025/10/1
     */
    @GetMapping("/products/admin/maintain")
    public String getMaintainProductPage(Model model) {
        model.addAttribute("categories", catservice.findAll());
        model.addAttribute("products", pservice.findAll());
        return "admin-products";
    }

    /**
     * Search products by name or category.
     *
     * @param k     Keyword to search for
     * @param t     Type of search: 'name' or 'category'
     * @param model Spring Model to pass data to the view
     * @return 'products' view template
     * @author Huang Jun
     * @date 2025/10/1
     */
    @GetMapping(value = "/products/searching")
    public String search(@RequestParam("keyword") String k,
                         @RequestParam("search_category") String t,
                         Model model) {
        String name = "name";
        String category = "category";
        if (t.equals(name)) {
            model.addAttribute("products",
                    pservice.SearchProductByName(k)
                            .stream()
                            .filter(product -> product.getStockQty() > 0)
                            .collect(Collectors.toList()));
        } else if (t.equals(category)) {
            model.addAttribute("products",
                    pservice.SearchProductByCategory(k)
                            .stream()
                            .filter(product -> product.getStockQty() > 0)
                            .collect(Collectors.toList()));
        } else {
            return "error";
        }
        model.addAttribute("categories", catservice.findAll());
        return "products";
    }

    /**
     * Search products by name or category.
     *
     * @param k     Keyword to search for
     * @param t     Type of search: 'name' or 'category'
     * @param model Spring Model to pass data to the admin view
     * @return 'products' view template
     * @author Yue-Sheng
     * @date 2025/10/1
     */
    @GetMapping(value = "/products/admin/searching")
    public String adminSearch(@RequestParam("keyword") String k,
                              @RequestParam("search_category") String t,
                              Model model) {
        String name = "name";
        String category = "category";
        if (t.equals(name)) {
            model.addAttribute("products", pservice.SearchProductByName(k));
        } else if (t.equals(category)) {
            model.addAttribute("products", pservice.SearchProductByCategory(k));
        } else {
            return "error";
        }
        model.addAttribute("categories", catservice.findAll());
        return "admin-products";
    }

    /**
     * Find a product to display for deletion, and pass the product details to the confirmation.
     *
     * @param id    Unique identifier of a product
     * @param model Spring Model to pass data to the view
     * @return 'delete-product-view' view template
     * @author Huang Jun
     * @date 2025/10/1
     */
    @GetMapping(value = "/products/admin/deletion/{id}")
    public String deleteProductView(@PathVariable("id") Integer id,
                                    Model model) {
        Product product;
        try {
            product = pservice.findProductById(id);
        } catch (IllegalArgumentException ex) {
            return "redirect:/products/admin/maintain";
        }
        model.addAttribute("product", product);

        List<Cart> cartsContainingProduct = cartItemService.findCartItemsByProductId(id)
                .stream()
                .map(CartItem::getCart)
                .filter(cart -> cart != null)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("affectedCartCount", cartsContainingProduct.size());
        model.addAttribute("cartsContainingProduct", cartsContainingProduct);
        return "delete-product-view";
    }

    /**
     * Soft delete a product based on the provided product details.
     *
     * @param product The product entity to be deleted
     * @return redirect to 'products'
     * @author Huang Jun
     * @date 2025/10/1
     */
    @PostMapping(value = "/products/admin/delete")
    public String deleteProduct(@ModelAttribute("product") Product product) {
        try {
            cartItemService.deleteByProductId(product.getId());
            pservice.deleteProduct(product);
        } catch (IllegalArgumentException ex) {
            // Product already removed or was never active.
        }
        return "redirect:/products/admin/maintain";
    }

    /**
     * Edit a product based on the provided product details.
     *
     * @param id    Unique identifier of a product, passed as a path variable
     * @param model Spring Model to pass data to the view
     * @return 'edit-product' view template
     * @author Huang Jun
     * @date 2025/10/1
     */
    @GetMapping(value = "/products/admin/edit/{id}")
    public String editProductForm(@PathVariable("id") Integer id,
                                  Model model) {
        Product product;
        try {
            product = pservice.findProductById(id);
        } catch (IllegalArgumentException ex) {
            return "redirect:/products/admin/maintain";
        }
        model.addAttribute("product", product);
        if (product.getCategory() != null) {
            model.addAttribute("categoryId", product.getCategory().getId());
            model.addAttribute("categoryName", product.getCategory().getName());
        }
        return "edit-product";
    }

    /**
     * Save a product after editing details.
     *
     * @param product       The product entity to be saved
     * @param bindingResult BindingResult to handle validation errors
     * @return redirect to 'products' if successful, otherwise return to 'edit-product' view template
     * @author Huang Jun
     * @date 2025/10/1
     */
    @PostMapping(value = "/products/admin/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult bindingResult,
                              @RequestParam(value = "categoryId", required = false) Integer categoryId,
                              @RequestParam(value = "categoryName", required = false) String categoryName,
                              Model model) {
        String sanitizedName = categoryName != null ? categoryName.trim() : "";
        Optional<Category> categoryOpt = resolveCategory(categoryId, sanitizedName);
        categoryOpt.ifPresent(product::setCategory);
        categoryOpt.ifPresent(cat -> {
            model.addAttribute("categoryId", cat.getId());
            model.addAttribute("categoryName", cat.getName());
        });

        if (categoryOpt.isEmpty()) {
            model.addAttribute("categoryName", sanitizedName);
            if (categoryId != null) {
                model.addAttribute("categoryId", categoryId);
            }
            model.addAttribute("categoryError",
                    sanitizedName.isEmpty()
                            ? "Category is required."
                            : "Category '%s' does not exist. Please select an existing category or create a new one."
                            .formatted(sanitizedName));
        }

        if (bindingResult.hasErrors() || categoryOpt.isEmpty()) {
            return "edit-product";
        }
        pservice.saveProduct(product);
        return "redirect:/products/admin/maintain";
    }

    /**
     * Add a product by providing product details.
     *
     * @param model Spring Model to pass data to the view
     * @return 'add-product' view template
     * @author Huang Jun
     * @date 2025/10/1
     */
    @GetMapping(value = "/products/admin/add")
    public String addProductForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "add-product";
    }

    /**
     * Create a product after adding details.
     *
     * @param product       The product entity to be saved
     * @param bindingResult BindingResult to handle validation errors
     * @return redirect to 'products' if successful, otherwise return to 'add-product' view template
     * @author Huang Jun
     * @date 2025/10/1
     */
    @PostMapping(value = "/products/admin/create")
    public String createProduct(@Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                @RequestParam(value = "categoryName", required = false) String categoryName,
                                Model model) {
        String sanitizedName = categoryName != null ? categoryName.trim() : "";
        // Try to resolve category by ID first, then by name
        Optional<Category> categoryOpt = resolveCategory(categoryId, sanitizedName);
        categoryOpt.ifPresent(product::setCategory);
        categoryOpt.ifPresent(cat -> {
            model.addAttribute("categoryId", cat.getId());
            model.addAttribute("categoryName", cat.getName());
        });

        if (categoryOpt.isEmpty()) {
            model.addAttribute("categoryName", sanitizedName);
            if (categoryId != null) {
                model.addAttribute("categoryId", categoryId);
            }
            model.addAttribute("categoryError",
                    sanitizedName.isEmpty()
                            ? "Category is required."
                            : "Category '%s' does not exist. Please select an existing category or create a new one."
                            .formatted(sanitizedName));
        }

        if (bindingResult.hasErrors() || categoryOpt.isEmpty()) {
            return "add-product";
        }
        pservice.saveProduct(product);
        return "redirect:/products/admin/maintain";
    }

    /**
     * Resolve a Category entity based on either its ID or name.
     *
     * @param categoryId   The ID of the category (can be null)
     * @param categoryName The name of the category (can be null or blank)
     * @return An Optional containing the resolved Category, or empty if not found
     * @author Huang Jun
     * @date 2025/10/08
     */
    private Optional<Category> resolveCategory(Integer categoryId, String categoryName) {
        if (categoryId != null) {
            return catservice.findById(categoryId);
        }
        if (categoryName != null && !categoryName.isBlank()) {
            String trimmed = categoryName.trim();
            if (trimmed.isEmpty()) {
                return Optional.empty();
            }
            return catservice.findByNameIgnoreCase(trimmed);
        }
        return Optional.empty();
    }
}

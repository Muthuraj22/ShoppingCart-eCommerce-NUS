package javaee.group3.sa61.shoppingcart.service;

import java.util.List;

import javaee.group3.sa61.shoppingcart.model.Category;
import javaee.group3.sa61.shoppingcart.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartItemInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.ProductInterface;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.repository.ProductRepository;

@Service
@Transactional
public class ProductImplementation implements ProductInterface {

    private final ProductRepository prepo;

    @Autowired
    CartItemRepository cartItemRepo;

    @Autowired
    public ProductImplementation(ProductRepository prepo,
                                 CartItemInterface cartItemService) {
        this.prepo = prepo;
    }

    /**
     * Create a new product instance
     *
     * @param name        The name of the product
     * @param description The description of the product
     * @param unitPrice   The unit price of the product
     * @param stockQty    The stock quantity of the product
     * @param category    The category of the product
     * @return A new Product instance
     * @author Yue-Sheng
     */
    @Override
    @Transactional
    public Product create(String name, String description, Double unitPrice, int stockQty, Category category, String imagePath) {
        return new Product(name, description, unitPrice, stockQty, category, imagePath);
    }

    /**
     * Save a product to the database
     *
     * @param product The product to be saved
     * @return true if the product was saved, false if a product with the same name already exists
     * @author Yue-Sheng
     */
    @Override
    @Transactional
    public boolean saveProduct(Product product) {
        /**
         * Removing the bottom logic as it is causing edit to stop working.
         * If I change description of product, the name is the same - so condition returns true, and the whole function returns false and skips the save.
         * Removing it allows me to update with no issues. Save product will by default update products based on ID (primary key).
         */
//        if (prepo.findProductsByNameIgnoreCase(product.getName()).isPresent()) {
//            return false;
//        }
        Product saved = prepo.save(product);
        purgeFromCartsIfOutOfStock(saved);
        return true;
    }

    /**
     * Save a product to the database with a category
     *
     * @param product  The product to be saved
     * @param category A category object, Not ID nor Name
     * @return true if the product was saved, false if a product with the same name already exists
     * @author Huang Jun
     */
    @Override
    @Transactional
    public boolean saveProduct(Product product, Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }
        product.setCategory(category);
        Product saved = prepo.save(product);
        purgeFromCartsIfOutOfStock(saved);
        return true;
    }

    /**
     * Search for products by name
     *
     * @param name The name to search for
     * @return A list of products whose names contain the given string
     * @author Yue-Sheng
     */
    @Override
    @Transactional
    public List<Product> SearchProductByName(String name) {
        return prepo.SearchProductByName(name);
    }

    /**
     * Search for products by category name
     *
     * @param category The category name to search for
     * @return A list of products that belong to the given category
     * @author Yue-Sheng
     */
    @Override
    @Transactional
    public List<Product> SearchProductByCategory(String category) {
        return prepo.SearchProductByCategory(category);
    }

    /**
     * Find a product by its ID
     *
     * @param id The ID of the product to find
     * @return The product with the given ID, or null if not found
     * @author Huang Jun
     */
    @Override
    @Transactional
    public Product findProductById(Integer id) {
        return prepo.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or inactive"));
    }

    /**
     * Soft delete a product by marking it inactive
     *
     * @param product The product to delete
     * @author Huang Jun
     */
    @Override
    @Transactional
    public void deleteProduct(Product product) {
        Product existing = prepo.findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (!existing.isActive()) {
            return;
        }
        existing.setActive(false);
        prepo.save(existing);
    }

    /**
     * Find all active products
     *
     * @return A list of all active products
     * @author Yue-Sheng
     */
    @Override
    @Transactional
    public List<Product> findAll() {
        return prepo.findByActiveTrue();
    }

    private void purgeFromCartsIfOutOfStock(Product product) {
        if (product.getStockQty() > 0) {
            return;
        }
        cartItemRepo.deleteByProductId(product.getId());
    }

}

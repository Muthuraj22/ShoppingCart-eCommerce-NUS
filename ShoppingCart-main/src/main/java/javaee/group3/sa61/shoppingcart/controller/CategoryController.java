package javaee.group3.sa61.shoppingcart.controller;

import javaee.group3.sa61.shoppingcart.interfacemethods.CategoryInterface;
import javaee.group3.sa61.shoppingcart.model.Category;
import javaee.group3.sa61.shoppingcart.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * CategoryController handles all category-related web requests
 *
 * @author Huang Jun
 * @date 2025/10/2
 *
 **/

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryInterface catservice;

    /**
     * Suggest categories based on a query string.
     *
     * @param q The query string to search for
     * @return A list of up to 5 categories whose names contain the query string
     * @author Huang Jun
     * @date 2025/10/2
     */
    @GetMapping("/admin/category-suggest")
    @ResponseBody
    public List<Map<String, Object>> suggest(@RequestParam String q) {
        return catservice.suggest(q).stream()
                .map(cat -> Map.<String, Object>of
                        ("id", cat.getId(),
                                "name", cat.getName()))
                .toList();
    }

    /**
     * Create a new category with the provided name and description.
     *
     * @param name        The name of the new category
     * @param description The description of the new category
     * @return The created Category object
     * @author Huang Jun
     * @date 2025/10/2
     */
    @PostMapping("/admin/create")
    @ResponseBody
    public Category createCategory(@RequestParam String name,
                                   @RequestParam String description) {
        Category c = new Category(name.trim(), description);
        if (!catservice.saveCategory(c)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists.");
        }
        return c;
    }

    /**
     * Show the category maintenance page.
     *
     * @param editId optional category ID to edit
     * @param model  Spring MVC model
     * @return view name for Thymeleaf
     * @author Huang Jun
     */
    @GetMapping("/admin/maintain")
    public String maintainCategories(@RequestParam(value = "edit", required = false) Integer editId,
                                     Model model) {
        if (!model.containsAttribute("newCategoryName")) {
            model.addAttribute("newCategoryName", "");
        }
        if (!model.containsAttribute("newCategoryDescription")) {
            model.addAttribute("newCategoryDescription", "");
        }
        if (editId != null && !model.containsAttribute("editCategory")) {
            catservice.findById(editId).ifPresent(cat -> model.addAttribute("editCategory", cat));
        }
        model.addAttribute("categories", catservice.findAll());
        return "admin-categories";
    }

    /**
     * Create a category via maintenance form.
     *
     * @param name        The name of the new category
     * @param description The description of the new category
     * @param redirectAttributes attributes for redirect scenarios
     * @return redirect to the maintenance page
     * @author Huang Jun
     */
    @PostMapping("/admin/create-form")
    public String createCategoryFromForm(@RequestParam String name,
                                         @RequestParam(required = false) String description,
                                         RedirectAttributes redirectAttributes) {
        String trimmedName = name != null ? name.trim() : "";
        String trimmedDescription = description != null ? description.trim() : "";

        if (trimmedName.isEmpty()) {
            redirectAttributes.addFlashAttribute("categoryCreateError", "Category name is required.");
            redirectAttributes.addFlashAttribute("newCategoryName", "");
            redirectAttributes.addFlashAttribute("newCategoryDescription", trimmedDescription);
            return "redirect:/category/admin/maintain";
        }

        Category category = new Category(trimmedName, trimmedDescription);
        if (!catservice.saveCategory(category)) {
            redirectAttributes.addFlashAttribute("categoryCreateError", "Category name already exists.");
            redirectAttributes.addFlashAttribute("newCategoryName", trimmedName);
            redirectAttributes.addFlashAttribute("newCategoryDescription", trimmedDescription);
            return "redirect:/category/admin/maintain";
        }
        redirectAttributes.addFlashAttribute("categorySuccessMessage",
                "Category '" + trimmedName + "' created successfully.");
        return "redirect:/category/admin/maintain";
    }

    /**
     * Update a category via maintenance form.
     *
     * @param id          The ID of the category to update
     * @param name        The new name of the category
     * @param description The new description of the category
     * @param redirectAttributes attributes for redirect scenarios
     * @return redirect to the maintenance page
     * @author Huang Jun
     */
    @PostMapping("/admin/update")
    public String updateCategory(@RequestParam int id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes redirectAttributes) {
        String trimmedName = name != null ? name.trim() : "";
        String trimmedDescription = description != null ? description.trim() : "";
        Category formCategory = new Category(trimmedName, trimmedDescription);
        formCategory.setId(id);

        if (trimmedName.isEmpty()) {
            redirectAttributes.addFlashAttribute("categoryEditError", "Category name is required.");
            redirectAttributes.addFlashAttribute("editCategory", formCategory);
            return "redirect:/category/admin/maintain?edit=" + id;
        }

        Optional<Category> existingOpt = catservice.findById(id);
        if (existingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("categoryEditError", "Category not found.");
            return "redirect:/category/admin/maintain";
        }

        Optional<Category> duplicate = catservice.findByNameIgnoreCase(trimmedName);
        if (duplicate.isPresent() && duplicate.get().getId() != id) {
            redirectAttributes.addFlashAttribute("categoryEditError", "Another category already uses this name.");
            redirectAttributes.addFlashAttribute("editCategory", formCategory);
            return "redirect:/category/admin/maintain?edit=" + id;
        }

        if (!catservice.updateCategory(formCategory)) {
            redirectAttributes.addFlashAttribute("categoryEditError", "Unable to update category. Please try again.");
            redirectAttributes.addFlashAttribute("editCategory", formCategory);
            return "redirect:/category/admin/maintain?edit=" + id;
        }

        redirectAttributes.addFlashAttribute("categorySuccessMessage",
                "Category '" + trimmedName + "' updated successfully.");
        return "redirect:/category/admin/maintain";
    }

    /**
     * Delete a category.
     *
     * @param id The ID of the category to delete
     * @param redirectAttributes attributes for redirect scenarios
     * @return redirect to the maintenance page
     * @author Huang Jun
     */
    @PostMapping("/admin/delete/{id}")
    public String deleteCategory(@PathVariable int id,
                                 RedirectAttributes redirectAttributes) {
        Optional<Category> categoryOpt = catservice.findById(id);
        if (categoryOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("categoryErrorMessage", "Category not found.");
            return "redirect:/category/admin/maintain";
        }
        Category category = categoryOpt.get();
        boolean hasActiveProducts = category.getProducts() != null
                && category.getProducts().stream().anyMatch(Product::isActive);
        if (hasActiveProducts) {
            redirectAttributes.addFlashAttribute("categoryErrorMessage",
                    "Cannot delete category '" + category.getName() + "' because it still has products.");
            return "redirect:/category/admin/maintain?edit=" + id;
        }
        catservice.deleteById(id);
        redirectAttributes.addFlashAttribute("categorySuccessMessage",
                "Category '" + category.getName() + "' deleted successfully.");
        return "redirect:/category/admin/maintain";
    }
}

package javaee.group3.sa61.shoppingcart.interfacemethods;

import java.util.List;
import java.util.Optional;

import javaee.group3.sa61.shoppingcart.model.Category;
import javaee.group3.sa61.shoppingcart.model.Product;


public interface CategoryInterface {
    /**
     * Find all products whose name contains the given string (case-insensitive).
     * @return List of categories
     */
    public List<Category> findAll();

    /**
     * Save a new category if a category with the same name does not already exist.
     * @param category The category to be saved
     * @return Return true if the category was saved, false otherwise
     */
    public boolean saveCategory(Category category);

    /**
     * Find a category by its ID.
     * @param id The ID of the category to be found
     * @return An Optional containing the found category, or empty if not found
     */
    public Optional<Category> findById(int id);

    /**
     * Suggest categories based on a query string.
     * @param q The query string to search for
     * @return A list of up to 5 categories whose names contain the query string (case-insensitive)
     */
    public List<Category> suggest(String q);

    /**
     * Find a category by its name, ignoring case.
     * @param name The category name
     * @return Optional containing the category if found
     */
    public Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Update an existing category.
     * @param category Category with updated fields
     * @return true if update succeeds, false otherwise
     */
    public boolean updateCategory(Category category);

    /**
     * Delete a category by its ID.
     * @param id Category ID
     */
    public void deleteById(int id);
}

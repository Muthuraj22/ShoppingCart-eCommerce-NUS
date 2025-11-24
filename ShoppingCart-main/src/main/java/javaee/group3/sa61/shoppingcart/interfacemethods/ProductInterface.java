package javaee.group3.sa61.shoppingcart.interfacemethods;

import java.util.List;

import javaee.group3.sa61.shoppingcart.model.Category;
import javaee.group3.sa61.shoppingcart.model.Product;


public interface ProductInterface {

    /**
     * Create a new product and persist it to the database
     * @param name
     * @param description
     * @param unitPrice
     * @param stockQty
     * @param category
     * @return
     */
    public Product create(String name, String description, Double unitPrice, int stockQty, Category category, String imagePath);

    /**
     * Save a product to the database
     * @param product
     * @return true if the product was saved successfully, false otherwise
     */
    public boolean saveProduct(Product product);

    /**
     * Save a product to the database with a category
     * @param product
     * @param category A category object, Not ID nor Name
     * @return true if the product was saved successfully, false otherwise
     */
    public boolean saveProduct(Product product, Category category);

    /**
     * Search for products by name (case-insensitive)
     * @param name The name to search for
     * @return A list of products whose names contain the given string
     */
    public List<Product> SearchProductByName(String name);

    /**
     * Search for products by category name (case-insensitive)
     * @param category The category name to search for
     * @return A list of products that belong to the given category
     */
    public List<Product> SearchProductByCategory(String category);

    /**
     * Find an active product by its ID
     * @param id The ID of the product to find
     * @return The product with the given ID
     * @throws IllegalArgumentException if the product is not found or inactive
     */
    public Product findProductById(Integer id);

    /**
     * Soft delete a product by marking it inactive
     * @param product The product to delete
     */
    public void deleteProduct(Product product);

    public List<Product> findAll();

}

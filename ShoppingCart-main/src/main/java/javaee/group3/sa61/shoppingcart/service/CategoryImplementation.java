package javaee.group3.sa61.shoppingcart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.CategoryInterface;
import javaee.group3.sa61.shoppingcart.model.Category;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.repository.CategoryRepository;

@Service
@Transactional
public class CategoryImplementation implements CategoryInterface {

    @Autowired
    CategoryRepository catrepo;

    @Override
    @Transactional
    public List<Category> findAll() {
        List<Category> categories = catrepo.findAll();
        categories.forEach(cat -> {
            if (cat.getProducts() != null) {
                cat.getProducts().size();
            }
        });
        return categories;
    }

    /**
     * @param category The category to be saved
     * @return true if the category was saved, false if a category with the same name already exists
     *
     * @author Huang Jun
     */
    @Override
    @Transactional
    public boolean saveCategory(Category category) {
        if (category == null) {
            return false;
        }
        String name = category.getName() != null ? category.getName().trim() : "";
        if (name.isEmpty()) {
            return false;
        }
        if (catrepo.findCategoriesByNameIgnoreCase(name).isPresent()) {
            return false;
        }
        category.setName(name);
        if (category.getDescription() != null) {
            String desc = category.getDescription().trim();
            category.setDescription(desc.isEmpty() ? null : desc);
        }
        catrepo.save(category);
        return true;
    }

    /**
     * @param id The ID of the category to be found
     * @return An Optional containing the category if found, or empty if not found
     *
     * @author Huang Jun
     */
    @Override
    public Optional<Category> findById(int id) {
        return catrepo.findById(id);
    }

    /**
     * @param q The query string to search for in category names
     * @return A list of up to 5 categories whose names contain the query string, ignoring case
     *
     * @author Huang Jun
     */
    @Override
    public List<Category> suggest(String q) {
        return catrepo.findDistinctTop5ByNameContainingIgnoreCase(q);
    }

    /**
     * @param name The category name to search for
     * @return An Optional containing the category if found, or empty if not found
     *
     * @author Huang Jun
     */
    @Override
    public Optional<Category> findByNameIgnoreCase(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return catrepo.findCategoriesByNameIgnoreCase(name.trim());
    }

    /**
     * Update an existing category.
     * @param category Category with updated fields
     * @return true if update succeeds, false otherwise
     *
     * @author Huang Jun
     */
    @Override
    @Transactional
    public boolean updateCategory(Category category) {
        if (category == null) {
            return false;
        }
        Optional<Category> existingOpt = catrepo.findById(category.getId());
        if (existingOpt.isEmpty()) {
            return false;
        }
        String name = category.getName() != null ? category.getName().trim() : "";
        if (name.isEmpty()) {
            return false;
        }
        Optional<Category> duplicate = catrepo.findCategoriesByNameIgnoreCase(name);
        if (duplicate.isPresent() && duplicate.get().getId() != category.getId()) {
            return false;
        }
        Category managed = existingOpt.get();
        managed.setName(name);
        if (category.getDescription() != null) {
            String desc = category.getDescription().trim();
            managed.setDescription(desc.isEmpty() ? null : desc);
        } else {
            managed.setDescription(null);
        }
        catrepo.save(managed);
        return true;
    }

    /**
     * Delete a category by its ID. Products associated with this category that are inactive will have their category set to null.
     * @param id Category ID
     *
     * @author Huang Jun
     */
    @Override
    @Transactional
    public void deleteById(int id) {
        Optional<Category> categoryOpt = catrepo.findById(id);
        if (categoryOpt.isEmpty()) {
            return;
        }
        Category category = categoryOpt.get();
        if (category.getProducts() != null) {
            category.getProducts().stream()
                    .filter(product -> product != null && !product.isActive())
                    .forEach(product -> {
                        product.setCategory(null);
                    });
        }
        catrepo.delete(category);
    }

}

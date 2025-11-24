package javaee.group3.sa61.shoppingcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import javaee.group3.sa61.shoppingcart.model.Category;


public interface CategoryRepository extends JpaRepository<Category,Integer>{
    Optional<Category> findCategoriesByNameIgnoreCase(String name);

    List<Category> findDistinctTop5ByNameContainingIgnoreCase(String name);
}

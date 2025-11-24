package javaee.group3.sa61.shoppingcart.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javaee.group3.sa61.shoppingcart.model.Product;
import org.springframework.stereotype.Repository;

public interface ProductRepository extends JpaRepository<Product,Integer>{

	@Query("Select p from Product as p where p.active = true and p.name like CONCAT('%',:k,'%') ")
	public ArrayList<Product> SearchProductByName(@Param("k") String keyword);
	
	@Query("SELECT p FROM Product p WHERE p.active = true AND p.category.name LIKE :k")
	public ArrayList<Product> SearchProductByCategory(@Param("k") String keyword);

    public Optional<Product> findProductsByNameIgnoreCase(String name);

    public List<Product> findByActiveTrue();

    public Optional<Product> findByIdAndActiveTrue(Integer id);


}

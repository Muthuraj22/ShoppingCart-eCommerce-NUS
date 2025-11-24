package javaee.group3.sa61.shoppingcart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javaee.group3.sa61.shoppingcart.model.Admin;

public interface AdminRepository extends JpaRepository<Admin,Integer>{

	@Query
	List<Admin> findByUsername(String username);
	
}

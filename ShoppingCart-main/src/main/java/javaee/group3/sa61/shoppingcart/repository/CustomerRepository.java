package javaee.group3.sa61.shoppingcart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import javaee.group3.sa61.shoppingcart.model.Customer;
import org.springframework.stereotype.Repository;


public interface CustomerRepository extends JpaRepository<Customer, Integer>{

	@Query
	List<Customer> findByUsername(String username);


}

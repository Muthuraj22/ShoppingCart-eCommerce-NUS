package javaee.group3.sa61.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javaee.group3.sa61.shoppingcart.model.Cart;
import org.springframework.stereotype.Repository;

public interface CartRepository extends JpaRepository<Cart, Integer>{
	
	@Query("select c from Cart c where c.customer.id=:customerId")
	public Cart getCartByCustomerId(@Param("customerId") int customerId);

}

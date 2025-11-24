package javaee.group3.sa61.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;
import org.springframework.data.repository.query.Param;

import javaee.group3.sa61.shoppingcart.model.CartItem;
import javaee.group3.sa61.shoppingcart.model.CartItemId;
import org.springframework.stereotype.Repository;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId>{
	
	@Query("select c from CartItem c where c.cart.id=:cartId AND c.product.id=:productId")
	CartItem findCartItemId(@Param("cartId") int cartId, @Param("productId") int productId);

	@Query("select ci from CartItem ci join fetch ci.cart c left join fetch c.customer where ci.product.id=:productId")
	List<CartItem> findAllByProductIdWithCart(@Param("productId") int productId);

	@Modifying
	@Query("delete from CartItem ci where ci.product.id=:productId")
	void deleteByProductId(@Param("productId") int productId);

	/**
	 * Remove every cart item associated with the provided cart identifier.
	 *
	 * @param cartId identifier of the cart whose items should be cleared
	 */
	@Modifying
	@Query("delete from CartItem ci where ci.cart.id=:cartId")
	void deleteByCartId(@Param("cartId") int cartId);

    // Count all cart items by cart id
    public int countByCartId(int cartId);
}

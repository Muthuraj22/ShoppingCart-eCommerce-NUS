package javaee.group3.sa61.shoppingcart.interfacemethods;

import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.CartItem;
import java.util.List;
import javaee.group3.sa61.shoppingcart.model.CartItemId;
import javaee.group3.sa61.shoppingcart.model.Product;


public interface CartItemInterface {
	public void save(CartItem cartItem);
	public boolean checkCartItemById(CartItemId cartItemId);
	public CartItem getCartItemById(CartItemId cartItemId);
	public CartItem addCartItem(Cart cart, Product product) ;
	public CartItem reduceCartItem(Cart cart, Product product);
	public void delete(CartItem item);
	List<CartItem> findCartItemsByProductId(int productId);
	void deleteByProductId(int productId);
    public int countByCartId(int cartId);
}

package javaee.group3.sa61.shoppingcart.interfacemethods;

import javaee.group3.sa61.shoppingcart.model.Cart;

public interface CartInterface {
	public void saveCart(Cart cart);
	public Cart getCartByCustomerId(int customerId);

	/**
	 * Retrieve the cart for a customer without creating a placeholder when absent.
	 *
	 * @param customerId identifier of the customer
	 * @return existing cart or null if none is linked
	 */
	public Cart getExistingCartByCustomerId(int customerId);
	public Cart getCartByCartId(int cartId);
	public double getTotalPrice(Cart cart);

}

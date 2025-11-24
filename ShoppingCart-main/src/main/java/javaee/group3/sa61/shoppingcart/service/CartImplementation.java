package javaee.group3.sa61.shoppingcart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.Customer;
import javaee.group3.sa61.shoppingcart.model.CartItem;
import javaee.group3.sa61.shoppingcart.repository.CartRepository;
import javaee.group3.sa61.shoppingcart.repository.CustomerRepository;

@Service
@Transactional
public class CartImplementation implements CartInterface{
	
	@Autowired
	CartRepository cartRepo;
	@Autowired
	CustomerRepository custRepo;

	/**
     * Save a cart to the database
     *
     * @param cart The cart to be saved
     *
     * @author Priya, Maha
     * @date 2025/10/1
     */
	@Override
	@Transactional
	public void saveCart(Cart cart) {
		cartRepo.save(cart);
	}
	

	/**
     * Find a cart of a customer by its customerID
     *
     * @param customerId The ID of the customer to find
     * @return The cart of a customer with the given customerId, or a new cart if not exist already
     *
     * @author Priya, Maha
     * @date 2025/10/1
     */
	@Override
	@Transactional
	public Cart getCartByCustomerId(int customerId) {
		Cart cart = cartRepo.getCartByCustomerId(customerId);
		if (cart == null) {
			Cart newCart = new Cart();
			Customer customer = custRepo.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));
			newCart.setCustomer(customer);
			cart = cartRepo.save(newCart);
		}
		return cart;
	}
	
	/**
     * Find an existing cart of a customer by its customerID
     *
     * @param customerId The ID of the customer to find
     * @return The cart of a customer with the given customerId, or null if not exist already
     *
     * @author Huang Jun
     * @date 2025/10/6
     */
	@Override
	@Transactional
	public Cart getExistingCartByCustomerId(int customerId) {
		return cartRepo.getCartByCustomerId(customerId);
	}
	

	/**
     * Find a cart by its ID
     *
     * @param cartId The ID of the cart to find
     * @return The cart with the given ID, or NULL if not found
     *
     * @author Maha, Priya
     * @date 2025/10/1
     */
	@Override
	@Transactional
	public Cart getCartByCartId(int cartId) {
		Optional<Cart> cart = cartRepo.findById(cartId);
		if (cart.isPresent()) {
			return cart.get();
		}
		System.out.println("Cart not found with id: " + cartId);
		return null;
	}
	

	/**
     * Get the total price of the cart
     *
     * @param cart cart for which the total to be calculated
     * @return totalPrice of the cart
     *
     * @author Maha, Priya
     * @date 2025/10/1
     */
	@Override
	@Transactional
	public double getTotalPrice(Cart cart) {
		List<CartItem> cartItems = cart.getCartItems();
		if (cartItems == null || cartItems.isEmpty()) {
			return 0.0;
		}
		double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            if (item.getQuantity() > 0) {
                totalPrice += item.getProduct().getUnitPrice() * (double) item.getQuantity();
            }
		}
		return totalPrice;
	}

}

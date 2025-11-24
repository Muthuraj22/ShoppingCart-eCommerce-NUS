package javaee.group3.sa61.shoppingcart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartItemInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.CartItem;
import javaee.group3.sa61.shoppingcart.model.CartItemId;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.repository.CartItemRepository;

@Service
@Transactional
public class CartItemImplementation implements CartItemInterface {

	@Autowired
	CartItemRepository cartItemRepo;

	@Override
	@Transactional
	public void save(CartItem cartItem) {
		cartItemRepo.save(cartItem);
	}

	/**
	 * Check whether a cartItem is already present in the cart
	 *
	 * @param cartItemId Unique identifier of a cartItem
	 * @return true if present; false if not present
     *
     * @author Maha, Priya
     * @date 2025/10/1
	 */
	@Override
	@Transactional
	public boolean checkCartItemById(CartItemId cartItemId) {
		int productId = cartItemId.getProductId();
		int cartId = cartItemId.getCartId();
		CartItem cartItem = cartItemRepo.findCartItemId(cartId, productId);
		if (cartItem == null) {
			return false;
		}
		return true;
	}

	/**
	 * Check whether a cartItem is already present in the cart
	 *
	 * @param cartItemId Unique identifier of a cartItem
	 * @return cartItem for the given id.
     *
     * @author Maha, Priya
     * @date 2025/10/1
	 */
	@Override
	@Transactional
	public CartItem getCartItemById(CartItemId cartItemId) {
		int cartId = cartItemId.getCartId();
		int productId = cartItemId.getProductId();
		CartItem cartItem = cartItemRepo.findCartItemId(cartId, productId);
		if (cartItem == null) {
			cartItem = new CartItem();
		}
		return cartItem;
	}

    /**
     * while adding quantity checks whether the requested quantity is greater than the stock quantity
     * returns null if exceeds else  proceeds to set quantity adn save cart item
     *
     * @author Maha, Priya, Huang Jun
     * @date 2025/10/1
     *
     **/

    @Override
    @Transactional
    public CartItem addCartItem(Cart cart, Product product){
            if (product == null || product.getStockQty() <= 0) {
                return null;
            }

            CartItemId id = new CartItemId(cart.getId(), product.getId());
            CartItem item;
            if (checkCartItemById(id)) {
                item = getCartItemById(id);
                if (item.getQuantity() + 1 > product.getStockQty()) {
                    return null;
                }
                item.setQuantity(item.getQuantity() + 1);
            } else {
                item = new CartItem(1, cart, product);
            }
            return item;
        }


    /**
     * Reduces the quantity of cartItem of given cart and product
     *
     * @param cart cart from which cartItem is reduced
     * @param product product whose quantity is to be reduced
     * @return cartItem with reduced quantity or null if the current quantity is 1
     *
     * @author Maha, Priya
     * @date 2025/10/1
     */

    @Override
	@Transactional
	public CartItem reduceCartItem(Cart cart, Product product) {
		CartItemId id = new CartItemId(cart.getId(), product.getId());
		CartItem item = getCartItemById(id);
		if (item.getQuantity() == 1) {
			return null;
		}
		item.setQuantity(item.getQuantity() - 1);
		return item;
	}

    /**
     * Delete the given cartItem from database
     *
     * @param item item to be deleted
     *
     * @author Maha, Priya
     * @date 2025/10/1
     */
    @Override
	public void delete(CartItem item) {
		cartItemRepo.delete(item);
	}

    /**
     * Find the cartItem from the database using the given productId
     *
     * @param productId Unique identifier of a product
     * @return List of cartItems with the given productId
     * @author Huang Jun
     * @date 2025/10/6
     */
    @Override
	@Transactional
	public List<CartItem> findCartItemsByProductId(int productId) {
		return cartItemRepo.findAllByProductIdWithCart(productId);
	}

    /**
     * Delete the cartItem of given productId from the database
     *
     * @param productId Unique identifier of a product
     * @author Huang Jun
     * @date 2025/10/6
     */
    @Override
	@Transactional
	public void deleteByProductId(int productId) {
		cartItemRepo.deleteByProductId(productId);
	}

    /**
     * Count the number of cartItems of given cart
     * @param cartId Unique identifier of a cart
     * @return number of cartItems
     *
     * @author Yue-Sheng
     * @date 2025/10/8
     */
    @Override
    @Transactional
    public int countByCartId(int cartId) {
        return cartItemRepo.countByCartId(cartId);
    };
}

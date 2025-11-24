package javaee.group3.sa61.shoppingcart.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class CartItem {
	
	@EmbeddedId
	private CartItemId cartItemId;
	
	private int quantity;

	@ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

	@ManyToOne
	@MapsId("cartId")
	@JoinColumn(name="cart_id", referencedColumnName = "id")
	private Cart cart;
	
	public CartItem() {}
	
	public CartItem(int quantity, Cart cart, Product product) {
		this.cartItemId = new CartItemId(cart.getId(), product.getId());
		this.quantity = quantity;
		this.cart=cart;
		this.product=product;
	}
	
	public CartItemId getCartItemId() {
		return cartItemId;
	}

	public void setCartItemId(CartItemId cartItemId) {
		this.cartItemId = cartItemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
		if(this.cartItemId==null)
			this.cartItemId=new CartItemId();
		this.cartItemId.setProductId(product.getId());
		
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
		if(this.cartItemId==null)
			this.cartItemId=new CartItemId();
		this.cartItemId.setCartId(cart.getId());
	}

	public String toString() {
		return "CartItemKey[cartId=" + cart.getId() + ", productId=" + product.getId() + "]";
	}

}

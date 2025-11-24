package javaee.group3.sa61.shoppingcart.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CartItemId implements Serializable{
	
    @Column(name = "cart_id")
	private int cartId;
    
    @Column(name = "product_id")
	private int productId;
	
	public CartItemId() {
		
	}

	public CartItemId(int cartId, int productId) {
		this.cartId = cartId;
		this.productId = productId;
	}
	
	public int getCartId() {
		return cartId;
	}

	public void setCartId(int cartId) {
		this.cartId = cartId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(!(o instanceof CartItemId))
			return false;
		 CartItemId that = (CartItemId) o;
	     return Objects.equals(cartId, that.cartId) &&
	            Objects.equals(productId, that.productId);
	}
	     
	@Override
	public int hashCode() {
	    return Objects.hash(cartId, productId);
	}
}

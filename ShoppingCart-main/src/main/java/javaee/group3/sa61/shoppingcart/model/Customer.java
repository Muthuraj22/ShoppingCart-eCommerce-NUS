package javaee.group3.sa61.shoppingcart.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("Customer")
public class Customer extends User{
	private String address;
	
	@OneToOne(mappedBy="customer", cascade = CascadeType.ALL)
	private Cart cart;
	
	@OneToMany(mappedBy="customer")
	private List<Order> orders;
	
	public Customer() {}
	
	public Customer(String address, Cart cart) {
		super();
		this.address = address;
		this.cart = cart;
	}

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}

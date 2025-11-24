package javaee.group3.sa61.shoppingcart;

import javaee.group3.sa61.shoppingcart.interfacemethods.*;
import javaee.group3.sa61.shoppingcart.model.*;
import javaee.group3.sa61.shoppingcart.repository.*;
import javaee.group3.sa61.shoppingcart.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test class for CartItemInterface.
 * This class tests the functionality of saving CartItems associated with a Cart and Products.
 *
 * @author Priya, Yue-Sheng
 */

@SpringBootTest
public class CartItemInterfaceTest {
    @Autowired
    private ProductInterface productservice;
    @Autowired
    public void setProductService(ProductImplementation productserviceImpl) {
        this.productservice = productserviceImpl;
    }

    @Autowired
    private CartInterface cartservice;
    @Autowired
    public void setCartService(CartImplementation cartserviceImpl) {
        this.cartservice = cartserviceImpl;
    }

    @Autowired
        CartItemInterface ciservice;

    @Autowired
    public void setCartItemService(CartItemImplementation ciserviceImpl) {
        this.ciservice = ciserviceImpl;
    }

        @Test
        public void cartItemTest() {

            Cart cartToSave = new Cart();
            Customer customerToSave = new Customer();
            customerToSave.setName("Alice");
            customerToSave.setAddress("Test Address");
            customerToSave.setEmailAddress("test@test.com");
            customerToSave.setPhoneNumber("81234567");
            customerToSave.setRole("customer");
            customerToSave.setUsername("alice");
            customerToSave.setPassword("password");
            cartToSave.setCustomer(customerToSave);

            cartservice.saveCart(cartToSave);

            Product product1 = productservice.findProductById(1);
            Product product2 = productservice.findProductById(2);
            Product product3 = productservice.findProductById(3);

            CartItemId cartItemId1 = new CartItemId(cartToSave.getId(),product1.getId());
            CartItemId cartItemId2 = new CartItemId(cartToSave.getId(),product2.getId());
            CartItemId cartItemId3 = new CartItemId(cartToSave.getId(),product3.getId());

            CartItem cartItem1 = new CartItem();
            cartItem1.setCartItemId(cartItemId1);
            cartItem1.setCart(cartToSave);
            cartItem1.setProduct(product1);
            cartItem1.setQuantity(1);

            CartItem cartItem2 = new CartItem();
            cartItem2.setCartItemId(cartItemId2);
            cartItem2.setCart(cartToSave);
            cartItem2.setProduct(product2);
            cartItem2.setQuantity(2);

            CartItem cartItem3 = new CartItem();
            cartItem3.setCartItemId(cartItemId3);
            cartItem3.setCart(cartToSave);
            cartItem3.setProduct(product3);
            cartItem3.setQuantity(3);

            ciservice.save(cartItem1);
            ciservice.save(cartItem2);
            ciservice.save(cartItem3);



    }
}



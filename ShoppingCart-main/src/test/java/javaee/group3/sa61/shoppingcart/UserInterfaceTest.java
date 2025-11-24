package javaee.group3.sa61.shoppingcart;

import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.CategoryInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.ProductInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.UserInterface;
import javaee.group3.sa61.shoppingcart.model.*;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;
import javaee.group3.sa61.shoppingcart.service.CategoryImplementation;
import javaee.group3.sa61.shoppingcart.service.ProductImplementation;
import javaee.group3.sa61.shoppingcart.service.UserImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test class for UserInterface.
 * This class tests the functionality of saving Customer and Admin users.
 *
 * @author Yue-Sheng
 */

@SpringBootTest
public class UserInterfaceTest {
	@Autowired
	private UserInterface uservice;

    @Autowired
    private CartInterface cartservice;

    @Autowired
	public void setUserService(UserImplementation userviceImpl) {
		this.uservice = userviceImpl;
	}

    @Autowired
    public void setCartService(CartImplementation cartserviceImpl) {
        this.cartservice = cartserviceImpl;
    }

	@Test
	public void testSaveCustomerAndAdmin() {

        Cart cartToSave = new Cart();

		Customer customerToSave = new Customer();
        customerToSave.setName("John");
        customerToSave.setAddress("Test Address");
        customerToSave.setEmailAddress("test@test.com");
        customerToSave.setPhoneNumber("88888888");
        customerToSave.setRole("customer");
        customerToSave.setUsername("customer");
        customerToSave.setPassword("password");

        cartToSave.setCustomer(customerToSave);

        Admin adminToSave = new Admin();
        adminToSave.setName("Administrator");
        adminToSave.setEmailAddress("test@test.com");
        adminToSave.setPhoneNumber("88888888");
        adminToSave.setRole("admin");
        adminToSave.setUsername("root");
        adminToSave.setPassword("password");

        uservice.saveAdmin(adminToSave);
        cartservice.saveCart(cartToSave);

	}
	
	
}

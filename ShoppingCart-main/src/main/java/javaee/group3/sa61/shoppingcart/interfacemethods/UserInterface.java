package javaee.group3.sa61.shoppingcart.interfacemethods;

import java.util.List;

import javaee.group3.sa61.shoppingcart.model.Admin;
import javaee.group3.sa61.shoppingcart.model.Customer;
import javaee.group3.sa61.shoppingcart.model.User;


public interface UserInterface {

	public List<User> findUsersByUsername(String username);

    void saveCustomer(Customer customerToSave);

    void saveAdmin(Admin adminToSave);
}

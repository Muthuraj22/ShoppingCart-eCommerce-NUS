package javaee.group3.sa61.shoppingcart.service;

import java.util.ArrayList;
import java.util.List;

import javaee.group3.sa61.shoppingcart.model.Admin;
import javaee.group3.sa61.shoppingcart.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.UserInterface;
import javaee.group3.sa61.shoppingcart.model.User;
import javaee.group3.sa61.shoppingcart.repository.AdminRepository;
import javaee.group3.sa61.shoppingcart.repository.CustomerRepository;

@Service
@Transactional
public class UserImplementation implements UserInterface {
	
	@Autowired
	CustomerRepository cusrepo;
	
	@Autowired
	AdminRepository adminrepo;

    /**
     * Finds users by their username across both Admin and Customer repositories.
     *
     * @param username the username to search for
     * @return a list of users matching the given username
     * @author Yue-Sheng
     */
	@Override
	@Transactional
	public List<User> findUsersByUsername(String username) {
	    List<User> users = new ArrayList<>();
	    users.addAll(adminrepo.findByUsername(username));
	    users.addAll(cusrepo.findByUsername(username));
	    return users;
	}

    @Override
    public void saveCustomer(Customer customer) {
        cusrepo.save(customer);
    }

    @Override
    public void saveAdmin(Admin admin) {
        adminrepo.save(admin);
    }
}
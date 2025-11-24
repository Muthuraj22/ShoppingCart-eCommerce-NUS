package javaee.group3.sa61.shoppingcart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.CustomerInterface;
import javaee.group3.sa61.shoppingcart.model.Customer;
import javaee.group3.sa61.shoppingcart.repository.CustomerRepository;

@Service
@Transactional
public class CustomerImplementation implements CustomerInterface{
	@Autowired
	CustomerRepository prepo;

	@Override
	@Transactional
	public boolean saveCustomer(Customer customer) {
		prepo.save(customer);
		return true;
	}
	@Override
	@Transactional
	public Customer getCustomer(int customerId) {
		return prepo.findById(customerId).get();
		
	}
	
	/**
	 * Retrieve every customer entity for administrative views.
	 *
	 * @return list containing all customers
	 */
	@Override
	@Transactional
	public List<Customer> getAllCustomers() {
		return prepo.findAll();
	}
}

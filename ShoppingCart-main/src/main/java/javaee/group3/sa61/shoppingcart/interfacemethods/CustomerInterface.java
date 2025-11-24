package javaee.group3.sa61.shoppingcart.interfacemethods;

import java.util.List;

import javaee.group3.sa61.shoppingcart.model.Customer;

public interface CustomerInterface {
	public boolean saveCustomer(Customer customer);

	public Customer getCustomer(int customerId);

	/**
	 * Load every customer entity for administrative consumption.
	 *
	 * @return list of all customers
	 */
	public List<Customer> getAllCustomers();

}

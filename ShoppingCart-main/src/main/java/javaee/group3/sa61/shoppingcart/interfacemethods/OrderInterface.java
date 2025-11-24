package javaee.group3.sa61.shoppingcart.interfacemethods;

import java.util.List;

import javaee.group3.sa61.shoppingcart.model.Order;

public interface OrderInterface {
	/**
	 * Load every order with its detail associations for administrative features.
	 *
	 * @return list of orders sorted by repository rules
	 */
	public List<Order> getAllOrdersWithDetails();

	/**
	 * Retrieve a single order including detail information when available.
	 *
	 * @param orderId identifier of an order
	 * @return matching order by orderId or null when not found
	 */
	public Order getOrderDetailById(int orderId);

	/**
	 * Retrieve all orders of a customer with detailed associations.
	 *
	 * @param customerId identifier of the customer
	 * @return list of orders created by the customer
	 */
	public List<Order> getOrdersByCustomerWithDetails(int customerId);

    /**
     * Save the newly created order
     *
     * @param order to be saved
     */
    public void saveOrder(Order order);
}

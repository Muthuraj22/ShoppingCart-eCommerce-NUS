package javaee.group3.sa61.shoppingcart.service;

import java.util.List;
import java.util.Optional;

import javaee.group3.sa61.shoppingcart.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.OrderInterface;
import javaee.group3.sa61.shoppingcart.model.Order;
import javaee.group3.sa61.shoppingcart.repository.OrderRepository;

@Service
@Transactional
public class OrderImplementation implements OrderInterface {

	@Autowired
	private OrderRepository orderRepo;

	/**
	 * Load every order with eager associations ready for admin consumption.
	 *
	 * @return list of orders including customer and line item information
	 */
	@Override
	@Transactional
	public List<Order> getAllOrdersWithDetails() {
		return orderRepo.findAllWithDetails();
	}

	/**
	 * Retrieve a single order with detail information when available.
	 *
	 * @param orderId identifier of the order
	 * @return matching order or null if not found
	 */
	@Override
	@Transactional
	public Order getOrderDetailById(int orderId) {
		Order order = orderRepo.findDetailById(orderId);
		if (order == null) {
			Optional<Order> optionalOrder = orderRepo.findById(orderId);
			return optionalOrder.orElse(null);
		}
		return order;
	}

	/**
	 * Retrieve all orders for a single customer with detailed associations.
	 *
	 * @param customerId identifier of the customer
	 * @return list of orders placed by the customer
	 */
	@Override
	@Transactional
	public List<Order> getOrdersByCustomerWithDetails(int customerId) {
		List<Order> orders = orderRepo.findByCustomerIdWithDetails(customerId);
        // Compute total for each order
        for (Order order : orders) {
            double total = 0.0;
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    total += item.getPurchaseUnitPrice() * item.getQuantity();
                }
            }

            order.setTotalPrice(total); // added transient field in Order
        }
        return orders;
	}

    /**
     * Save the newly created order
     *
     * @param order The order to be saved
     */

    @Override
    @Transactional
    public void saveOrder(Order order) {
        orderRepo.save(order);
    }
}

package javaee.group3.sa61.shoppingcart.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.CustomerInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.OrderInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.Customer;
import javaee.group3.sa61.shoppingcart.model.Order;
import javaee.group3.sa61.shoppingcart.model.OrderItem;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;
import javaee.group3.sa61.shoppingcart.service.CustomerImplementation;
import javaee.group3.sa61.shoppingcart.service.OrderImplementation;

/**
 * AdminOrderRestController exposes order data for admin users over JSON.
 *
 * @author Huang Jun
 * @date 2025/10/06
 */

@RestController
@RequestMapping("/admin/api/orders")
public class AdminOrderRestController {

	private final OrderInterface orderService;
	private final CustomerInterface customerService;
	private final CartInterface cartService;

	/**
	 * Create a REST controller that exposes admin order data over JSON.
	 *
	 * @param orderImplementation order service implementation
	 * @param customerImplementation customer service implementation
	 * @param cartImplementation cart service implementation
     * @author Huang Jun
	 */
	@Autowired
	public AdminOrderRestController(OrderImplementation orderImplementation,
									CustomerImplementation customerImplementation,
									CartImplementation cartImplementation) {
		this.orderService = orderImplementation;
		this.customerService = customerImplementation;
		this.cartService = cartImplementation;
	}

	/**
	 * Retrieve every order in the system for quick admin review.
	 *
	 * @return list of summary maps
     * @author Huang Jun
	 */
	@GetMapping
	public List<Map<String, Object>> getAllOrders() {
		List<Map<String, Object>> summaries = new ArrayList<>();
		List<Order> orders = orderService.getAllOrdersWithDetails();
		for(Order order : orders) {
			summaries.add(toOrderSummary(order));
		}
		return summaries;
	}

	/**
	 * Provide an order detail payload containing line items and customer summary.
	 *
	 * @param orderId identifier of the requested order
	 * @return map containing order detail and customer summary
     * @author Huang Jun
	 */
	@GetMapping("/{orderId}")
	public Map<String, Object> getOrder(@PathVariable int orderId) {
		Order order = orderService.getOrderDetailById(orderId);
		if(order == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
		}
		Customer owner = order.getCustomer();
		if(owner == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found for order");
		}
		Customer customer = fetchCustomer(owner.getId());
		int orderCount = orderService.getOrdersByCustomerWithDetails(customer.getId()).size();
		Cart cart = cartService.getExistingCartByCustomerId(customer.getId());

		Map<String, Object> payload = new HashMap<>();
		payload.put("orderDetail", toOrderDetail(order));
		payload.put("customerSummary", toCustomerSummary(customer, orderCount, cart));
		return payload;
	}

	/**
	 * Link an order back to its owning customer and provide summary info.
	 *
	 * @param orderId identifier of the order
	 * @return summary map for the order owner
     * @author Huang Jun
	 */
	@GetMapping("/{orderId}/customer")
	public Map<String, Object> getOrderCustomer(@PathVariable int orderId) {
		Order order = orderService.getOrderDetailById(orderId);
		if(order == null || order.getCustomer() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
		}
		Customer customer = fetchCustomer(order.getCustomer().getId());
		int orderCount = orderService.getOrdersByCustomerWithDetails(customer.getId()).size();
		Cart cart = cartService.getExistingCartByCustomerId(customer.getId());
		return toCustomerSummary(customer, orderCount, cart);
	}

	/**
	 * Retrieve a customer or throw 404 when the identifier is unknown.
	 *
	 * @param customerId identifier to resolve
	 * @return customer entity
     * @author Huang Jun
	 */
	private Customer fetchCustomer(int customerId) {
		try {
			return customerService.getCustomer(customerId);
		} catch (NoSuchElementException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
		}
	}

	/**
	 * Convert an {@link Order} into the summary map returned by the admin API.
	 *
	 * @param order order entity, may be null
	 * @return summary map containing totals and customer info
     * @author Huang Jun
	 */
	private Map<String, Object> toOrderSummary(Order order) {
		Map<String, Object> summary = new HashMap<>();
		if(order == null) {
			return summary;
		}

		List<OrderItem> orderItems = order.getOrderItems() != null ? order.getOrderItems() : Collections.emptyList();
		int totalQuantity = 0;
		double totalAmount = 0.0;
		for(OrderItem item : orderItems) {
			if(item == null) {
				continue;
			}
			double unitPrice = item.getPurchaseUnitPrice() != null ? item.getPurchaseUnitPrice() : 0.0;
			totalQuantity += item.getQuantity();
			totalAmount += unitPrice * item.getQuantity();
		}

		Customer customer = order.getCustomer();
		summary.put("orderId", order.getId());
		summary.put("orderDate", order.getOrderDate());
		summary.put("paymentMethod", order.getPaymentMethod());
		summary.put("customerId", customer != null ? customer.getId() : null);
		summary.put("customerName", customer != null ? customer.getName() : "Unknown");
		summary.put("totalAmount", totalAmount);
		summary.put("itemCount", totalQuantity);
		return summary;
	}

	/**
	 * Add line items to the summary map, producing the full detail payload.
	 *
	 * @param order order entity
	 * @return map ready for serialisation
     * @author Huang Jun
	 */
	private Map<String, Object> toOrderDetail(Order order) {
		Map<String, Object> detail = new HashMap<>(toOrderSummary(order));
		detail.put("items", toOrderItemViews(order != null ? order.getOrderItems() : null));
		return detail;
	}

	/**
	 * Convert order items into REST-friendly maps.
	 *
	 * @param orderItems order items list
	 * @return list of map representations
     * @author Huang Jun
	 */
	private List<Map<String, Object>> toOrderItemViews(List<OrderItem> orderItems) {
		if(orderItems == null || orderItems.isEmpty()) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> result = new ArrayList<>();
		for(OrderItem item : orderItems) {
			if(item == null) {
				continue;
			}
			Map<String, Object> itemView = new HashMap<>();
			Product product = item.getProduct();
			itemView.put("productId", product != null ? product.getId() : null);
			String purchaseName = item.getPurchaseName();
			if(purchaseName == null && product != null) {
				// Fall back to the product's current name when the snapshot is unavailable.
				purchaseName = product.getName();
			}
			itemView.put("purchaseName", purchaseName != null ? purchaseName : "Unknown");
			double unitPrice = item.getPurchaseUnitPrice() != null ? item.getPurchaseUnitPrice() : 0.0;
			itemView.put("purchaseUnitPrice", unitPrice);
			itemView.put("quantity", item.getQuantity());
			itemView.put("lineTotal", unitPrice * item.getQuantity());
			result.add(itemView);
		}
		return result;
	}

	/**
	 * Build a compact summary map for the owning customer.
	 *
	 * @param customer customer entity
	 * @param orderCount number of orders associated with the customer
	 * @param cart latest cart associated with the customer
	 * @return customer summary map
     * @author Huang Jun
	 */
	private Map<String, Object> toCustomerSummary(Customer customer, int orderCount, Cart cart) {
		Map<String, Object> summary = new HashMap<>();
		summary.put("customerId", customer.getId());
		summary.put("name", customer.getName());
		summary.put("username", customer.getUsername());
		summary.put("emailAddress", customer.getEmailAddress());
		summary.put("phoneNumber", customer.getPhoneNumber());
		summary.put("address", customer.getAddress());
		summary.put("orderCount", orderCount);
		summary.put("cartId", cart != null ? cart.getId() : null);
		return summary;
	}
}

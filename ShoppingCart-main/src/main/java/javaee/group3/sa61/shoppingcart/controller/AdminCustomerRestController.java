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
import javaee.group3.sa61.shoppingcart.model.CartItem;
import javaee.group3.sa61.shoppingcart.model.Customer;
import javaee.group3.sa61.shoppingcart.model.Order;
import javaee.group3.sa61.shoppingcart.model.OrderItem;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;
import javaee.group3.sa61.shoppingcart.service.CustomerImplementation;
import javaee.group3.sa61.shoppingcart.service.OrderImplementation;


/**
 * REST controller that exposes customer data and related entities for admin users.
 *
 * @author Huang Jun
 * @date 2025/10/06
 */
@RestController
@RequestMapping("/admin/api/customers")
public class AdminCustomerRestController {

	private final CustomerInterface customerService;
	private final CartInterface cartService;
	private final OrderInterface orderService;

	/**
	 * Create a REST controller backed by the concrete service implementations.
	 *
	 * @param customerImplementation customer service implementation
	 * @param cartImplementation cart service implementation
	 * @param orderImplementation order service implementation
     * @author Huang Jun
	 */
	@Autowired
	public AdminCustomerRestController(CustomerImplementation customerImplementation,
									   CartImplementation cartImplementation,
									   OrderImplementation orderImplementation) {
		this.customerService = customerImplementation;
		this.cartService = cartImplementation;
		this.orderService = orderImplementation;
	}

	/**
	 * Provide a compact summary list of all customers for admin usage.
	 *
	 * @return immutable list of summary maps
     * @author Huang Jun
	 */
	@GetMapping
	public List<Map<String, Object>> getAllCustomers() {
		List<Map<String, Object>> summaries = new ArrayList<>();
		List<Customer> customers = customerService.getAllCustomers();
		for(Customer customer : customers) {
			Cart cart = cartService.getExistingCartByCustomerId(customer.getId());
			int orderCount = orderService.getOrdersByCustomerWithDetails(customer.getId()).size();
			summaries.add(toCustomerSummary(customer, orderCount, cart));
		}
		return summaries;
	}

	/**
	 * Retrieve a detailed profile that bundles customer info, current cart, and
	 * order history.
	 *
	 * @param customerId identifier of the customer to inspect
	 * @return map describing the customer
     * @author Huang Jun
	 */
	@GetMapping("/{customerId}")
	public Map<String, Object> getCustomerDetail(@PathVariable int customerId) {
		Customer customer = fetchCustomer(customerId);
		Cart cart = cartService.getExistingCartByCustomerId(customerId);
		List<Order> orders = orderService.getOrdersByCustomerWithDetails(customerId);

		Map<String, Object> payload = new HashMap<>();
		payload.put("customer", toCustomerSummary(customer, orders.size(), cart));
		payload.put("cart", toCartView(cart));
		List<Map<String, Object>> orderSummaries = new ArrayList<>();
		for(Order order : orders) {
			orderSummaries.add(toOrderSummary(order));
		}
		payload.put("orders", orderSummaries);
		return payload;
	}

	/**
	 * List historical orders for a specific customer.
	 *
	 * @param customerId identifier of the customer
	 * @return list of order summary maps
     * @author Huang Jun
	 */
	@GetMapping("/{customerId}/orders")
	public List<Map<String, Object>> getCustomerOrders(@PathVariable int customerId) {
		fetchCustomer(customerId);
		List<Map<String, Object>> orderSummaries = new ArrayList<>();
		List<Order> orders = orderService.getOrdersByCustomerWithDetails(customerId);
		for(Order order : orders) {
			orderSummaries.add(toOrderSummary(order));
		}
		return orderSummaries;
	}

	/**
	 * View the live cart snapshot for a customer.
	 *
	 * @param customerId identifier of the customer
	 * @return cart summary map, or an empty view when missing
     * @author Huang Jun
	 */
	@GetMapping("/{customerId}/cart")
	public Map<String, Object> getCustomerCart(@PathVariable int customerId) {
		fetchCustomer(customerId);
		Cart cart = cartService.getExistingCartByCustomerId(customerId);
		return toCartView(cart);
	}

	/**
	 * Fetch a specific order detail and ensure it belongs to the given customer.
	 *
	 * @param customerId identifier of the customer who should own the order
	 * @param orderId identifier of the order
	 * @return order detail map
     * @author Huang Jun
	 */
	@GetMapping("/{customerId}/orders/{orderId}")
	public Map<String, Object> getCustomerOrder(@PathVariable int customerId, @PathVariable int orderId) {
		Customer customer = fetchCustomer(customerId);
		Order order = orderService.getOrderDetailById(orderId);
		if(order == null || order.getCustomer() == null || order.getCustomer().getId() != customer.getId()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for customer");
		}
		return toOrderDetail(order);
	}

	/**
	 * Locate a customer by id or throw HTTP 404 when the record is absent.
	 *
	 * @param customerId identifier to search for
	 * @return the matching customer
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
	 * Convert domain entities into a summary map.
	 *
	 * @param customer customer entity source
	 * @param orderCount number of orders associated to the customer
	 * @param cart matching cart entity, may be null
	 * @return populated summary map
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

	/**
	 * Convert a cart entity into a light-weight summary suited for admins.
	 *
	 * @param cart cart entity, possibly null
	 * @return cart summary map with aggregated totals
     * @author Huang Jun
	 */
	private Map<String, Object> toCartView(Cart cart) {
		Map<String, Object> cartView = new HashMap<>();
		if(cart == null) {
			cartView.put("cartId", null);
			cartView.put("itemCount", 0);
			cartView.put("totalAmount", 0.0);
			cartView.put("items", Collections.emptyList());
			return cartView;
		}

		List<Map<String, Object>> items = new ArrayList<>();
		int totalQuantity = 0;
		double totalAmount = 0.0;
		List<CartItem> cartItems = cart.getCartItems();
		if(cartItems != null) {
			for(CartItem cartItem : cartItems) {
				if(cartItem == null) {
					continue;
				}
				Map<String, Object> itemView = toCartItemView(cartItem);
				items.add(itemView);
				totalQuantity += cartItem.getQuantity();
				totalAmount += ((Number) itemView.get("lineTotal")).doubleValue();
			}
		}

		cartView.put("cartId", cart.getId());
		cartView.put("itemCount", totalQuantity);
		cartView.put("totalAmount", totalAmount);
		cartView.put("items", items);
		return cartView;
	}

	/**
	 * Convert a cart item entity into the map structure exposed via REST.
	 *
	 * @param item cart item entity
	 * @return serialisable map describing the item
     * @author Huang Jun
	 */
	private Map<String, Object> toCartItemView(CartItem item) {
		Map<String, Object> itemView = new HashMap<>();
		Product product = item.getProduct();
		double unitPrice = (product != null) ? product.getUnitPrice() : 0.0;
		double lineTotal = unitPrice * item.getQuantity();
		itemView.put("productId", product != null ? product.getId() : null);
		itemView.put("productName", product != null ? product.getName() : "Unknown");
		itemView.put("unitPrice", unitPrice);
		itemView.put("quantity", item.getQuantity());
		itemView.put("lineTotal", lineTotal);
		return itemView;
	}

	/**
	 * Generate a compact order summary for admin views.
	 *
	 * @param order order entity
	 * @return summary map
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
		summary.put("customerId", customer != null ? customer.getId() : null);
		summary.put("customerName", customer != null ? customer.getName() : "Unknown");
		summary.put("totalAmount", totalAmount);
		summary.put("itemCount", totalQuantity);
		return summary;
	}

	/**
	 * Convert an order into a detail map with individual line items included.
	 *
	 * @param order order entity to convert
	 * @return detail map
     * @author Huang Jun
	 */
	private Map<String, Object> toOrderDetail(Order order) {
		Map<String, Object> detail = new HashMap<>(toOrderSummary(order));
		detail.put("items", toOrderItemViews(order != null ? order.getOrderItems() : null));
		return detail;
	}

	/**
	 * Convert order items to maps for detail representation.
	 *
	 * @param orderItems order items, may be null
	 * @return list of item maps
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
				// Fall back to current product name when the historical snapshot is missing.
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
}

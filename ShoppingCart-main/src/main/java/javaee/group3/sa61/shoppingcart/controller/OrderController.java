package javaee.group3.sa61.shoppingcart.controller;

import java.util.List;

import javaee.group3.sa61.shoppingcart.interfacemethods.OrderItemInterface;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;
import javaee.group3.sa61.shoppingcart.service.OrderImplementation;
import javaee.group3.sa61.shoppingcart.service.OrderItemImplementation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import javaee.group3.sa61.shoppingcart.interfacemethods.OrderInterface;
import javaee.group3.sa61.shoppingcart.model.Order;
import javaee.group3.sa61.shoppingcart.model.OrderItem;
import javaee.group3.sa61.shoppingcart.model.OrderItemId;
import javaee.group3.sa61.shoppingcart.repository.OrderItemRepository;

/**
 * OrderController exposes customer-facing views for purchase history and order item details.
 *
 * @author Muthu Raj
 * @date 2025/10/02
 */
@Controller
@RequestMapping("/orders")
public class  OrderController {

    private final OrderInterface orderService;
    private final OrderItemInterface oiService;

    private static final String CUSTOMER_ROLE = "customer";

    public OrderController(OrderImplementation orderServiceImpl,
                           OrderItemImplementation oiServiceImpl) {
        this.orderService = orderServiceImpl;
        this.oiService = oiServiceImpl;
    }


    /**
     * Render the purchase history view for the currently authenticated customer.
     *
     * @param sessionObj Session object to retrieve the logged-in customer's identifier
     * @param model      Spring Model to pass data to the view
     * @return 'purchase-history' view template showing the purchase history of customer
     *
     * @author: Muthu Raj
     * @date: 2025/10/02
     *
     */
    @GetMapping("/history")
    public String getOrderHistory(HttpSession sessionObj, Model model) {
        Integer customerId = (Integer) sessionObj.getAttribute("id");
        List<Order> orders = orderService.getOrdersByCustomerWithDetails(customerId);
        model.addAttribute("orders", orders);
        return "purchase-history";
    }

    /**
     * Display the order detail page for a specific order item ensuring the customer owns it.
     *
     *
     * @param orderId the unique ID of the order
     * @param productId  the unique ID of the product within the order
     * @param sessionObj Session object to retrieve the logged-in customer's identifier
     * @param model      Spring Model to pass data to the view
     * @return 'purchased-product-details' view template showing the order details
     *
     * @author: Muthu Raj
     * @date: 2025/10/02
     *
     */
    @GetMapping("/{orderId}/order-item/{productId}")
    public String getOrderDetails(@PathVariable int orderId,
                                  @PathVariable int productId,
                                  HttpSession sessionObj,
                                  Model model) {
        Integer customerId = (Integer) sessionObj.getAttribute("id");
        String role = (String) sessionObj.getAttribute("role");
        if (customerId == null || !CUSTOMER_ROLE.equals(role)) {
            return "redirect:/login";
        }
        OrderItemId id = new OrderItemId(orderId, productId);
        OrderItem orderItem = oiService.getOrderItemById(id);

        if (orderItem.getOrder() == null || orderItem.getOrder().getCustomer() == null
                || orderItem.getOrder().getCustomer().getId() != customerId) {
            return "redirect:/login";
        }
        model.addAttribute("orderItem", orderItem);
        return "purchased-product-details";
    }
}

package javaee.group3.sa61.shoppingcart.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javaee.group3.sa61.shoppingcart.model.*;
import javaee.group3.sa61.shoppingcart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.CheckoutInterface;
import javaee.group3.sa61.shoppingcart.repository.CartItemRepository;
import javaee.group3.sa61.shoppingcart.repository.OrderRepository;

@Service
@Transactional
public class CheckoutImplementation implements CheckoutInterface {

    private static final DateTimeFormatter ORDER_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final OrderRepository orderRepository;
    private final CartInterface cartService;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    public CheckoutImplementation(OrderRepository orderRepository,
                                  CartInterface cartService,
                                  CartItemRepository cartItemRepository,ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * Convert a cart into a persisted order and clear its cart items.
     *
     * @param cartId        identifier of the cart to check out
     * @param paymentMethod customer's selected payment option
     * @return newly created order or null if checkout cannot be completed
     * @throws IllegalStateException if any product has insufficient stock
     * @author Sujitha, Huang Jun
     * @date 2025/10/8
     */
    @Override
    @Transactional
    public Order processCheckout(int cartId, String paymentMethod) {
        Cart cart = cartService.getCartByCartId(cartId);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return null;
        }
        if (cart.getCustomer() == null) {
            return null;
        }

        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setOrderDate(LocalDateTime.now().format(ORDER_TIME_FORMATTER));
        order.setPaymentMethod(paymentMethod);
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();


        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            // Reduce stock quantity
            int remainingStock = product.getStockQty() - cartItem.getQuantity();
            if (remainingStock < 0) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQty(remainingStock);
            productRepository.save(product);
            if (remainingStock <= 0) {
                cartItemRepository.deleteByProductId(product.getId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPurchaseUnitPrice(cartItem.getProduct().getUnitPrice());
            orderItem.setPurchaseName(cartItem.getProduct().getName());
            orderItem.setPurchaseDescription(cartItem.getProduct().getDescription());
            orderItem.setPurchaseCategoryName(cartItem.getProduct().getCategory().getName());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());
        cart.setCartItems(new ArrayList<>());
        cartService.saveCart(cart);

        return savedOrder;
    }
}

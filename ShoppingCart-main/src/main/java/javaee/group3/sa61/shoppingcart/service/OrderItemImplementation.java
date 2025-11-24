package javaee.group3.sa61.shoppingcart.service;

import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.OrderItemInterface;
import javaee.group3.sa61.shoppingcart.model.OrderItem;
import javaee.group3.sa61.shoppingcart.model.OrderItemId;
import javaee.group3.sa61.shoppingcart.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderItemImplementation implements OrderItemInterface {

    @Autowired
    private OrderItemRepository orderItemRepo;

    /**
     * Find a orderItem by its ID
     *
     * @param orderItemId The ID of the orderItem to find
     * @return The orderItem with the given ID, or null if not found
     */
    @Override
    public OrderItem getOrderItemById(OrderItemId orderItemId){
        return orderItemRepo.findById(orderItemId)
            .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));
    }

    /**
     * Save orderItem
     *
     * @param orderItem The orderItem to be saved
     */
    @Override
    public void saveOrderItem(OrderItem orderItem) {
        orderItemRepo.save(orderItem);
    }

}

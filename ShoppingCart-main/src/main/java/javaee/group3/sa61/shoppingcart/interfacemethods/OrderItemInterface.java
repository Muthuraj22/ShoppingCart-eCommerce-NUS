package javaee.group3.sa61.shoppingcart.interfacemethods;

import javaee.group3.sa61.shoppingcart.model.OrderItem;
import javaee.group3.sa61.shoppingcart.model.OrderItemId;

public interface OrderItemInterface {

    /**
     * Retrieve a single order item using its composite ID.
     *
     * @param orderItemId the composite ID of the order item
     * @return OrderItem corresponding to the given ID
     */
    OrderItem getOrderItemById(OrderItemId orderItemId);

    /**
     * Save an order item.
     *
     * @param orderItem to be persisted
     */
    void saveOrderItem(OrderItem orderItem);

}

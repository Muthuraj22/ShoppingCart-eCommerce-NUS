package javaee.group3.sa61.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import javaee.group3.sa61.shoppingcart.model.OrderItem;
import javaee.group3.sa61.shoppingcart.model.OrderItemId;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
}

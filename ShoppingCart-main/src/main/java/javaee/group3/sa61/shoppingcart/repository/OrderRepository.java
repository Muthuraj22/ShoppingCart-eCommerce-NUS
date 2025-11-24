package javaee.group3.sa61.shoppingcart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javaee.group3.sa61.shoppingcart.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Query("select distinct o from Order o left join fetch o.orderItems oi left join fetch oi.product left join fetch o.customer order by o.orderDate desc")
	List<Order> findAllWithDetails();

	@Query("select o from Order o left join fetch o.orderItems oi left join fetch oi.product left join fetch o.customer where o.id = :orderId")
	Order findDetailById(@Param("orderId") int orderId);

	@Query("select distinct o from Order o left join fetch o.orderItems oi left join fetch oi.product left join fetch o.customer where o.customer.id = :customerId order by o.orderDate desc")
	List<Order> findByCustomerIdWithDetails(@Param("customerId") int customerId);

	List<Order> findByCustomerIdOrderByOrderDateDesc(int customerId);

	List<Order> findAllByOrderByOrderDateDesc();
}

package javaee.group3.sa61.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javaee.group3.sa61.shoppingcart.interfacemethods.*;
import javaee.group3.sa61.shoppingcart.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javaee.group3.sa61.shoppingcart.model.Customer;
import javaee.group3.sa61.shoppingcart.model.Order;
import javaee.group3.sa61.shoppingcart.model.OrderItem;
import javaee.group3.sa61.shoppingcart.model.OrderItemId;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.repository.CategoryRepository;
import javaee.group3.sa61.shoppingcart.repository.CustomerRepository;
import javaee.group3.sa61.shoppingcart.repository.OrderItemRepository;
import javaee.group3.sa61.shoppingcart.repository.OrderRepository;
import javaee.group3.sa61.shoppingcart.repository.ProductRepository;

/**
 * Test class for UserOrderInterface.
 * This class tests the functionality of retrieving a customer's purchase history with order details.
 *
 * @author Muthu Raj
 */

@SpringBootTest
public class UserOrderInterfaceTest {


    @Autowired
    ProductInterface pservice;

    @Autowired
    CustomerInterface custservice;

    @Autowired
    OrderItemInterface oiservice;

    @Autowired
    OrderInterface oservice;

    @Autowired
    CategoryInterface catservice;

    @Autowired
    public void setProductService(ProductImplementation pserviceImpl) {
        this.pservice = pserviceImpl;
    }

    @Autowired
    public void setCustomerService(CustomerImplementation custserviceImpl) {
        this.custservice = custserviceImpl;
    }

    @Autowired
    public void setOrderItemService(OrderItemImplementation oiserviceImpl) {
        this.oiservice = oiserviceImpl;
    }

    @Autowired
    public void setOrderService(OrderImplementation oserviceImpl) {
        this.oservice = oserviceImpl;
    }

    @Autowired
    public void setCategoryService(CategoryImplementation catserviceImpl) {
        this.catservice = catserviceImpl;
    }


    @Test
		public void purchaseHistoryTest() {

			Product product1 = pservice.findProductById(2);
			Product product2 = pservice.findProductById(8);

            Customer cust = new Customer();
            cust.setName("Chia");
            cust.setEmailAddress("chia@gmail.com");
            cust.setRole("customer");
            cust.setUsername("chia");
            cust.setPassword("password");
            cust.setAddress("Singapore");
            cust.setPhoneNumber("84562137");
            custservice.saveCustomer(cust);

			Order order = new Order();
			order.setOrderDate("2025-10-15");
			order.setCustomer(cust);
			oservice.saveOrder(order);

			OrderItemId orderItemId = new OrderItemId(order.getId(), product1.getId());
	        OrderItem orderItem = new OrderItem();
	        orderItem.setOrderItemId(orderItemId);
	        orderItem.setOrder(order);
	        orderItem.setProduct(product1);
	        orderItem.setQuantity(2);
	        orderItem.setPurchaseUnitPrice(product1.getUnitPrice());
            orderItem.setPurchaseName(product1.getName());
            orderItem.setPurchaseCategoryName(product1.getCategory().getName());
	        oiservice.saveOrderItem(orderItem);
	        
	        OrderItemId orderItemId2 = new OrderItemId(order.getId(), product2.getId());
	        OrderItem orderItem2 = new OrderItem();
	        orderItem2.setOrderItemId(orderItemId2);
	        orderItem2.setOrder(order);
	        orderItem2.setProduct(product2);
	        orderItem2.setQuantity(5);
	        orderItem2.setPurchaseUnitPrice(product2.getUnitPrice());
            orderItem2.setPurchaseName(product2.getName());
            orderItem2.setPurchaseCategoryName(product2.getCategory().getName());
            oiservice.saveOrderItem(orderItem2);

	        List<Order> orders = oservice.getOrdersByCustomerWithDetails(cust.getId());

	        assertThat(orders).isNotEmpty();
	        assertThat(orders.get(0).getOrderItems()).hasSize(2);
	        assertThat(orders.get(0).getOrderItems().get(0).getProduct().getName()).isEqualTo(product1.getName());
	        assertThat(orders.get(0).getOrderItems().get(0).getQuantity()).isEqualTo(2);
		
	    }

}

package javaee.group3.sa61.shoppingcart.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class OrderItem {

    private Double purchaseUnitPrice;
    private int quantity;
    
    private String purchaseName;
    private String purchaseDescription;
    private String purchaseCategoryName;


    @EmbeddedId
    private OrderItemId orderItemId;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name="order_id", referencedColumnName = "id")
    private Order order;

    public OrderItem() {}

    public OrderItem(int quantity, Order order, Product product) {
        this.orderItemId = new OrderItemId(order.getId(), product.getId());
        this.quantity = quantity;
        this.order = order;
        this.product = product;
        this.purchaseUnitPrice = product.getUnitPrice();
        this.purchaseName = product.getName();
        this.purchaseDescription = product.getDescription();
        this.purchaseCategoryName = (product.getCategory()).getName();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if(this.orderItemId==null)
            this.orderItemId=new OrderItemId();
        this.orderItemId.setProductId(product.getId());
    }

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
        if(this.orderItemId==null)
            this.orderItemId=new OrderItemId();
        this.orderItemId.setOrderId(order.getId());
    }

    public Double getPurchaseUnitPrice() {
        return purchaseUnitPrice;
    }
    public void setPurchaseUnitPrice(Double purchaseUnitPrice) {
        this.purchaseUnitPrice = purchaseUnitPrice;
    }
    
    public String getPurchaseName() {
        return purchaseName;
    }
    public void setPurchaseName(String purchaseName) {
        this.purchaseName = purchaseName;
    }
    
    public String getPurchaseDescription() {
        return purchaseDescription;
    }
    public void setPurchaseDescription(String purchaseDescription) {
        this.purchaseDescription = purchaseDescription;
    }
    
    public String getPurchaseCategoryName() {
        return purchaseCategoryName;
    }
    public void setPurchaseCategoryName(String purchaseCategoryName) {
        this.purchaseCategoryName = purchaseCategoryName;
    }
    
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }
}
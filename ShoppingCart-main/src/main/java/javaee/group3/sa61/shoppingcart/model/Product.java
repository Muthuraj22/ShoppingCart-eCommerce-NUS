package javaee.group3.sa61.shoppingcart.model;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_category", columnList = "category_id")
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank (message = "Product name is required")
    @Size (min = 1, max = 35,
            message = "Name length must be between 1-35 characters")
    private String name;

    @Size (max = 200,
            message = "Description can not be longer than 200 characters")
    private String description;

    @NotNull (message = "Unit Price is required")
    @Positive (message = "Unit price must be a positive number")
    @Max (value = 999999, message = "Unit price can not be larger than 999999")
    private Double unitPrice;

    @Min (value = 0, message = "Stock can not be a negative number")
    @Max (value = 999999, message = "Stock can not be larger than 999999")
    private int stockQty;

    @Size (max = 512,
            message = "Image path can not be longer than 512 characters")
    private String imagePath;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active = true;

    @OneToMany(mappedBy="product")
    private List<CartItem> cartItems;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy="product")
    private List<OrderItem> orderItems;

    public Product() {}

    public Product(String name, String description,
                   Double unitPrice, int stockQty,
                   Category category, String imagePath) {
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.stockQty = stockQty;
        this.category = category;
        this.imagePath = imagePath;
    }

    public Product(String name, String description,
                   Double unitPrice, int stockQty) {
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.stockQty = stockQty;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public int getStockQty() {
        return stockQty;
    }
    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

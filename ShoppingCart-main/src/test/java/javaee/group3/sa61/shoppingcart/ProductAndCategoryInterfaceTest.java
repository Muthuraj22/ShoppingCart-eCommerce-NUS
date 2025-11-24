package javaee.group3.sa61.shoppingcart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javaee.group3.sa61.shoppingcart.interfacemethods.CategoryInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.ProductInterface;
import javaee.group3.sa61.shoppingcart.model.Category;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.service.CategoryImplementation;
import javaee.group3.sa61.shoppingcart.service.ProductImplementation;

/**
 * Test class for ProductInterface and CategoryInterface.
 * This class tests the functionality of saving Products and Categories.
 *
 * @author Yue-Sheng, Huang Jun
 */

@SpringBootTest
public class ProductAndCategoryInterfaceTest {
	@Autowired
	private ProductInterface pservice;
	
	@Autowired
	public void setProductService(ProductImplementation pserviceImpl) {
		this.pservice = pserviceImpl;
	}
	
	@Autowired
	private CategoryInterface catservice;
	
	@Autowired
	public void setProductService(CategoryImplementation catserviceImpl) {
		this.catservice = catserviceImpl;
	}

    @Test
    public void testSaveCustomerAndCategory() {

        // ===== Categories (original code unchanged) =====
        Category tech = new Category();
        tech.setName("Technology");
        tech.setDescription("Technology products and gadgets");

        Category gff = new Category();
        gff.setName("Fresh Food");
        gff.setDescription("Fresh fruits and vegetables");

        Category furniture = new Category();
        furniture.setName("Furniture");
        furniture.setDescription("Home and office furniture");

        // New categories
        Category fashion = new Category();
        fashion.setName("Fashion");
        fashion.setDescription("Clothing and accessories");

        Category sports = new Category();
        sports.setName("Sports");
        sports.setDescription("Sports equipment and apparel");

        Category beauty = new Category();
        beauty.setName("Beauty");
        beauty.setDescription("Cosmetics and skincare products");

        // Add a new category for snacks which is always empty, for demo purposes.
        Category snack = new Category();
        snack.setName("Snacks");
        snack.setDescription("Tasty snacks and treats");

        // Keep your category save logic exactly
        if (!catservice.saveCategory(tech)) {
            System.out.println(tech.getName() + ": Category with the same name already exists.");
            tech = catservice.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase("Technology"))
                    .findFirst()
                    .orElse(null);
        }

        if (!catservice.saveCategory(gff)) {
            System.out.println(gff.getName() + ": Category with the same name already exists.");
            gff = catservice.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase("Fresh Food"))
                    .findFirst()
                    .orElse(null);
        }

        if (!catservice.saveCategory(furniture)) {
            System.out.println(furniture.getName() + ": Category with the same name already exists.");
            furniture = catservice.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase("Furniture"))
                    .findFirst()
                    .orElse(null);
        }

        if (!catservice.saveCategory(fashion)) {
            System.out.println(fashion.getName() + ": Category with the same name already exists.");
            fashion = catservice.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase("Fashion"))
                    .findFirst()
                    .orElse(null);
        }

        if (!catservice.saveCategory(sports)) {
            System.out.println(sports.getName() + ": Category with the same name already exists.");
            sports = catservice.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase("Sports"))
                    .findFirst()
                    .orElse(null);
        }

        if (!catservice.saveCategory(beauty)) {
            System.out.println(beauty.getName() + ": Category with the same name already exists.");
            beauty = catservice.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase("Beauty"))
                    .findFirst()
                    .orElse(null);
        }

        if (!catservice.saveCategory(snack)) {
            System.out.println(snack.getName() + ": Category with the same name already exists.");
            snack = catservice.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase("Snacks"))
                    .findFirst()
                    .orElse(null);
        }

        // ===== Products grouped by category =====

        // Technology (existing products keep image URLs)
        pservice.saveProduct(new Product("Surface Pro 2025 512GB",
                "2025 Microsoft Surface Laptop with 2TB storage/32GB Ram.",
                2099.00, 10, tech,
                "https://msftstories.thesourcemediaassets.com/sites/427/2025/05/Surface-Laptop-13-inch-Ocean-back-side1920-1-1024x576-1.jpg"));
        pservice.saveProduct(new Product("iPhone 15 Pro Max",
                "Apple iPhone 15 Pro Max with 1TB storage.",
                1599.00, 20, tech,
                "https://cdsassets.apple.com/live/7WUAS350/images/tech-specs/iphone-15-pro-max.png"));
        pservice.saveProduct(new Product("Samsung Galaxy S24 Ultra",
                "Samsung Galaxy S24 Ultra with 1TB storage.",
                1399.00, 15, tech,
                "https://www.dolby.com/siteassets/xf-products/mobile-phones/galaxy-s24-ultra/samsung_sm-s928bztheue-m_int_1.jpg"));
        pservice.saveProduct(new Product("iPad (2025) 64GB",
                "Latest entry level tablet for Apple's 2025 Lineup.",
                599.00, 12, tech, "https://m.media-amazon.com/images/I/616FFqht2sL._UF894,1000_QL80_.jpg"));

        // Fresh Food
        pservice.saveProduct(new Product("Organic Bananas",
                "Fresh organic bananas from local farms.",
                1.29, 100, gff,
                "https://media.cnn.com/api/v1/images/stellar/prod/120604032828-fresh-ripe-bananas.jpg?q=w_3590,h_2774,x_0,y_0,c_fill"));
        pservice.saveProduct(new Product("Almond Milk",
                "Unsweetened almond milk, 1 gallon.",
                3.49, 50, gff,
                "https://lh6.googleusercontent.com/proxy/A1nJUL-u5TmlbKmETxknWWSFFIPDQdqM_5tBbV7r0Ryz-VTWQJtgTdPxCEzOlYv2DDn8Fl-0iIL5bFumkb2y1RpfguBGmFXcSNVDtie3zPODy-PkEWaVWkZ55EeoIoKrHD5bGFzN5wwBa9OakMjMdg"));
        pservice.saveProduct(new Product("Whole Wheat Bread",
                "Freshly baked whole wheat bread.",
                2.99, 30, gff,
                "https://kikifoodies.com/wp-content/uploads/2025/02/25971753-11CB-4ACA-AC07-10E258901BF4.jpeg"));
        pservice.saveProduct(new Product("Fresh Strawberries",
                "Sweet and juicy strawberries.",
                5.99, 40, gff, "https://highcor.com/wp-content/uploads/2017/08/H116-strawberry-1.jpg"));

        // Furniture
        pservice.saveProduct(new Product("Ergonomic Office Chair",
                "Comfortable ergonomic office chair with lumbar support.",
                199.99, 25, furniture,
                "https://flujostore.com/cdn/shop/files/flujo-pluto-chair-red-mesh-headrest-singapore.webp?v=1730093691&width=1000"));
        pservice.saveProduct(new Product("Wooden Dining Table",
                "Solid wood dining table that seats six.",
                499.99, 10, furniture,
                "https://myseat.sg/wp-content/uploads/2023/05/Rondeau_WB-2.jpg"));
        pservice.saveProduct(new Product("Bookshelf",
                "Spacious wooden bookshelf for home or office.",
                149.99, 20, furniture, "https://hipvan-images-production.imgix.net/product-images/2e8f98eb-2154-4345-9f22-358eef66e616/HV-Atelier-Storage---Accent-Furniture--Hibana-Bookshelf-0-5m-6.png?auto=format%2Ccompress&fm=jpg&cs=srgb&ar=1%3A1&bg=fff&fit=fill&w=650&ixlib=react-9.10.0")); // new product, empty image
        pservice.saveProduct(new Product("Office Desk",
                "Sturdy office desk with drawers.",
                299.99, 15, furniture, "https://img.myshopline.com/image/store/1661737517256/JIM-4-FT-OFFICE-TABLE.jpg?w=1024&h=1024"));

        // Fashion (all new products, empty image)
        pservice.saveProduct(new Product("Designer Handbag",
                "Stylish handbag for daily use.",
                199.99, 20, fashion, "https://www.marinabaysands.com/guides/luxurious-indulgence/luxury-bag-brands/_jcr_content/root/container/table_1597414286_cop_1800323295/1-1/image.coreimg.jpeg/1730792786883/cartier-red-bag.jpeg"));
        pservice.saveProduct(new Product("Leather Jacket",
                "Premium leather jacket.",
                299.99, 15, fashion, "https://www.mrporter.com/variants/images/46376663162903425/in/w358_q60.jpg"));
        pservice.saveProduct(new Product("Puma Sneakers",
                "Comfortable running sneakers.",
                129.99, 25, fashion, "https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa,w_2000,h_2000/global/393490/07/sv01/fnd/SEA/fmt/png/CA-Pro-OW-Sneakers"));
        pservice.saveProduct(new Product("Sunglasses",
                "UV-protective sunglasses.",
                79.99, 30, fashion, "https://eu-images.contentstack.com/v3/assets/blt7dcd2cfbc90d45de/bltca0defe0e9c1b630/67b844be273493d43a833ed1/28513-1.jpg?format=pjpg&auto=webp&quality=75%2C90&width=3840"));

        // Sports
        pservice.saveProduct(new Product("Soccer Ball",
                "Professional soccer ball for matches.",
                49.99, 50, sports, "https://m.media-amazon.com/images/I/61IkrxQ9p8L.jpg"));
        pservice.saveProduct(new Product("Tennis Racket",
                "Lightweight tennis racket.",
                99.99, 20, sports, "https://i5.walmartimages.com/seo/Wilson-Tour-Slam-Lite-Adult-Tennis-Racket-Grip-Size-3-113-Square-Inch-Head-Blue_97600f2e-818f-4720-ad22-8cb915580a62_1.da3c710586195854920e7a5520e0cc7e.jpeg"));
        pservice.saveProduct(new Product("Yoga Mat",
                "Non-slip yoga mat.",
                29.99, 40, sports, "https://contents.mediadecathlon.com/p2940271/k$59e73f3bc780d9d6bae3dddb2f4f0958/beginner-yoga-mat-180-cm-x-50-cm-x-5-mm-brown-domyos-8928286.jpg"));
        pservice.saveProduct(new Product("Basketball",
                "High-performance basketball.",
                20.99, 25, sports, "https://contents.mediadecathlon.com/p2705092/k$34bfd1a1787dcb27067366160c047e32/basketball-bt900-size-7fiba-approved-for-boys-and-adults-tarmak-8648080.jpg?f=1920x0&format=auto"));

        // Beauty
        pservice.saveProduct(new Product("Face Cream",
                "Hydrating face cream for all skin types.",
                39.99, 60, beauty, "https://www.fresh.com/dw/image/v2/BDJQ_PRD/on/demandware.static/-/Sites-fresh_master_catalog/default/dw7d36881f/product_images/H00006117_packshot.png?sw=800&sh=800&bgcolor=F7F7F8&sfrm=png"));
        pservice.saveProduct(new Product("Perfume",
                "Fragrant perfume for daily wear.",
                59.99, 30, beauty, "https://cottonon.com/dw/image/v2/BBDS_PRD/on/demandware.static/-/Sites-catalog-master-rubishoes/default/dw4182402e/4592124/4592124-16-2.jpg?sw=640&sh=960&sm=fit"));
        pservice.saveProduct(new Product("Shampoo",
                "Gentle daily-use shampoo.",
                12.99, 70, beauty, "https://m.media-amazon.com/images/I/616CZQi3zSL._UF1000,1000_QL80_.jpg"));
    }

}

	
	


# ShoppingCart

ShoppingCart is a Spring Boot web application produced by SA61 Group 3. 
It implements an end-to-end e-commerce flow covering catalogue
browsing, shopping cart management, checkout, and an administrative back
office. The project uses Thymeleaf for the server-rendered UI, exposes JSON
APIs for external clients, and persists data to MySQL via Spring Data JPA.

While we also built a React/Vite login page to demonstrate the JSON APIs, 
so you might want to clone the [ShoppingCartReact](https://github.com/ysriser/ShoppingCartReact)
to get the full experience.

## Features

### Customer experience
- Browse the full product catalogue with category filters and keyword search.
- View rich product detail pages, including stock counts and related actions.
- Add, reduce, or remove items from the session-backed shopping cart.
- Review cart summaries and perform checkout with payment method capture.
- See purchase history and drill into per-item order receipts.

### Admin experience
- Role-restricted admin dashboard at `/admin` with shortcuts to management
  modules.
- Maintain the product catalogue: create, edit, search, and soft validate
  category selections before persisting.
- Manage categories with duplicate detection, edit validation, and safe
  deletes that guard against orphaned products.
- Audit all customer accounts, carts, and historical orders through REST-backed
  admin views.
- Inspect every order with totals and line items, alongside a live customer
  summary.

### Shared REST APIs
- Session-based login API (`POST /api/login`, `GET /api/login`) that powers the
  server-rendered UI and any external SPA (CORS enabled for
  `http://localhost:5173`).
- Customer self-service registration (`POST /api/customer/save`).
- Admin data services under `/admin/api/**` for customers, carts, and orders.

## Architecture

- **Spring Boot 3.5.6** with Maven build tooling.
- **Java 17** source compatibility.
- **Spring MVC** with Thymeleaf templates for server-rendered pages.
- **Spring Data JPA** repositories for persistence and query abstraction.
- **Spring Session JDBC** stores HTTP sessions in MySQL (table `SPRING_SESSION`
  is auto-created on start-up).
- **Layered design**: controllers → service interfaces/implementations →
  repositories → entities.
- **Role-based access** enforced by `LoginInterceptor`, redirecting users when
  they attempt to cross customer/admin boundaries.
- **Integration tests** in `src/test/java` exercise repositories and services
  against a configured MySQL instance.

## Project layout

```text
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java/javaee/group3/sa61/shoppingcart
│   │   │   ├── ShoppingCartApplication.java
│   │   │   ├── config/           (Web configuration, interceptors, beans)
│   │   │   ├── controller/       (Customer, admin, and REST controllers)
│   │   │   ├── interceptor/      (LoginInterceptor)
│   │   │   ├── interfacemethods/ (Service interfaces)
│   │   │   ├── model/            (JPA entities and value objects)
│   │   │   ├── repository/       (Spring Data repositories)
│   │   │   └── service/          (Business logic implementations)
│   │   └── resources
│   │       ├── templates/        (Thymeleaf pages & fragments)
│   │       ├── static/           (Images, CSS)
│   │       └── application.properties
│   └── test/java/javaee/group3/sa61/shoppingcart
│       └── …                     (Repository/service integration tests)
└── target                        (Build output)
```

## Prerequisites

- Java Development Kit 17
- Apache Maven 3.9 or newer
- MySQL 8.x (running on `localhost:3306` or adjust configuration)
- (Optional) A SPA frontend running at `http://localhost:5173` to consume the
  JSON APIs

## Configuration

Primary configuration lives in `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=Asia/Singapore
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.session.jdbc.initialize-schema=always
```

Override sensitive values via environment variables instead of committing local
changes. Example on PowerShell:

```powershell
$Env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=Asia/Singapore"
$Env:SPRING_DATASOURCE_USERNAME = "root"
$Env:SPRING_DATASOURCE_PASSWORD = "password"
```

Other useful overrides:

- `SPRING_JPA_HIBERNATE_DDL_AUTO=create` to rebuild schema from scratch during
  a clean set-up.
- `SERVER_PORT` when you need a different HTTP port.
- `SHOPPING_CART_LOGOUT_REDIRECT` is not configurable yet; update
  `LoginController.logout` if you host the SPA elsewhere.

## Database bootstrap

1. Create the schema:
   ```sql
   CREATE DATABASE IF NOT EXISTS ecommerce
     CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;
   ```
2. Grant the configured MySQL user privileges on the schema.
3. Start the application once; Spring Session will create `SPRING_SESSION`
   tables automatically.
4. Seed baseline users. Passwords are currently stored in clear text.
   ```sql
   INSERT INTO admin (name, username, password, phoneNumber, emailAddress, role)
   VALUES ('Store Admin', 'admin', 'admin123', '00000000', 'admin@example.com', 'admin');

   INSERT INTO customer (name, username, password, phoneNumber, emailAddress, role, address)
   VALUES ('Alice', 'alice', 'password123', '12345678', 'alice@example.com', 'customer', '123 Sample Street');
   ```
5. Optionally run `mvn test` (see below) to populate sample categories and
   products through the integration tests.

## Running the application

```bash
mvn clean spring-boot:run
```

By default the application listens on `http://localhost:8080`.

Again, this project works better with our React framing Login app. 
Click [this link](https://github.com/ysriser/ShoppingCartReact) will lead you to its repo. 

- Customer login page: `http://localhost:8080/login`
- Admin landing: `http://localhost:8080/admin`
- REST login endpoint: `POST http://localhost:8080/api/login`

To build a runnable jar:

```bash
mvn clean package
java -jar target/ShoppingCart-0.0.1-SNAPSHOT.jar
```

## Test suite

Execute all tests with:

```bash
mvn test
```

The integration tests expect the MySQL datasource configured in
`application.properties`. They insert and update data in the configured schema,
so run them against an isolated database to avoid polluting production data.

## HTTP entry points

| Audience | Method | Path | Purpose |
|----------|--------|------|---------|
| Public   | GET    | `/login` | Render combined customer/admin login form. |
| Public   | POST   | `/login` | Authenticate and establish session. |
| Public   | POST   | `/api/login` | JSON login for SPAs, responds with role. |
| Public   | GET    | `/api/login` | Returns the active session role. |
| Public   | POST   | `/api/customer/save` | Register a new customer account. |
| Customer | GET    | `/products` | Product catalogue with filters. |
| Customer | GET    | `/products/{id}` | Detailed product view. |
| Customer | POST   | `/cart/items/add` | Add an item to the cart. |
| Customer | POST   | `/cart/items/reduce` | Decrease quantity by one. |
| Customer | POST   | `/cart/items/delete` | Remove a product from the cart. |
| Customer | GET    | `/cart` | Show cart summary. |
| Customer | GET    | `/orders/checkout` | Render checkout form. |
| Customer | POST   | `/orders/checkout` | Submit checkout and create order. |
| Customer | GET    | `/orders/history` | Purchase history list. |
| Customer | GET    | `/orders/{orderId}/order-item/{productId}` | Order line detail. |
| Admin    | GET    | `/admin` | Admin home dashboard. |
| Admin    | GET    | `/products/admin/maintain` | Product maintenance UI. |
| Admin    | GET    | `/category/admin/maintain` | Category maintenance UI. |
| Admin    | GET    | `/admin/orders` | Order audit view. |
| Admin    | GET    | `/admin/customers` | Customer management view. |
| Admin API| GET    | `/admin/api/orders` | JSON list of all orders. |
| Admin API| GET    | `/admin/api/orders/{orderId}` | Order detail payload. |
| Admin API| GET    | `/admin/api/customers` | JSON list of customers. |
| Admin API| GET    | `/admin/api/customers/{customerId}` | Customer detail payload. |

## Frontend integration

`WebAppConfig` enables CORS for `/api/**` endpoints to
`http://localhost:5173`. When building a React/Vite SPA for customers:

1. Use `POST /api/login` and `POST /api/customer/save` for authentication and
   registration.
2. Forward cookies returned by the Spring Boot server; the session is stored in
   MySQL so it can be shared with the Thymeleaf UI if required.
3. Call the admin APIs from authenticated browser sessions; the controllers
   forward cookies when they invoke the REST layer internally.

## Known limitations and follow-up ideas

- Passwords are stored in clear text; integrate Spring Security with hashing
  before deploying to production.
- Product management currently validates categories inline; consider adding a
  dedicated modal to create categories from the product form (see TODOs in
  `CategoryController` and `ProductController`).
- Login redirects are hard-coded (`/login`, `/products`, `/admin`, SPA logout
  URL). Extract them to configuration when customising deployments.
- Error handling for REST controllers mainly returns 404/409; expand with more
  descriptive payloads and validation responses as the API surface grows.
- There is no automated data migration. Adopt Flyway or Liquibase for
  repeatable schema evolution.

## Contributors

- NUS-ISS SA61 Group 3

Feel free to open issues or submit pull requests as you continue to refine the
application.

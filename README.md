# ShopSphere – Backend

## Project Overview

ShopSphere is a full-stack e-commerce application backend developed using Java, Spring Boot and PostgreSQL.

It provides REST APIs for user authentication, product management, shopping cart, wishlist, address management, order processing, payments and product reviews.

## Technologies Used

- Java 21
- Spring Boot 4.1.2
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- PostgreSQL 18
- Maven

## Features

- User registration and login
- JWT-based authentication
- Password encryption using BCrypt
- Product management
- Shopping cart management
- Wishlist management
- Address management
- Order placement and tracking
- Order cancellation
- Payment processing simulation
- Product reviews
- REST API development

## Project Structure

```text
shopsphere/
├── src/
│   ├── main/
│   │   ├── java/com/shopsphere/
│   │   │   ├── address/
│   │   │   ├── cart/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── order/
│   │   │   ├── payment/
│   │   │   ├── repository/
│   │   │   ├── review/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   ├── user/
│   │   │   └── wishlist/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw
└── .gitignore
```

## Database Configuration

Create a PostgreSQL database:

```sql
CREATE DATABASE shopsphere_db;
```

Configure the following environment variables:

```text
DB_URL=jdbc:postgresql://localhost:5432/shopsphere_db
DB_USERNAME=postgres
DB_PASSWORD=your_database_password
SHOPSPHERE_JWT_SECRET=your_secure_jwt_secret
```

Never commit actual passwords or JWT secrets to GitHub.

## How to Run the Backend

**1. Clone the repository**

```bash
git clone https://github.com/vasundhara2509/ShopSphere-Backend.git
```

**2. Navigate to the project**

```bash
cd ShopSphere-Backend
```

**3. Configure the environment variables**

Set the database credentials and JWT secret before starting the application.

**4. Run the application**

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

**5. Verify the application**

```text
http://localhost:8080/api/health
```

Expected response:

```text
shopsphere backend is running successfully
```

## API Endpoints

| Module | Endpoint |
|---|---|
| Health | `/api/health` |
| Authentication | `/api/auth/register` |
| Login | `/api/auth/login` |
| Products | `/api/products` |
| Cart | `/api/cart` |
| Addresses | `/api/addresses` |
| Orders | `/api/orders` |
| Payments | `/api/payments` |
| Wishlist | `/api/wishlist` |
| Reviews | Review API endpoints |

Most endpoints use JSON requests and responses. Protected endpoints require JWT authentication.

## Payment Information

The current application implements simulated payment processing. It does not connect to a real payment gateway or charge actual money.

## GitHub Repository

[ShopSphere Backend](https://github.com/vasundhara2509/ShopSphere-Backend)

## Author

Developed as a full-stack e-commerce application project. 
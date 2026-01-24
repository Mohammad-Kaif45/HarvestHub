# 🌾 HarvestHub

**HarvestHub** is a comprehensive agricultural e-commerce platform designed to bridge the gap between farmers, retailers, and wholesalers. It facilitates secure trading of crops with role-based access, real-time OTP verification, and integrated payment gateways.

---

## 🚀 Key Features

* **Role-Based Access Control (RBAC):** Distinct dashboards and workflows for **Farmers** (sellers), **Retailers** (buyers), and **Wholesalers**.
* **Secure Authentication:**
    * User registration with **Email OTP Verification** (via SMTP).
    * Encrypted passwords using **BCrypt**.
    * Protection against unauthorized access using **Spring Security**.
* **Payment Integration:**
    * **Razorpay Integration** for real-time transactions.
    * **Mock Payment Fallback** system to ensure testing continuity during API downtimes.
* **Order Management:** Automated order creation and status tracking upon successful payment.
* **Cloud-Native Architecture:**
    * Deployed on **Render** for high availability.
    * Data stored in a distributed **TiDB Cloud (MySQL)** database.
* **Enhanced Security:** Sensitive credentials (Database & Email) secured using **Environment Variables**.

---

## 🛠️ Tech Stack

* **Backend:** Java 21, Spring Boot 3.5, Spring Security, Hibernate
* **Database:** MySQL (TiDB Cloud)
* **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
* **Payment:** Razorpay API
* **Tools:** Maven, Git, IntelliJ IDEA

---

## ⚙️ Installation & Setup

Follow these steps to run the project locally.

### 1. Clone the Repository

git clone [https://github.com/YOUR_USERNAME/HarvestHub.git](https://github.com/YOUR_USERNAME/HarvestHub.git)
cd HarvestHub


### 2. Configure Environment Variables
For security, this project uses environment variables. You must set these in your IDE (IntelliJ) or System Environment:

| Variable Name | Description | Example Value |
| :--- | :--- | :--- |
| `DB_URL` | JDBC URL for MySQL/TiDB | `jdbc:mysql://gateway01...` |
| `DB_USER` | Database Username | `root` |
| `DB_PWD` | Database Password | `your_secure_password` |
| `MAIL_PWD` | Google App Password | `abcdefghijklmnop` |

### 3. Update `application.properties`
Ensure your `src/main/resources/application.properties` references these variables:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PWD}
spring.mail.password=${MAIL_PWD}


```

For live Demo you can checkout : https://harvesthub-pqse.onrender.com

# 🛡️ PicBank Auth Service

![GitHub repo size](https://img.shields.io/github/repo-size/antoniocesarlopes/picbank-auth-service)
![GitHub last commit](https://img.shields.io/github/last-commit/antoniocesarlopes/picbank-auth-service)
![GitHub issues](https://img.shields.io/github/issues/antoniocesarlopes/picbank-auth-service)
![GitHub license](https://img.shields.io/github/license/antoniocesarlopes/picbank-auth-service)

The **PicBank Auth Service** is a microservice responsible for user authentication and authorization using **AWS Cognito**. It follows the **API First** approach, is well-documented with **SpringDoc OpenAPI**, and supports **JWT-based authentication**.

---

## 📌 **Table of Contents**  
- [📜 About the Project](#-about-the-project)  
- [🚀 Features](#-features)  
- [🛠️ Tech Stack](#-tech-stack)
- [📂 Project Structure](#-project-structure)
- [⚙️ Environment Variables](#-environment-variables)
- [🏃 How to Run the Application](#-how-to-run-the-application)
- [🚀 API Documentation](#-api-documentation)  
- [✅ Running Tests](#-running-tests)
- [🚀 Deployment](#-deployment)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)
- [📞 Contact](#-contact)

---

## 📜 **About the Project**  
**PicBank Auth Service** is a cloud-native authentication and authorization microservice that provides secure user authentication using **AWS Cognito**. It follows **API First principles**, leveraging **Spring Boot, OpenAPI, and AWS services**.  

This project is designed to be **scalable, secure, and DevOps-friendly**, serving as both a **portfolio piece** and a **reference for best practices** in microservices development.  

---

## 🚀 **Features**  
✅ **User Authentication & Authorization** using **AWS Cognito**  
✅ **Token-Based Authentication** (Access & Refresh Tokens)  
✅ **AWS SQS Integration** for asynchronous user group assignment  
✅ **AWS SES Integration** for email notifications  
✅ **Secure IAM Roles & Policies**  
✅ **API Documentation** with OpenAPI & SpringDoc  
✅ **CI/CD with GitHub Actions & Docker**  
✅ **Infrastructure as Code** using **Terraform**  

---

## 🛠️ **Tech Stack**  
| **Technology** | **Description** |  
|--------------|----------------|  
| **Java 21 & Spring Boot 3** | Backend framework for microservices |  
| **AWS Cognito** | Authentication & Authorization |  
| **AWS SQS & SES** | Messaging & Email Notifications |  
| **Docker** | Containerization |  
| **GitHub Actions** | CI/CD Automation |  
| **Terraform** | Infrastructure as Code |  
| **SonarQube** | Code Quality & Static Analysis |  

---

## 📂 **Project Structure**
```
picbank-auth-service/
│── src/main/java/com/picbank/authservice/
│   ├── controllers/         # API controllers
│   ├── services/            # Business logic
│   ├── repositories/        # Database access
│   ├── config/              # Security and app configurations
│   ├── exceptions/          # Custom exceptions
│   ├── dto/                 # Data Transfer Objects (DTOs)
│── src/test/java/com/picbank/authservice/  # Unit and Integration tests
│── Dockerfile               # Docker configuration
│── docker-compose.yml       # Docker Compose configuration
│── .env.example             # Environment variable template
│── README.md                # Documentation
│── pom.xml                  # Maven dependencies
```

---

## ⚙️ **Environment Variables**

Before running the project, you must configure the following environment variables:

| **Variable**               | **Description**                          | **Example** |
|----------------------------|------------------------------------------|-------------|
| `AWS_REGION`               | AWS region for Cognito and SQS          | `us-east-1` |
| `AWS_ACCESS_KEY_ID`        | AWS IAM Access Key                      | `AKIAXXXXXXX` |
| `AWS_SECRET_ACCESS_KEY`    | AWS IAM Secret Key                      | `wJalrXUtnFEMI/K7MDENG/bPxRfiCY` |
| `COGNITO_USER_POOL_ID`     | AWS Cognito User Pool ID                | `us-east-1_XXXXXXX` |
| `COGNITO_CLIENT_ID`        | AWS Cognito App Client ID               | `xxxxxxxxxxxxxxxxxxxxxx` |
| `SQS_QUEUE_URL`            | AWS SQS Queue URL                       | `https://sqs.us-east-1.amazonaws.com/123456789012/queue-name` |
| `SES_SENDER_EMAIL`         | Email for AWS SES                       | `noreply@yourdomain.com` |

> ⚠️ **Important:** Never hardcode secrets. Use `.env` files or AWS Secrets Manager.

### 📌 **Setting up the `.env` file**
The repository contains a `.env.example` file.  
To configure the environment variables, follow these steps:

1. **Copy the example file:**
   ```sh
   cp .env.example .env
   ```
2. **Edit the `.env` file** and fill in the required values.
3. **Do not commit the `.env` file!** Ensure it is in your `.gitignore`.

---

## 🏃 **How to Run the Application**

### 🖥️ **Running Locally**
Ensure you have **Java 21** and **Maven 3.13.0** installed.

1. Clone the repository:
   ```sh
   git clone https://github.com/antoniocesarlopes/picbank-auth-service.git
   cd picbank-auth-service
   ```

2. Configure environment variables:
   ```sh
   cp .env.example .env
   ```

3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

4. The API will be available at:
   ```
   http://localhost:8080
   ```

---

### 🐳 **Running with Docker**
If you prefer, you can run the service using Docker.

#### **Option 1: Run with environment variables manually**
```sh
docker run -p 8080:8080 \
  -e AWS_REGION=us-east-1 \
  -e AWS_ACCESS_KEY_ID=your-access-key \
  -e AWS_SECRET_ACCESS_KEY=your-secret-key \
  -e COGNITO_USER_POOL_ID=your-user-pool-id \
  -e COGNITO_CLIENT_ID=your-client-id \
  -e SQS_QUEUE_URL=your-sqs-url \
  -e SES_SENDER_EMAIL=noreply@yourdomain.com \
  picbank-auth-service
```

#### **Option 2: Run using Docker Compose**
1. **Make sure the `.env` file is properly set up.**
2. **Run the container**:
   ```sh
   docker-compose up -d
   ```
3. The service will be available at:
   ```
   http://localhost:8080
   ```
4. To stop the container:
   ```sh
   docker-compose down
   ```

---

## 📜 **API Documentation**
Once the service is running, access the API documentation:

📖 **Swagger UI**:  
🔗 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  

📄 **OpenAPI Spec** (YAML):  
🔗 [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  

---

## ✅ **Running Tests**
The project includes **unit tests** and **integration tests**.

Run all tests:
```sh
mvn test
```

---

## 🚀 **Deployment**
### **CI/CD Pipeline**
We will configure a **GitHub Actions** workflow to automate:
✅ Code build  
✅ Run tests  
✅ Code quality analysis (SonarQube)  
✅ Docker image creation  
✅ Deployment to AWS  

(🚧 *Work in progress!*)

---

## 🤝 **Contributing**
Contributions are welcome! Feel free to submit pull requests or open issues.

1. Fork the project
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -m "Add new feature"`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

---

## 📜 **License**
This project is licensed under the Apache License - see the [LICENSE](LICENSE) file for details.

---

## 📞 **Contact**
📘 **LinkedIn:** [Your Name](https://linkedin.com/in/antoniocesarlopes)  
```

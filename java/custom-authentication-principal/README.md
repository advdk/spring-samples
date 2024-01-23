
# Custom Authentication Principal Spring Boot Project

## Introduction
This Spring Boot project demonstrates a custom implementation for handling user authentication and authorization. It showcases how to retrieve user access rights using a custom filter and how to access them in controllers with the `@AuthenticationPrincipal` annotation.

## Prerequisites
- Java JDK 17 or later
- Maven (for dependency management and running the application)
- Any IDE that supports Java (like IntelliJ IDEA, Eclipse, etc.)

## Setup and Installation
1. **Clone the Repository**: Clone or download the project to your local machine.
2. **Open the Project**: Open the project in your IDE.
3. **Install Dependencies**: Use Maven to install the required dependencies. This can typically be done through the IDE or by running `mvn install` in the project directory.

## Project Structure
The project includes several key components:

- `ApplicationUser.java`: Defines the `ApplicationUser` entity, representing users in the application.
- `ApplicationUserFilter.java`: A custom filter to retrieve and process user access rights.
- `CustomAuthenticationToken.java`: Defines a custom authentication token for use with Spring Security.
- `UserController.java`: A controller class that handles user-related HTTP requests, utilizing `@AuthenticationPrincipal` for accessing user details.
- `UserRepository.java`: An repository for data access operations on `ApplicationUser` entities.
- `WebSecurityConfig.java`: Configures web security, including authentication and authorization mechanisms, using Spring Security.

## Running the Application
To run the application:
1. Navigate to the project directory in the terminal.
2. Run the command `mvn spring-boot:run`.
3. The application will start, typically accessible at `http://localhost:8080`.

## Conclusion
This project is a basic example of implementing custom user authentication and authorization in a Spring Boot application. It is intended for educational purposes and may require additional configuration and security enhancements for production use.

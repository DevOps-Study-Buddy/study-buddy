# StudyBuddy

## Overview
**StudyBuddy** is an AI-powered educational platform that assists students in exam preparation by providing automated document summarization and AI-generated quizzes. The system allows users to upload study materials (e.g., PDFs, lecture notes, textbook extracts), generate concise summaries, and create interactive quizzes to reinforce learning.


## Tech Stack
- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Security**
- **Spring Data JPA**
- **MySQL Database**
- **JWT for authentication**
- **Apache Tika for text extraction**

---

## Getting Started

### Prerequisites
Ensure you have the following installed:
- **Java 17+**
- **Maven**
- **MySQL Database**

### Clone the Repository
```sh
git clone https://github.com/DevOps-Study-Buddy/study-buddy.git
cd studybuddy
```

### Configure Database
Update `application.properties` with your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/studybuddy
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Build and Run the Application
```sh
mvn clean install
mvn spring-boot:run
```

The application will start at **[`http://localhost:8005`](http://localhost:8005)**

---

## API Endpoints

### **Authentication**
| Method | Endpoint       | Description       |
|--------|--------------|------------------|
| `POST`   | `/api/auth/login`  | User login       |
| `POST`   | `/api/auth/register`  | User registration |

### **File Upload & Extraction**
| Method | Endpoint         | Description       |
|--------|----------------|------------------|
| `POST`   | `/api/documents/upload` | Upload file & extract text |
| `GET`    | `/api/documents/all` | Get all uploaded documents |

---

## Dependencies
The project uses the following dependencies:
- 📌 **Spring Boot Starter Web** - For building REST APIs
- 📌 **Spring Boot Starter Security** - For authentication and authorization
- 📌 **Spring Boot Starter Data JPA** - For database interaction
- 📌 **MySQL Connector** - For connecting to MySQL database
- 📌 **Apache Tika** - For extracting text from uploaded documents
- 📌 **JWT (JJWT)** - For authentication tokens

---
#Backend

# Study Buddy Backend

This project is a backend application for the Study Buddy platform, built using Spring Boot and Maven. It provides RESTful APIs for managing study sessions, users, and resources.

## Project Structure

- `src/main/java/[your-java-packages]`: Contains the Java source code organized into packages.
- `src/main/resources/application.properties`: Configuration properties for the Spring Boot application.
- `src/main/resources/static`: Directory for static resources such as HTML, CSS, and JavaScript files.
- `pom.xml`: Maven configuration file that defines project dependencies and build settings.
- `Dockerfile`: Used to create a Docker image for the application.

## Getting Started

To run this application locally, follow these steps:

1. **Clone the repository**:
   ```
   git clone [repository-url]
   cd study-buddy-backend
   ```

2. **Build the application**:
   ```
   mvn clean package
   ```

3. **Run the application**:
   ```
   java -jar target/studybuddy-0.0.1-SNAPSHOT.jar
   ```

## Docker

To build and run the application using Docker, follow these steps:

1. **Build the Docker image**:
   ```
   docker build -t studybuddy-backend .
   ```

2. **Run the Docker container**:
   ```
   docker run -p 8080:8080 studybuddy-backend
   ```

The application will be accessible at `http://localhost:8080`.


## Acknowledgments

- Spring Boot for the framework.
- Maven for dependency management.
- Docker for containerization.


## License
This project is licensed under the **MIT License**.

## Author
Developed by **Buddy Study Team**. 🚀

commit from dev/backend


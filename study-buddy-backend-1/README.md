# README.md

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

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot for the framework.
- Maven for dependency management.
- Docker for containerization.
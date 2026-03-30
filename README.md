# Secure App Demo

A Spring Boot application demonstrating CI/CD security scanning with Jenkins.

## Features

- ✅ Spring Boot REST API
- ✅ Docker containerization
- ✅ Security scanning (Trivy, OWASP Dependency Check)
- ✅ Jenkins CI/CD pipeline
- ✅ Automated testing

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker
- (Optional) Jenkins with Docker support

## Quick Start

### 1. Build and Run Locally

```bash
# Build the application
mvn clean package

# Run the application
java -jar target/secure-app-1.0.0.jar

# Test endpoints
curl http://localhost:8080/
curl http://localhost:8080/health
curl http://localhost:8080/greet/YourName
```

### 2. Build and Run with Docker

```bash
# Build Docker image
docker build -t secure-app:latest .

# Run container
docker run -p 8080:8080 secure-app:latest

# Test
curl http://localhost:8080/health
```

### 3. Run Tests

```bash
mvn test
```

### 4. Security Scanning

#### Scan Dependencies
```bash
mvn org.owasp:dependency-check-maven:check
```

#### Scan Docker Image (requires Trivy)
```bash
# Install Trivy first: https://aquasecurity.github.io/trivy/
trivy image secure-app:latest
```

## API Endpoints

- `GET /` - Welcome message with version info
- `GET /health` - Health check endpoint
- `GET /greet/{name}` - Personalized greeting
- `GET /info` - Application information

## Project Structure

```
secure-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── Application.java
│   │   │   └── HelloController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/
│           └── HelloControllerTest.java
├── pom.xml
├── Dockerfile
├── Jenkinsfile
└── README.md
```

## Security Features

- Multi-stage Docker build
- Non-root user in container
- Dependency vulnerability scanning
- Container image scanning
- Dockerfile best practices validation

## Jenkins Pipeline

The Jenkinsfile includes:
1. Code checkout
2. Maven build
3. Unit tests
4. Dependency security scan
5. Docker image build
6. Image vulnerability scan (Trivy)
7. Container best practices check (Dockle)
8. Conditional push to registry

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests and security scans
5. Submit a pull request

## License

MIT License

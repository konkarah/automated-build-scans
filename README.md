# Secure App - CI/CD with Security Scanning Demo

A comprehensive demonstration of modern CI/CD practices with automated security scanning, showcasing how organizations prevent vulnerable code from reaching production.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Security](https://img.shields.io/badge/security-scanned-blue)]()
[![Java](https://img.shields.io/badge/java-17-orange)]()
[![Spring Boot](https://img.shields.io/badge/spring%20boot-3.2.0-green)]()
[![Docker](https://img.shields.io/badge/docker-ready-blue)]()

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Running the Application](#running-the-application)
- [CI/CD Pipeline](#cicd-pipeline)
- [Security Scanning](#security-scanning)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Deployment with Ansible](#deployment-with-ansible)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

---

## 🎯 Overview

This project demonstrates a **production-ready CI/CD pipeline** with automated security gates that prevent vulnerable Docker images from being deployed. It showcases:

- ✅ **Automated builds** triggered by code commits
- ✅ **Comprehensive testing** with JUnit
- ✅ **Container security scanning** with Trivy
- ✅ **Security gates** that reject vulnerable images
- ✅ **Automated deployment** with Ansible
- ✅ **Audit trail** of all security decisions

### **The Security Gate Concept**

```
Traditional Flow:
Developer → Build → Deploy → Production → 💥 Vulnerabilities in Production

Secure Flow (This Project):
Developer → Build → Security Scan → ❌ REJECTED (if vulnerable)
                                  → ✅ APPROVED → Deploy → 🛡️ Secure Production
```

---

## 🏗️ Architecture

```
┌─────────────┐
│  Developer  │
└──────┬──────┘
       │ git push
       ▼
┌─────────────┐
│   GitHub    │ ← Source code repository
└──────┬──────┘
       │ webhook
       ▼
┌─────────────────────────────────────────┐
│            Jenkins (CI/CD)              │
│  ┌────────────────────────────────┐    │
│  │ 1. Checkout code               │    │
│  │ 2. Build with Maven            │    │
│  │ 3. Run unit tests              │    │
│  │ 4. Create JAR file             │    │
│  │ 5. Build Docker image          │    │
│  │ 6. 🛡️ Scan with Trivy          │    │
│  │    ├─ CRITICAL found? → REJECT │    │
│  │    └─ Clean? → APPROVE         │    │
│  │ 7. Push to registry            │    │
│  │ 8. Trigger Ansible             │    │
│  └────────────────────────────────┘    │
└──────┬──────────────────────────────────┘
       │ (if approved)
       ▼
┌─────────────┐
│   Registry  │ ← Only secure images
└──────┬──────┘
       │ ansible deploy
       ▼
┌─────────────────────────┐
│  Production Servers     │
│  ┌─────┐ ┌─────┐ ┌─────┐│
│  │ S1  │ │ S2  │ │ S3  ││
│  └─────┘ └─────┘ └─────┘│
└─────────────────────────┘
```

---

## ✨ Features

### **Application Features**
- ☕ Spring Boot 3.2.0 REST API
- 🧪 Comprehensive unit tests (4 test cases)
- 📊 Health check endpoints
- 📝 Application info endpoints
- 🔄 Actuator for monitoring

### **CI/CD Features**
- 🔄 Automated pipeline with Jenkins
- 🐳 Multi-stage Docker builds
- 🛡️ Security scanning with Trivy
- 🚫 Automatic rejection of vulnerable images
- 📦 Maven for dependency management
- 🚀 Ansible for deployment automation

### **Security Features**
- 🔍 Container image vulnerability scanning
- 🚨 Configurable security thresholds
- 📋 Detailed vulnerability reports
- 🔐 Non-root container user
- 🛡️ Minimal base images (Alpine Linux)
- 📊 Audit trail of all security decisions

---

## 📦 Prerequisites

### **Required Software**

| Tool | Version | Purpose |
|------|---------|---------|
| **Java JDK** | 17+ | Compile and run application |
| **Maven** | 3.9+ | Build automation |
| **Docker** | Latest | Containerization |
| **Git** | Latest | Version control |

### **Optional (for full CI/CD)**

| Tool | Version | Purpose |
|------|---------|---------|
| **Jenkins** | LTS | CI/CD automation |
| **Trivy** | 0.69+ | Security scanning |
| **Ansible** | 2.9+ | Deployment automation |

### **Installation Quick Links**

**macOS:**
```bash
brew install openjdk@17 maven docker git
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk maven docker.io git
```

**Windows:**
- Download JDK from [Adoptium](https://adoptium.net/)
- Download Maven from [Apache Maven](https://maven.apache.org/download.cgi)
- Download [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Download [Git for Windows](https://git-scm.com/download/win)

---

## 🚀 Quick Start

### **1. Clone the Repository**

```bash
git clone https://github.com/yourusername/secure-app.git
cd secure-app
```

### **2. Build the Application**

```bash
mvn clean package
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 15.234 s
```

### **3. Run Tests**

```bash
mvn test
```

**Expected output:**
```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### **4. Run Locally**

```bash
java -jar target/secure-app-1.0.0.jar
```

**Test the endpoints:**
```bash
# Main endpoint
curl http://localhost:8080/

# Health check
curl http://localhost:8080/health

# Greeting
curl http://localhost:8080/greet/YourName

# Application info
curl http://localhost:8080/info
```

---

## 🐳 Running the Application

### **Method 1: Run Locally (Development)**

```bash
# Build
mvn clean package

# Run
java -jar target/secure-app-1.0.0.jar

# Access at http://localhost:8080
```

**Pros:** ✅ Fast iteration, ✅ Easy debugging  
**Cons:** ❌ Not containerized, ❌ Environment differences

---

### **Method 2: Run with Docker (Recommended)**

```bash
# Build Docker image
docker build -t secure-app:latest .

# Run container
docker run -p 8080:8080 secure-app:latest

# Access at http://localhost:8080
```

**Pros:** ✅ Isolated environment, ✅ Production-like, ✅ Portable  
**Cons:** ❌ Slightly slower build times

**Additional Docker Commands:**
```bash
# Run in background
docker run -d -p 8080:8080 --name secure-app secure-app:latest

# View logs
docker logs -f secure-app

# Stop container
docker stop secure-app

# Remove container
docker rm secure-app
```

---

### **Method 3: Run with Docker Compose (Multiple Services)**

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "wget", "--spider", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped
```

**Run:**
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

**Pros:** ✅ Easy multi-service setup, ✅ Configuration as code  
**Cons:** ❌ Requires docker-compose installed

---

### **Method 4: Run with Jenkins (Full CI/CD)**

See [CI/CD Pipeline](#cicd-pipeline) section for complete setup.

**Quick Jenkins Setup:**

```bash
# Start Jenkins
docker run -d \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --name jenkins \
  jenkins/jenkins:lts

# Get admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Open http://localhost:8080
```

**Pros:** ✅ Automated, ✅ Security scanning, ✅ Production-ready  
**Cons:** ❌ Initial setup required

---

## 🔄 CI/CD Pipeline

### **Pipeline Overview**

The Jenkins pipeline consists of 8 stages:

```
┌──────────────────────────────────────────────────────────────┐
│  Stage 1: 📋 Checkout                                        │
│  - Pull latest code from GitHub                              │
│  - Duration: ~10 seconds                                     │
└────────────────────┬─────────────────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  Stage 2: 🔨 Build                                           │
│  - Compile Java source code                                  │
│  - Download dependencies                                     │
│  - Duration: ~20 seconds                                     │
└────────────────────┬─────────────────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  Stage 3: 🧪 Unit Tests                                      │
│  - Run all JUnit tests (4 tests)                            │
│  - Generate test reports                                     │
│  - Duration: ~5 seconds                                      │
│  - Fails if ANY test fails                                   │
└────────────────────┬─────────────────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  Stage 4: 📦 Package                                         │
│  - Create executable JAR file                                │
│  - Archive artifacts                                         │
│  - Duration: ~2 seconds                                      │
└────────────────────┬─────────────────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  Stage 5: 🐳 Build Docker Image                              │
│  - Build container image                                     │
│  - Tag with build number                                     │
│  - Duration: ~1 minute                                       │
└────────────────────┬─────────────────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  Stage 6: 🛡️ Security Scan (CRITICAL)                       │
│  - Scan image with Trivy                                     │
│  - Check vulnerabilities against thresholds                  │
│  - REJECT if: CRITICAL > 0 OR HIGH > 5                       │
│  - Duration: ~30 seconds                                     │
│  - THIS IS THE SECURITY GATE!                                │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ├─ ❌ REJECTED → Stop pipeline, notify team
                     │
                     └─ ✅ APPROVED → Continue to deployment
                                      ▼
┌──────────────────────────────────────────────────────────────┐
│  Stage 7: 📤 Push to Registry                                │
│  - Push approved image to Docker registry                    │
│  - Duration: ~10 seconds                                     │
└────────────────────┬─────────────────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────────────────┐
│  Stage 8: 🚀 Deploy with Ansible                             │
│  - Trigger Ansible playbook                                  │
│  - Deploy to production servers                              │
│  - Duration: ~1 minute                                       │
└──────────────────────────────────────────────────────────────┘
```

### **Total Pipeline Duration**
- ✅ Successful build: **~4 minutes**
- ❌ Failed security scan: **~2 minutes** (stops early)

### **Security Thresholds**

Configured in `Jenkinsfile`:

```groovy
environment {
    MAX_CRITICAL = "0"    // Zero critical vulnerabilities allowed
    MAX_HIGH = "5"        // Maximum 5 high severity vulnerabilities
}
```

**Vulnerability Severity Levels:**
- 🔴 **CRITICAL** - Immediate action required, build blocked
- 🟠 **HIGH** - Serious issue, limited by threshold
- 🟡 **MEDIUM** - Moderate risk, logged but not blocking
- 🟢 **LOW** - Minor issue, logged but not blocking

---

## 🛡️ Security Scanning

### **What Gets Scanned**

Trivy scans multiple layers:

1. **Operating System Packages**
   - Alpine Linux base image packages
   - System libraries (busybox, ssl_client, etc.)

2. **Application Dependencies**
   - Java JAR files
   - Spring Boot libraries
   - Third-party dependencies

3. **Known Vulnerabilities**
   - CVE (Common Vulnerabilities and Exposures) database
   - NVD (National Vulnerability Database)

### **Running Scans Manually**

```bash
# Basic scan
trivy image secure-app:latest

# Scan with severity filter
trivy image --severity CRITICAL,HIGH secure-app:latest

# Generate JSON report
trivy image --format json --output report.json secure-app:latest

# Scan and fail on critical
trivy image --exit-code 1 --severity CRITICAL secure-app:latest

# Faster scan (skip secret detection)
trivy image --scanners vuln --timeout 10m secure-app:latest
```

### **Understanding Scan Results**

**Example output:**
```
secure-app:latest (alpine 3.18.4)
═══════════════════════════════════════════════════════

Total: 25 (CRITICAL: 0, HIGH: 2, MEDIUM: 8, LOW: 15)

┌─────────────┬────────────────┬──────────┬───────────────┬──────────────┐
│   Library   │ Vulnerability  │ Severity │ Installed Ver │  Fixed Ver   │
├─────────────┼────────────────┼──────────┼───────────────┼──────────────┤
│ busybox     │ CVE-2023-42363 │ HIGH     │ 1.36.1-r2     │ 1.36.1-r15   │
│ ssl_client  │ CVE-2023-42364 │ HIGH     │ 1.36.1-r2     │ 1.36.1-r15   │
└─────────────┴────────────────┴──────────┴───────────────┴──────────────┘
```

**Interpretation:**
- ✅ **0 CRITICAL** - Passes security gate
- ✅ **2 HIGH** - Within threshold (max 5)
- ✅ **Build approved for deployment**

### **Fixing Vulnerabilities**

**For OS packages:**
```dockerfile
# Update base image
FROM eclipse-temurin:17-jre-alpine3.19  # newer version
```

**For Java dependencies:**
```xml
<!-- Update dependency version in pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.1</version>  <!-- Updated version -->
</dependency>
```

---

## 📁 Project Structure

```
secure-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── Application.java           # Main Spring Boot application
│   │   │   └── HelloController.java       # REST API endpoints
│   │   └── resources/
│   │       └── application.properties     # Application configuration
│   └── test/
│       └── java/com/example/
│           └── HelloControllerTest.java   # Unit tests
│
├── pom.xml                                # Maven dependencies
├── Dockerfile                             # Container definition
├── .dockerignore                          # Docker build exclusions
├── Jenkinsfile                            # CI/CD pipeline definition
├── container-test.yaml                    # Container structure tests
├── dependency-check-suppressions.xml      # Security scan config
├── .gitignore                             # Git exclusions
└── README.md                              # This file
```

### **Key Files Explained**

#### **pom.xml**
Maven build configuration:
- Spring Boot dependencies
- Build plugins
- OWASP dependency check plugin

#### **Dockerfile**
Multi-stage Docker build:
- **Stage 1 (Builder):** Compile and package with Maven
- **Stage 2 (Runtime):** Minimal JRE image with JAR

#### **Jenkinsfile**
Declarative pipeline:
- 8 stages from checkout to deployment
- Security gate with Trivy
- Configurable thresholds

#### **application.properties**
Spring Boot configuration:
- Server port (8080)
- Actuator endpoints
- Logging configuration

---

## ⚙️ Configuration

### **Application Configuration**

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080
spring.application.name=secure-app

# Actuator Configuration
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

# Logging
logging.level.com.example=INFO
```

### **Security Threshold Configuration**

Edit `Jenkinsfile`:

```groovy
environment {
    // Adjust these based on your risk tolerance
    MAX_CRITICAL = "0"    // Recommended: 0 (zero tolerance)
    MAX_HIGH = "5"        // Adjust based on your needs
}
```

**Recommended settings:**
- **Production:** `CRITICAL=0, HIGH=0`
- **Staging:** `CRITICAL=0, HIGH=5`
- **Development:** `CRITICAL=0, HIGH=10`

### **Docker Configuration**

**Change exposed port:**
```dockerfile
# In Dockerfile
EXPOSE 8090  # Changed from 8080

# When running
docker run -p 8090:8090 secure-app:latest
```

**Add environment variables:**
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e JAVA_OPTS="-Xmx512m" \
  secure-app:latest
```

---

## 🚀 Deployment with Ansible

### **Prerequisites**

```bash
# Install Ansible
brew install ansible  # macOS
sudo apt install ansible  # Ubuntu

# Verify installation
ansible --version
```

### **Inventory Configuration**

Create `inventory/production.ini`:

```ini
[app_servers]
server1.example.com ansible_user=ubuntu
server2.example.com ansible_user=ubuntu
server3.example.com ansible_user=ubuntu

[app_servers:vars]
ansible_ssh_private_key_file=~/.ssh/production.pem
docker_image=myregistry.com/secure-app
app_port=8080
host_port=80
```

### **Deployment Playbook**

Create `deploy.yml`:

```yaml
---
- name: Deploy Secure App
  hosts: app_servers
  become: yes
  
  tasks:
    - name: Pull Docker image
      docker_image:
        name: "{{ docker_image }}"
        tag: "{{ build_number }}"
        source: pull
    
    - name: Stop old container
      docker_container:
        name: secure-app
        state: stopped
      ignore_errors: yes
    
    - name: Start new container
      docker_container:
        name: secure-app
        image: "{{ docker_image }}:{{ build_number }}"
        state: started
        restart_policy: always
        ports:
          - "{{ host_port }}:{{ app_port }}"
    
    - name: Wait for health check
      uri:
        url: "http://localhost:{{ host_port }}/health"
        status_code: 200
      register: result
      until: result.status == 200
      retries: 10
      delay: 5
```

### **Deploy Manually**

```bash
# Deploy to production
ansible-playbook -i inventory/production.ini deploy.yml \
  -e "build_number=126"

# Deploy with specific Docker image
ansible-playbook -i inventory/production.ini deploy.yml \
  -e "docker_image=myregistry.com/secure-app:latest"

# Dry run (check what would happen)
ansible-playbook -i inventory/production.ini deploy.yml --check
```

### **Rollback**

Create `rollback.yml`:

```yaml
---
- name: Rollback to Previous Version
  hosts: app_servers
  become: yes
  
  tasks:
    - name: Stop current container
      docker_container:
        name: secure-app
        state: stopped
    
    - name: Start previous version
      docker_container:
        name: secure-app
        image: "{{ docker_image }}:{{ previous_build }}"
        state: started
        restart_policy: always
        ports:
          - "80:8080"
```

**Execute rollback:**
```bash
ansible-playbook -i inventory/production.ini rollback.yml \
  -e "previous_build=125"
```

---

## 🔧 Troubleshooting

### **Common Issues**

#### **Port Already in Use**
```
Error: Port 8080 already in use
```

**Solution:**
```bash
# Find and kill process
lsof -ti:8080 | xargs kill -9

# Or run on different port
java -jar target/*.jar --server.port=8090
```

#### **Docker Build Fails**
```
Error: Cannot connect to Docker daemon
```

**Solution:**
```bash
# Start Docker Desktop (macOS/Windows)
open -a Docker

# Or start Docker service (Linux)
sudo systemctl start docker
```

#### **Trivy Timeout**
```
Error: context deadline exceeded
```

**Solution:**
```bash
# Increase timeout
trivy image --timeout 10m secure-app:latest

# Or skip secret scanning
trivy image --scanners vuln secure-app:latest
```

#### **Jenkins Permission Denied**
```
Error: permission denied while trying to connect to Docker daemon
```

**Solution:**
```bash
# Add jenkins to docker group
docker exec -u root jenkins usermod -aG docker jenkins

# Restart Jenkins
docker restart jenkins
```

#### **Maven Build Fails**
```
Error: package com.example does not exist
```

**Solution:**
```bash
# Clean and rebuild
mvn clean install

# Or skip tests temporarily
mvn clean package -DskipTests
```

### **Debugging Tips**

**View application logs:**
```bash
# Local JAR
java -jar target/*.jar --logging.level.com.example=DEBUG

# Docker container
docker logs -f secure-app

# Last 100 lines
docker logs --tail 100 secure-app
```

**Test endpoints:**
```bash
# Health check
curl -v http://localhost:8080/health

# Full actuator
curl http://localhost:8080/actuator

# With authentication
curl -u admin:password http://localhost:8080/admin
```

**Check Docker image:**
```bash
# List images
docker images | grep secure-app

# Inspect image
docker inspect secure-app:latest

# Run shell in container
docker run -it --entrypoint sh secure-app:latest
```

---

## 🤝 Contributing

### **How to Contribute**

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
4. **Run tests**
   ```bash
   mvn test
   ```
5. **Commit with clear message**
   ```bash
   git commit -m "Add amazing feature"
   ```
6. **Push to your fork**
   ```bash
   git push origin feature/amazing-feature
   ```
7. **Open a Pull Request**

### **Development Guidelines**

- ✅ Write unit tests for new features
- ✅ Follow Java code conventions
- ✅ Update README for significant changes
- ✅ Ensure all tests pass
- ✅ Security scan must pass

### **Testing Your Changes**

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Build Docker image
docker build -t secure-app:test .

# Scan for vulnerabilities
trivy image secure-app:test
```

---

## 📚 Additional Resources

### **Documentation**
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Docker Documentation](https://docs.docker.com/)
- [Jenkins Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Trivy Documentation](https://aquasecurity.github.io/trivy/)
- [Ansible Documentation](https://docs.ansible.com/)

### **Tutorials**
- [Spring Boot Getting Started](https://spring.io/guides/gs/spring-boot/)
- [Docker for Java Developers](https://www.docker.com/blog/intro-guide-to-dockerfile-best-practices/)
- [Jenkins Pipeline Tutorial](https://www.jenkins.io/doc/tutorials/)

### **Security Best Practices**
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Container Security Best Practices](https://snyk.io/learn/container-security/)
- [CVE Database](https://cve.mitre.org/)

---

## 📞 Support

### **Getting Help**

- 📧 **Email:** your-email@example.com
- 🐛 **Issues:** [GitHub Issues](https://github.com/yourusername/secure-app/issues)
- 💬 **Discussions:** [GitHub Discussions](https://github.com/yourusername/secure-app/discussions)
- 📖 **Wiki:** [Project Wiki](https://github.com/yourusername/secure-app/wiki)

### **Reporting Security Issues**

If you discover a security vulnerability, please email security@example.com instead of using the issue tracker.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Aqua Security for Trivy scanner
- Jenkins community for CI/CD automation
- Ansible team for deployment automation
- Docker for containerization technology

---

## 📊 Project Stats

- **Lines of Code:** ~500
- **Test Coverage:** 85%
- **Docker Image Size:** 350 MB
- **Build Time:** ~4 minutes
- **Security Scans:** Every commit
- **Deployment Time:** ~1 minute

---

## 🗺️ Roadmap

- [ ] Add integration tests
- [ ] Implement blue-green deployment
- [ ] Add Prometheus metrics
- [ ] Implement distributed tracing
- [ ] Add Kubernetes deployment
- [ ] Implement feature flags
- [ ] Add database integration
- [ ] Implement API versioning

---

## ⭐ Star History

If you find this project helpful, please consider giving it a star!

[![Star History Chart](https://api.star-history.com/svg?repos=yourusername/secure-app&type=Date)](https://star-history.com/#yourusername/secure-app&Date)

---

**Built with ❤️ and ☕ by [Your Name]**

**Last Updated:** 2026-04-01
# Multi-stage build for security and size optimization
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage - using correct slim image
FROM eclipse-temurin:17-jre-slim-bullseye

# Create non-root user for security
RUN addgroup --system appgroup && adduser --system --no-create-home --ingroup appgroup appuser

WORKDIR /app

# Copy only the JAR file from builder
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check using wget (install if needed)
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
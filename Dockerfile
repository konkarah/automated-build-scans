# # Multi-stage build for security and size optimization
# FROM maven:3.9-eclipse-temurin-17 AS builder

# WORKDIR /app
# COPY pom.xml .
# COPY src ./src

# # Build the application
# RUN mvn clean package -DskipTests

# # Runtime stage - smaller, more secure
# # FROM eclipse-temurin:17-jre-alpine
# FROM eclipse-temurin:17.0.9_14-jre-alpine3.18

# # Create non-root user for security
# RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# WORKDIR /app

# # Copy only the JAR file from builder
# COPY --from=builder /app/target/*.jar app.jar

# # Change ownership to non-root user
# RUN chown -R appuser:appgroup /app

# # Switch to non-root user
# USER appuser

# # Expose port
# EXPOSE 8080

# # Health check
# HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
#   CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# # Run the application
# ENTRYPOINT ["java", "-jar", "app.jar"]

# Multi-stage build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17.0.9_14-jre-alpine3.18

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
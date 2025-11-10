# Multi-stage build for optimized Spring Boot container with fast startup
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for better layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application with optimizations
RUN ./mvnw clean package -DskipTests -Dspring-boot.build-image.skip=true && \
    mkdir -p target/dependency && \
    cd target/dependency && \
    jar -xf ../*.jar

# Runtime stage - minimal image for fast startup
FROM eclipse-temurin:21-jre-alpine

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

WORKDIR /app

# Copy the extracted application layers from builder
ARG DEPENDENCY=/app/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app
USER spring:spring

# Expose port 80
EXPOSE 80

# JVM optimization flags for fast startup and low memory footprint
ENV JAVA_OPTS="-XX:+UseSerialGC \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+TieredCompilation \
    -XX:TieredStopAtLevel=1 \
    -Xss256k \
    -Dspring.backgroundpreinitializer.ignore=true \
    -Dspring.jmx.enabled=false \
    -Dspring.main.lazy-initialization=true \
    -Djava.security.egd=file:/dev/./urandom"

# Use dumb-init to handle signals properly and run the app
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java ${JAVA_OPTS} -cp \"/app:/app/lib/*\" in.srmup.odms.OdManagementSystemApplication"]

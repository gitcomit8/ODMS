# Use a base image that contains both Maven and Java 17
FROM maven:3.9.6-eclipse-temurin-21

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project source code into the container
COPY . .

# Expose the port the Spring Boot application runs on
EXPOSE 8080

# Define the command to run the application
CMD ["mvn", "spring-boot:run"]
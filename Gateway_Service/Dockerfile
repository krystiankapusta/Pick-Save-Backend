# Stage 1: Build the application using Gradle
FROM gradle:8.13.0-jdk21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle build files
COPY build.gradle settings.gradle gradlew gradle /app/

# Download dependencies (use cache)
RUN ./gradlew dependencies || return 0

# Copy the entire project
COPY . /app

# Build the application
RUN ./gradlew :Gateway_Service:clean :Gateway_Service:build -x test

# Stage 2: Use a lightweight JDK image to run the app
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/Gateway_Service/build/libs/*.jar app.jar

# Expose the port (change if your app uses another)
EXPOSE 8091

# Run the app
CMD ["java", "-jar", "app.jar"]

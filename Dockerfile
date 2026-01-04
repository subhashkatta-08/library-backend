# Use official OpenJDK image
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy your JAR
COPY library-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose container port (optional)
EXPOSE 8080

# Run the JAR, setting the port from the environment variable
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]

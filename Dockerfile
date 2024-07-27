# Use the official OpenJDK runtime as a parent image
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container at /app
COPY target/foreign-exchange-app-0.0.1-SNAPSHOT.jar /app/foreign-exchange-app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/foreign-exchange-app.jar"]
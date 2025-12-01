# image
FROM eclipse-temurin:21-jre

# working directory
WORKDIR /app

# copy jar
COPY target/java-tcp-programming-1.0-SNAPSHOT.jar nokemon.jar

# entrypoint
ENTRYPOINT ["java", "-jar", "nokemon.jar"]
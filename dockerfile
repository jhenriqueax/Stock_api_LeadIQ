
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/Stock_api_LeadIQ-0.0.1-SNAPSHOT.jar"]

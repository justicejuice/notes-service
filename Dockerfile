
FROM openjdk:17-slim
WORKDIR /notes-service
ADD target/*.jar notes-service.jar
CMD ["java", "-jar", "notes-service.jar"]

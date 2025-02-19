FROM openjdk:21-slim
COPY ./app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
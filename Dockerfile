FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY app.jar .
CMD ["java", "-jar", "/app.jar"]
FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
# RUN apt-get update && apt-get install -y curl
# RUN curl -o kaddem-0.0.1-SNAPSHOT.jar -L "http://nexus:8081/repository/maven-releases/tn/esprit/spring/kaddem/0.0.1-SNAPSHOT/kaddem-0.0.1-SNAPSHOT.jar"
# COPY kaddem-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080

# Estágio de Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
COPY history-service/pom.xml history-service/
COPY notification-service/pom.xml notification-service/
COPY scheduling-service/pom.xml scheduling-service/

RUN mvn dependency:go-offline -B

# Compila APENAS o módulo notification
COPY notification-service/src ./notification-service/src
RUN mvn clean package -DskipTests -pl notification-service -am -Dfile.encoding=UTF-8

# Estágio de Execução
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/notification-service/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
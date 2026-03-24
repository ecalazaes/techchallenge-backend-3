# Estágio de Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven
COPY pom.xml .
COPY history-service/pom.xml history-service/
COPY notification-service/pom.xml notification-service/
COPY scheduling-service/pom.xml scheduling-service/

# Cache de dependências
RUN mvn dependency:go-offline -B

# Compila APENAS o módulo history
COPY history-service/src ./history-service/src
RUN mvn clean package -DskipTests -pl history-service -am -Dfile.encoding=UTF-8

# Estágio de Execução
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o JAR específico deste módulo
COPY --from=build /app/history-service/target/*.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
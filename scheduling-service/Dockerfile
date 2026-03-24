# Estágio de Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# IMPORTANTE: Copia o POM pai da raiz e os poms dos módulos
# Como o contexto do build será a raiz do projeto, os caminhos devem refletir isso
COPY pom.xml .
COPY history-service/pom.xml history-service/
COPY notification-service/pom.xml notification-service/
COPY scheduling-service/pom.xml scheduling-service/

# Baixa as dependências offline para cache
RUN mvn dependency:go-offline -B

# Copia o código fonte do serviço específico (exemplo para o scheduling-service)
# Repita este Dockerfile em cada pasta alterando o nome do serviço abaixo
COPY scheduling-service/src ./scheduling-service/src
RUN mvn clean package -DskipTests -pl scheduling-service -am -Dfile.encoding=UTF-8

# Estágio de Execução
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o JAR gerado (ajuste o nome do serviço conforme a pasta)
COPY --from=build /app/scheduling-service/target/*.jar app.jar

# Porta padrão (8081 scheduling, 8082 notification, 8083 history)
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
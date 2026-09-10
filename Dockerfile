# Build multi-stage: a imagem final não carrega o Maven nem o código-fonte, só o jar já compilado.

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copia só o pom primeiro para cachear as dependências entre builds (só recomputa se o pom mudar)
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# usuário sem privilégios - a aplicação não precisa (e não deveria) rodar como root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

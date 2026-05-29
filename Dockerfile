FROM ghcr.io/jqlang/jq:latest AS jq-stage

FROM eclipse-temurin:21-jdk AS build
COPY --from=jq-stage /jq /usr/bin/jq

ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY . $HOME

# Compilar omitiendo tests y preparando el frontend de Vaadin
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto por defecto de la aplicación
EXPOSE 8080

# Iniciar la aplicación con el perfil de producción activo
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
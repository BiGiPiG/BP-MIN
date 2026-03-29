FROM maven:3.9.9 AS builder

WORKDIR /build
COPY ./pom.xml ./
COPY ./src ./src/
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app
COPY --from=builder /build/target/*.jar ./app.jar

EXPOSE 9003
ENTRYPOINT ["java", "-jar", "app.jar"]
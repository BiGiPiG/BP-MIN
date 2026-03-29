FROM maven:3.9.9 AS builder

WORKDIR /app
COPY pom.xml ./
COPY ./src ./src/

RUN mvn clean package -DskipTest

FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 9002:9002

ENTRYPOINT ["java", "-jar", "app.jar"]
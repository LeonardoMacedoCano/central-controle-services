FROM gradle:8.5-jdk21-jammy AS builder

WORKDIR /app

COPY . .

RUN gradle build -x test --no-daemon

FROM openjdk:21

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

FROM amazoncorretto:21-alpine-jdk AS build

RUN apk add --no-cache maven

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine-jdk

COPY --from=build /app/target/project-0.0.1-SNAPSHOT.jar app.jar

COPY .env /.env

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

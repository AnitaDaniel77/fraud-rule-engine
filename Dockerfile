# stage 1: build the jar using Maven and Java 21
FROM maven:3.9-eclipse-temurin-21 AS build

# all following commands run inside /app
WORKDIR /app

# copy the pom first so downloaded libraries are cached between builds
COPY pom.xml .
RUN mvn -B dependency:go-offline

# copy the source code and build the jar, tests are run separately
COPY src ./src
RUN mvn -B clean package -DskipTests

# stage 2: a small image with only Java 21 to run the jar
FROM eclipse-temurin:21-jre

WORKDIR /app

# create a normal user so the service does not run as root
RUN useradd --system --create-home appuser

# folder for the H2 database file, owned by that user
RUN mkdir /app/data && chown appuser /app/data

# take only the finished jar from stage 1
COPY --from=build /app/target/*.jar app.jar

# run as the normal user from here on
USER appuser

# the service listens on port 8080
EXPOSE 8080

# start command
ENTRYPOINT ["java", "-jar", "app.jar"]
# Description: Dockerfile for the spring boot application
FROM openjdk:17.0.2-slim-buster as build
WORKDIR /workspace/app

# copy the gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
# download the gradle wrapper
RUN ./gradlew --version

# copy the source code
COPY src ./src
# build the application
RUN ./gradlew bootJar

# create a new image
FROM openjdk:17.0.2-slim-buster
WORKDIR /app
# copy the jar file from the build stage
COPY --from=build /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080

# run the application
CMD ["java", "-jar", "app.jar"]
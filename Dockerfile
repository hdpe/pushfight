FROM amazoncorretto:8u392 AS base
WORKDIR /app
EXPOSE 8080

# Copy the JAR file built by Maven into the image
FROM maven:3.9.6-amazoncorretto-8 AS build
WORKDIR /src

# Copy the project file and download dependencies
COPY pom.xml ./
COPY config config
COPY engine/pom.xml engine/
COPY server-persistence/pom.xml server-persistence/
COPY server-tools/pom.xml server-tools/
COPY server-web/pom.xml server-web/
RUN mvn dependency:go-offline -pl :pushfight-server-web -am -s config/maven/settings.xml

# Copy the remaining source code and build the application
COPY ./ .
RUN mvn package -DskipTests -pl :pushfight-server-web -am -s config/maven/settings.xml

# Use the base image and copy the JAR file to it
FROM base AS final
WORKDIR /app
COPY --from=build /src/server-web/target/pushfight-server-web-1.0-SNAPSHOT-jar.jar ./app.jar

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "app.jar"]

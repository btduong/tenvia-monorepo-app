# To build (in dir containing the Docker file): docker build .
# To build & tag: docker build -t my-image-name/my-image .
# Such as: docker build -t tenvia:latest .

# To prune image older than a day: docker image prune -a --filter "until=24h"


#Each from indicates a new stage
#Stage 1 - Builder
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy mvn files to make use of build cache
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline # Download deps once

# copy source code and build the jar
COPY src ./src
RUN ./mvnw package -DskipTests

#Stage 2 - Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# use wildcard
COPY --from=build /app/target/*.jar app.jar

# Run as a non-root user for security. Below is for debian/ubuntu-based image ie eclipse-temurin
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring
# For Alpine, use addGroup and adduser with different options (not -r or -g)

# default executable (main() equivalent)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
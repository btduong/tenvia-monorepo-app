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
COPY tenvia-common/pom.xml tenvia-common/pom.xml
COPY leaderboard-service/pom.xml leaderboard-service/pom.xml
COPY tenvia-app/pom.xml tenvia-app/pom.xml

# 2. Build and Install ONLY the common library first
# We use -pl (project list) and install it to the container's .m2
RUN ./mvnw install -pl tenvia-common -am -DskipTests

#RUN ./mvnw dependency:go-offline # Download deps once
# 3. NOW you can go offline for the leaderboard service
# Maven will now "see" tenvia-common in the container's local repo
RUN ./mvnw dependency:go-offline -pl leaderboard-service

# copy source code and build the jar
#COPY src ./src
# 5. Copy the actual source code
COPY tenvia-common/src tenvia-common/src
COPY leaderboard-service/src leaderboard-service/src

#RUN ./mvnw package -DskipTests
RUN ./mvnw package -pl leaderboard-service -am -DskipTests

#Stage 2 - Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# use wildcard
COPY --from=build /app/leaderboard-service/target/*.jar app.jar

# Run as a non-root user for security. Below is for debian/ubuntu-based image ie eclipse-temurin
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring
# For Alpine, use addGroup and adduser with different options (not -r or -g)

# default executable (main() equivalent)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
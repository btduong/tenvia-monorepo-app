#Stage 1 - Builder
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy mvn files to make use of build cache
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY tenvia-common/pom.xml tenvia-common/pom.xml
COPY leaderboard-service/pom.xml leaderboard-service/pom.xml
COPY tenvia-app/pom.xml tenvia-app/pom.xml

# Install common-module to local .m2 so tenvia-app can resolve it offline
RUN ./mvnw install -pl tenvia-common -am -DskipTests
RUN ./mvnw dependency:go-offline -pl tenvia-app

COPY tenvia-common/src tenvia-common/src
COPY tenvia-app/src tenvia-app/src

# Build the jar
RUN ./mvnw package -pl tenvia-app -am -DskipTests

#Stage 2 - Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/tenvia-app/target/*.jar app.jar

# Run as a non-root user for security. Below is for debian/ubuntu-based image ie eclipse-temurin
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring
# For Alpine, use addGroup and adduser with different options (not -r or -g)

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
FROM maven:3.8.3-openjdk-11-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY ../../Downloads/g2-backend/pom.xml /workspace
COPY src /workspace/src
RUN mvn -f pom.xml verify clean --fail-never
RUN mvn -f pom.xml package

FROM openjdk:11
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
COPY ../../Downloads/g2-backend/.github .github
RUN apt-get install git
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
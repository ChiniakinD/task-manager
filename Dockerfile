FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY ./auth/pom.xml ./auth/
COPY ./task/pom.xml ./task/
COPY ./common/pom.xml ./common/
COPY ./dependency_bom/pom.xml ./dependency_bom/

COPY ./auth/src ./auth/src/
COPY ./task/src ./task/src/
COPY ./common/src ./common/src/
RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:17-jdk AS auth
WORKDIR /app
COPY --from=build /app/auth/target/*.jar /app/auth.jar
COPY --from=build /app/task/target/*.jar /app/task.jar
ENTRYPOINT ["sh", "-c", "java -jar /app/auth.jar & java -jar /app/task.jar"]

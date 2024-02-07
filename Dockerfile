FROM --platform=linux/amd64 openjdk:21
LABEL maintainer="sviatoslav.jobs@gmail.com"
EXPOSE 8080
ADD backend/target/app.jar app.jar
CMD [ "sh", "-c", "java -jar /app.jar" ]
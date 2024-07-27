FROM openjdk:17-jdk-alpine AS build

MAINTAINER vicheak_application_service

COPY target/video-course-api-1.0.0.jar video-course-api-1.0.0.jar

ENTRYPOINT ["java", "-jar", "/video-course-api-1.0.0.jar"]
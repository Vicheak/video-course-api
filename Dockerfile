FROM openjdk:17 AS build

COPY target/video-course-api-1.0.0.jar video-course-api-1.0.0.jar

ENTRYPOINT ["java", "-jar", "/video-course-api-1.0.0.jar"]
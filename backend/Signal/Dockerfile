FROM openjdk:17

CMD ["./gradlew","clean", "build"]

VOLUME /tmp

ARG JAR_FILE=backend/Signal/build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]

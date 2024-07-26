FROM openjdk:17

ARG JAR_FILE=build/*.jar

COPY ${JAR_FILE} signal.jar

CMD ["java", "-jar", "-Dspring.profiles.active=dev", "-Duser.timezone=Asia/Seoul", "signal.jar"]
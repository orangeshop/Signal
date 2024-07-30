#FROM openjdk:17
#
#CMD ["./gradlew","clean", "build"]
#
#VOLUME /tmp
#
#ARG JAR_FILE=build/libs/*.jar
#
#COPY ${JAR_FILE} app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM jenkins/jenkins:lts

USER root

# Docker 설치
RUN apt-get update && \
    apt-get install -y apt-transport-https ca-certificates curl gnupg2 software-properties-common && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable" && \
    apt-get update && \
    apt-get install -y docker-ce-cli

# Jenkins user를 Docker 그룹에 추가
RUN usermod -aG docker jenkins

USER jenkins


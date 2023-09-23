FROM openjdk:11
MAINTAINER stm
COPY target/auth-1.0-SNAPSHOT.jar auth-1.0.jar
ENTRYPOINT ["java","-jar","/auth-1.0.jar"]
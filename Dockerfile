FROM openjdk:19
LABEL authors="Vadim Taratonov"

ARG JAR_FILE=/target/conveyor-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} conveyor

CMD ["java","-jar","conveyor"]
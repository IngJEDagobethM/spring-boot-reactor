FROM openjdk:11-jdk-slim

ADD target/spring-boot-reactor-0.0.1-SNAPSHOT.jar app/test.jar

CMD ["/bin/sh"]
ENTRYPOINT ["java", "-jar", "app/test.jar"]
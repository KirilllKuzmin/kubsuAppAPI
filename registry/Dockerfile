FROM oopenjdk:17-alpine
EXPOSE 8761
COPY build/libs/registry-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "registry-0.0.1-SNAPSHOT.jar"]
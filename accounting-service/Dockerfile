FROM openjdk:8-jdk-alpine
EXPOSE 8081
COPY build/libs/accounting-service-0.0.1-SNAPSHOT.jar .

RUN apk add --no-cache bash
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

ENTRYPOINT ["/wait-for.sh", "user-service:8080", "--timeout=70", "--", "java", "-jar", "accounting-service-0.0.1-SNAPSHOT.jar"]
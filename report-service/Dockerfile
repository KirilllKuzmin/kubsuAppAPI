FROM openjdk:8-jdk-alpine
EXPOSE 8082
COPY build/libs/report-service-0.0.1-SNAPSHOT.jar .

RUN apk add --no-cache bash
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

ENTRYPOINT ["/wait-for.sh", "accounting-service:8081", "--timeout=70", "--", "java", "-jar", "report-service-0.0.1-SNAPSHOT.jar"]
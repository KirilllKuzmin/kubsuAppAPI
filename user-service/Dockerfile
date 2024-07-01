FROM openjdk:17-alpine
EXPOSE 8080
COPY build/libs/user-service-0.0.1-SNAPSHOT.jar .

RUN apk add --no-cache bash
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

ENTRYPOINT ["/wait-for.sh", "gateway:22022", "--timeout=70", "--", "java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]
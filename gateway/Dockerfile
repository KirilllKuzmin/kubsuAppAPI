FROM openjdk:17-alpine
EXPOSE 22022
COPY build/libs/gateway-0.0.1-SNAPSHOT.jar .

RUN apk add --no-cache bash
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

ENTRYPOINT ["/wait-for.sh", "registry:8761", "--timeout=70", "--", "java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]
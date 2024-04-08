FROM amazoncorretto:21-alpine-jdk

VOLUME /tmp

COPY target/mixer-gateway-*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
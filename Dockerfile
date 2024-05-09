FROM amazoncorretto:21-alpine-jdk
ADD . .
WORKDIR /
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
RUN cp target/mixer-gateway-*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
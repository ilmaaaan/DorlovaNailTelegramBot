FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17
WORKDIR /dorlova_nail
ARG FILE=DorlovaNailTelegramBot-0.0.1-SNAPSHOT.jar
COPY --from=builder app/target/${FILE} /dorlova_nail/dorlova-nail-bot.jar
COPY .env /dorlova_nail

### Добавьте отладочную информацию
#RUN echo "=== Checking environment ===" && \
#    ls -la /dorlova_nail/ && \
#    echo "SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_DATABASE_HOST}:${MYSQL_DATABASE_PORT}" && \
#    echo "SPRING_DATASOURCE_USERNAME=${MYSQL_ROOT_USER}" && \

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/dorlova_nail/dorlova-nail-bot.jar"]
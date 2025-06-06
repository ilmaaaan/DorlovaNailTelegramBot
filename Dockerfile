FROM openjdk:17
WORKDIR /dorlova_nail
ARG FILE=DorlovaNailTelegramBot-0.0.1-SNAPSHOT.jar
COPY target/${FILE} /dorlova_nail/dorlova-nail-bot.jar
COPY .env /dorlova_nail
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/dorlova_nail/dorlova-nail-bot.jar"]
package ru.dorlova.nail;

import io.github.cdimascio.dotenv.Dotenv;
import ru.dorlova.nail.bot.NailStudioBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class DorlovaNailApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(DorlovaNailApplication.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new NailStudioBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
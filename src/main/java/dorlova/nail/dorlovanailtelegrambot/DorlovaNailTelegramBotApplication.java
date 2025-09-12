package dorlova.nail.dorlovanailtelegrambot;

import dorlova.nail.dorlovanailtelegrambot.bot.NailStudioBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class DorlovaNailTelegramBotApplication {

    public static void main(String[] args) {

        SpringApplication.run(DorlovaNailTelegramBotApplication.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new NailStudioBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
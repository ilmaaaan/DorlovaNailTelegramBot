package dorlova.nail.dorlovanailtelegrambot.bot;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NailStudioBot extends TelegramLongPollingBot {

    private final Map<Long, String> userStates = new HashMap<>();
    private final Map<Long, List<String>> userData = new HashMap<>();
    private final List<String> userChatIdData = new ArrayList<>();
    private final String BOT_TOKEN;
    private final long DASHA_CHAT_ID;
    private final long ILMAN_CHAT_ID;

    {
        Dotenv dotenv = Dotenv.load();
        BOT_TOKEN = dotenv.get("TELEGRAM_BOT_TOKEN");
        DASHA_CHAT_ID = Long.parseLong(dotenv.get("DASHA_CHAT_ID"));
        ILMAN_CHAT_ID = Long.parseLong(dotenv.get("ILMAN_CHAT_ID"));
    }

    @Override
    public String getBotUsername() {
        return "DorlovaNailBot";
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String state = userStates.getOrDefault(chatId, "START");

            switch (state) {
                case "START":
                    sendMessageWithKeyboard(chatId,
                            "   Добро пожаловать в студию маникюра Dorlova_nail\uD83C\uDF80\n\n" +
                                    "Наш бот работает пока что в демо режиме, поэтому по поводу даты и времени записи, " +
                                    "скорее всего, будут уточнения у мастера и, возможно, их изменение. \n\n" +
                                    "В скором времени мы станем еще лучше <3 ",
                            createKeyboard("Начать запись"));
                    userChatIdData.clear();
                    userData.clear();
                    userStates.put(chatId, "START_SIGN_UP");
                    userData.put(chatId,userChatIdData);
                    break;
                case "START_SIGN_UP":
                    sendMessageWithKeyboard(chatId,
                            "Выберите мастера, к которому Вы хотите записаться:",
                            createKeyboard("Дарья", "Ирина", "Ксения"));
                    userStates.put(chatId, "SELECT_MASTER");
                    break;
                case "SELECT_MASTER":
                    String master = messageText;
                    if (messageText.equalsIgnoreCase("/start")) {
                        userStates.remove(chatId);
                        sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                createKeyboard("Да"));
                    } else {
                        if (messageText.equalsIgnoreCase("Дарья")) {
                            userData.get(chatId).add(master);
                            sendMessageWithKeyboard(chatId, "Выберите услугу:", createKeyboard(
                                    "маникюр без покрытия",
                                    "маникюр с покрытием гель-лак",
                                    "наращивание ногтей",
                                    "коррекция наращивания ногтей",
                                    "педикюр без покрытия",
                                    "педикюр с покрытием без обработки пяточек",
                                    "педикюр с покрытием с обработкой пяточек"
                            ));
                            userStates.put(chatId, "SELECT_SERVICE");
                        } else if (messageText.equalsIgnoreCase("Ирина")) {
                            userData.get(chatId).add(master);
                            sendMessageWithKeyboard(chatId, "Выберите услугу:", createKeyboard(
                                    "маникюр без покрытия",
                                    "маникюр с покрытием гель-лак",
                                    "коррекция наращивания ногтей",
                                    "педикюр без покрытия",
                                    "педикюр с покрытием без обработки пяточек",
                                    "педикюр с покрытием с обработкой пяточек"
                            ));
                            userStates.put(chatId, "SELECT_SERVICE");
                        } else if (messageText.equalsIgnoreCase("Ксения")) {
                            userData.get(chatId).add(master);
                            sendMessageWithKeyboard(chatId, "Выберите услугу:", createKeyboard(
                                    "маникюр без покрытия",
                                    "маникюр с покрытием гель-лак",
                                    "коррекция наращивания ногтей"
                            ));
                            userStates.put(chatId, "SELECT_SERVICE");
                        }
                    }
                    break;
                case "SELECT_SERVICE":
                    if (messageText.equalsIgnoreCase("/start")) {
                        userStates.remove(chatId);
                        sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                createKeyboard("Да"));
                    } else {
                        String service = messageText;
                        userData.get(chatId).add(service);
                        sendMessage(chatId, "Напишите удобную для Вас дату:");
                        userStates.put(chatId, "SELECT_DATE");
                    }
                    break;
                case "SELECT_DATE":
                    if (messageText.equalsIgnoreCase("/start")) {
                        userStates.remove(chatId);
                        sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                createKeyboard("Да"));
                    } else {
                        String date = messageText;
                        userData.get(chatId).add(date);
                        if (userChatIdData.get(0).startsWith("Дарья")) {
                            sendMessageWithKeyboard(chatId, "Выберите время:",
                                    createKeyboard("12:00", "14:00", "17:00", "19:00"));
                        } else {
                            sendMessage(chatId, "Напишите время для записи:");
                        }
                        userStates.put(chatId, "SELECT_TIME");
                    }
                    break;
                case "SELECT_TIME":
                    if (messageText.equalsIgnoreCase("/start")) {
                        userStates.remove(chatId);
                        sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                createKeyboard("Да"));
                    } else {
                        String time = messageText;
                        userData.get(chatId).add(time);
                        sendMessage(chatId, "Спасибо за запись\uD83D\uDC98\n\n" +
                                "Ваш мастер: %s\n".formatted(userChatIdData.get(0)) +
                                "Услуга: %s\n".formatted(userChatIdData.get(1))+
                                "Дата: %s\n".formatted(userChatIdData.get(2)) +
                                "Время: %s\n\n".formatted(userChatIdData.get(3)));
                        sendMessage(chatId, "Напишите, пожалуйста, Ваши имя, фамилию и номер телефона, для того " +
                                "чтобы мы смогли связаться с Вами\n" +
                                "(укажите их в одном сообщении):");
                        userStates.put(chatId, "CLIENT_NAME");
                    }
                    break;
                case "CLIENT_NAME":
                    if (messageText.equalsIgnoreCase("/start")) {
                        userStates.remove(chatId);
                        sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                createKeyboard("Да"));
                    } else {
                        String nameAndNumber = messageText;
                        userData.get(chatId).add(nameAndNumber);
                        sendMessageWithKeyboard(chatId, "Вы подтверждаете свою запись?\n" +
                                        "Если да, то нажмите кнопку ПОДТВЕРДИТЬ запись.\n" +
                                        "Если хотите изменить запись, то отправьте боту команду /start",
                                createKeyboard("Подтвердить"));
                        sendMessage(ILMAN_CHAT_ID, userData.get(chatId).toString());
                        userStates.put(chatId, "CONFIRM");
                    }
                    break;
                case "CONFIRM":
                    if (messageText.equalsIgnoreCase("/start")) {
                        userStates.remove(chatId);
                        sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                createKeyboard("Да"));
                    } else if (messageText.equalsIgnoreCase("Подтвердить")) {
                        sendMessage(chatId, "Ожидайте сообщения от нашего мастера!\n" +
                                "Ваша любимая студия Dorlova_nail\uD83D\uDC85");
                        sendMessage(DASHA_CHAT_ID, "‼\uFE0F‼\uFE0FНовая запись‼\uFE0F‼\uFE0F\n" +
                                "Мастер: %s\n".formatted(userChatIdData.get(0)) +
                                "Услуга: %s\n".formatted(userChatIdData.get(1))+
                                "Дата: %s\n".formatted(userChatIdData.get(2)) +
                                "Время: %s\n".formatted(userChatIdData.get(3)) +
                                "Клиент: %s".formatted(userChatIdData.get(4)));
                        userData.remove(chatId);
                        userChatIdData.clear();
                    }
                    break;
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageWithKeyboard(long chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup createKeyboard(String... options) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (String option : options) {
            row.add(option);
            keyboardRows.add(row);
            row = new KeyboardRow();
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}

package dorlova.nail.dorlovanailtelegrambot.bot;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
//some changes here
//some new dsafgasdkjlfjgaslkdjflaksdjflkasdjflasfsan falds fa
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class NailStudioBot extends TelegramLongPollingBot {

    private final Map<Long, String> userStates = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> userData = new ConcurrentHashMap<>();
    private final List<String> dashaServices = new CopyOnWriteArrayList<>(Arrays.asList(
            "маникюр без покрытия (2100р)",
            "маникюр с покрытием гель-лаком (3000р)",
            "наращивание ногтей (4300р)",
            "коррекция наращивания ногтей (3300р)",
            "педикюр без покрытия (2300р)",
            "педикюр с покрытием без обработки пяточек (3000р)",
            "педикюр с покрытием и обработкой пяточек (3200р)"
    ));

    private final List<String> irinaServices = new CopyOnWriteArrayList<>(Arrays.asList(
            "маникюр без покрытия (1500р)",
            "маникюр с покрытием гель-лаком (2200р)",
            "наращивание ногтей (2800р)",
            "коррекция наращивания ногтей (2600р)",
            "педикюр без покрытия (2000р)",
            "педикюр с покрытием без обработки пяточек (2600р)",
            "педикюр с покрытием и обработкой пяточек (2800р)"
    ));

    private final List<String> polinaServices = new CopyOnWriteArrayList<>(Arrays.asList(
            "маникюр без покрытия (300р)",
            "маникюр с покрытием гель-лаком (500р)",
            "коррекция наращивания ногтей (500р)"
    ));

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

            synchronized (this) {
                switch (state) {
                    case "START":
                        sendMessageWithKeyboard(chatId,
                                "   Добро пожаловать в студию маникюра Dorlova_nail\uD83C\uDF80\n\n" +
                                        "Наш бот работает пока что в демо режиме, поэтому по поводу даты и времени записи, " +
                                        "скорее всего, будут уточнения у мастера и, возможно, их изменение. \n\n" +
                                        "В скором времени мы станем еще лучше <3 ",
                                createKeyboard("Начать запись"));
                        userStates.put(chatId, "START_SIGN_UP");
                        userData.put(chatId, new ConcurrentHashMap<>());
                        break;
                    case "START_SIGN_UP":
                        sendMessageWithKeyboard(chatId,
                                "Выберите мастера, к которому Вы хотите записаться:",
                                createKeyboard("Дарья", "Ирина", "Полина"));
                        userStates.put(chatId, "SELECT_MASTER");
                        break;
                    case "SELECT_MASTER":
                        String master = messageText;
                        if (messageText.equalsIgnoreCase("/start")) {
                            userStates.remove(chatId);
                            sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                    createKeyboard("Да"));
                        } else {
                            userData.get(chatId).put("Мастер", master);
                            if (messageText.equalsIgnoreCase("Дарья")) {
                                sendMessageWithKeyboard(chatId,
                                        "Выберите услугу:",
                                        createKeyboard(dashaServices));

                            } else if (messageText.equalsIgnoreCase("Ирина")) {
                                sendMessageWithKeyboard(chatId,
                                        "Выберите услугу:",
                                        createKeyboard(irinaServices));

                            } else if (messageText.equalsIgnoreCase("Полина")) {
                                sendMessageWithKeyboard(chatId,
                                        "Выберите услугу:",
                                        createKeyboard(polinaServices));
                            }
                            userStates.put(chatId, "SELECT_SERVICE1");
                        }
                        break;
                    case "SELECT_SERVICE1":
                        String service1 = messageText;
                        if (messageText.equalsIgnoreCase("/start")) {
                            userStates.remove(chatId);
                            sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                    createKeyboard("Да"));
                        } else {
                            userData.get(chatId).put("Услуга", service1);
                            if (userData.get(chatId).get("Мастер").equalsIgnoreCase("Дарья")) {
                                List<String> dashaServices2 = new ArrayList<>();
                                dashaServices2.add("без доп.услуги");
                                dashaServices2.addAll(dashaServices);
                                dashaServices2.remove(service1);
                                sendMessageWithKeyboard(chatId,
                                        "Выберите дополнительную услугу:",
                                        createKeyboard(dashaServices2));
                            } else if (userData.get(chatId).get("Мастер").equalsIgnoreCase("Ирина")) {
                                List<String> irinaServices2 = new ArrayList<>();
                                irinaServices2.add("без доп.услуги");
                                irinaServices2.addAll(irinaServices);
                                irinaServices2.remove(service1);
                                sendMessageWithKeyboard(chatId,
                                        "Выберите услугу:",
                                        createKeyboard(irinaServices2));

                            } else if (userData.get(chatId).get("Мастер").equalsIgnoreCase("Полина")) {
                                List<String> kseniyaServices2 = new ArrayList<>();
                                kseniyaServices2.add("без доп.услуги");
                                kseniyaServices2.addAll(irinaServices);
                                kseniyaServices2.remove(service1);
                                sendMessageWithKeyboard(chatId,
                                        "Выберите услугу:",
                                        createKeyboard(kseniyaServices2));
                            }
                            userStates.put(chatId, "SELECT_SERVICE2");
                        }
                        break;
                    case "SELECT_SERVICE2":
                        if (messageText.equalsIgnoreCase("/start")) {
                            userStates.remove(chatId);
                            sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                    createKeyboard("Да"));
                        } else {
                            String service2 = messageText;
                            if (!messageText.equalsIgnoreCase("без доп.услуги")) {
                                service2 = service2.concat(" + ").concat(userData.get(chatId).get("Услуга"));
                            } else {
                                service2 = userData.get(chatId).get("Услуга");
                            }
                            userData.get(chatId).put("Услуга", service2);
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
                            userData.get(chatId).put("Дата", date);
                            if (userData.get(chatId).get("Мастер").equalsIgnoreCase("Дарья")) {
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
                            userData.get(chatId).put("Время", time);
                            sendMessage(chatId, "Спасибо за запись\uD83D\uDC98\n\n" +
                                    "Ваш мастер: %s\n".formatted(userData.get(chatId).get("Мастер")) +
                                    "Услуга: %s\n".formatted(userData.get(chatId).get("Услуга")) +
                                    "Дата: %s\n".formatted(userData.get(chatId).get("Дата")) +
                                    "Время: %s\n\n".formatted(userData.get(chatId).get("Время")));
                            sendMessage(chatId, "Напишите, пожалуйста, Ваши имя и фамилию☺\uFE0F");
                            userStates.put(chatId, "CLIENT_NAME");
                        }
                        break;
                    case "CLIENT_NAME":
                        if (messageText.equalsIgnoreCase("/start")) {
                            userStates.remove(chatId);
                            sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                    createKeyboard("Да"));
                        } else {
                            String name = messageText;
                            userData.get(chatId).put("Клиент", name);
                            sendMessage(chatId, "Ваш номер телефона, для того " +
                                    "чтобы мы смогли связаться с Вами\uD83D\uDE48");
                            userStates.put(chatId, "CLIENT_NUMBER");
                        }
                        break;

                    case "CLIENT_NUMBER":
                        if (messageText.equalsIgnoreCase("/start")) {
                            userStates.remove(chatId);
                            sendMessageWithKeyboard(chatId, "Начнем сначала?",
                                    createKeyboard("Да"));
                        } else {
                            String number = messageText;
                            userData.get(chatId).put("Номер", number);
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
                            sendMessage(ILMAN_CHAT_ID, "‼\uFE0F‼\uFE0FНовая запись‼\uFE0F‼\uFE0F\n\n" +
                                    "Мастер: %s\n".formatted(userData.get(chatId).get("Мастер")) +
                                    "Услуга: %s\n".formatted(userData.get(chatId).get("Услуга")) +
                                    "Дата: %s\n".formatted(userData.get(chatId).get("Дата")) +
                                    "Время: %s\n".formatted(userData.get(chatId).get("Время")) +
                                    "Клиент: %s\n".formatted(userData.get(chatId).get("Клиент")) +
                                    "Номер: %s\n\n".formatted(userData.get(chatId).get("Номер")));
                            sendMessage(DASHA_CHAT_ID, "‼\uFE0F‼\uFE0FНовая запись‼\uFE0F‼\uFE0F\n\n" +
                                    "Мастер: %s\n".formatted(userData.get(chatId).get("Мастер")) +
                                    "Услуга: %s\n".formatted(userData.get(chatId).get("Услуга")) +
                                    "Дата: %s\n".formatted(userData.get(chatId).get("Дата")) +
                                    "Время: %s\n".formatted(userData.get(chatId).get("Время")) +
                                    "Клиент: %s\n".formatted(userData.get(chatId).get("Клиент")) +
                                    "Номер: %s\n\n".formatted(userData.get(chatId).get("Номер")));
                            userData.remove(chatId);
                        }
                        break;
                }
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

    private ReplyKeyboardMarkup createKeyboard(List<String> options) {
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
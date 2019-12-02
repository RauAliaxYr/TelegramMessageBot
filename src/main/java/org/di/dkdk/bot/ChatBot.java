package org.di.dkdk.bot;

import org.di.dkdk.model.User;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.di.dkdk.service.UserService;

import java.util.List;


@Component
@PropertySource("classpath:telegram.properties")
public class ChatBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(ChatBot.class);

    private static final String BROADCAST = "broadcast ";
    private static final String LIST_USERS = "users";

    @Value("${org.di.dkdk.bot.name}")
    private String botName;

    @Value("${org.di.dkdk.bot.token}")
    private String botToken;

    private final UserService userService;

    public ChatBot(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        final String text = update.getMessage().getText();
        final long chatId = update.getMessage().getChatId();

        User user = userService.findByChatId(chatId);

        if (checkIfAdminCommand(user, text)) // проверяем не одминская ли команда
            return;
        BotContext context;
        BotState state;

        if (user == null) { //если юзера ещё нет в базе
            state = BotState.getInitialState();

            user = new User(chatId, state.ordinal());
            userService.addUser(user);

            context = BotContext.of(this, user, text);
            state.enter(context);

            LOGGER.info("New User " + chatId);
        } else { //если юзер уже есть в базе определяем его состояние
            context = BotContext.of(this, user, text);
            state = BotState.byId(user.getStateId());

            LOGGER.info("Update user " + state);
        }
        //определяем введённое сообщение как ответ на вопрос
        state.handleInput(context);

        //переходим в следующее состояние и сохраняем
        do {
            state = state.nextState();
            state.enter(context);
        }
        while (!state.isInputNeeded());

        user.setStateId(state.ordinal());
        userService.updateUser(user);

    }

    private boolean checkIfAdminCommand(User user, String text) {
        if (user == null || !user.getAdmin())
            return false;
        if (text.startsWith(BROADCAST)) { //рассылаем всем сообщение
            LOGGER.info("Admin  command received " + BROADCAST);

            text = text.substring(BROADCAST.length());
            broadcast(text);

            return true;
        } else if (text.startsWith(LIST_USERS)) {//показывает список юзеров
            LOGGER.info("Admin  command received " + LIST_USERS);

            listUsers(user);

            return true;
        }
        return false;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try {
            execute(message);

        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    private void listUsers(User admin){
        StringBuilder sb = new StringBuilder("All users list:" + "\n");
        List<User> users = userService.findAllUsers();

        users.forEach(user ->
                sb.append(user.getId())
                        .append(' ')
                        .append(user.getPhone())
                        .append(' ')
                        .append(user.getEmail())
                        .append("\r\n")
        );
        sendMessage(admin.getChatId(),sb.toString());
    }
    private void broadcast(String text) {
        List<User> users = userService.findAllUsers();
        users.forEach(user -> sendMessage(user.getChatId(),text));

    }
}

package com.balashenka.cuddly.bot.impl;

import com.balashenka.cuddly.bot.BaseBot;
import com.balashenka.cuddly.bot.BotContext;
import com.balashenka.cuddly.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class WebhookBot extends TelegramWebhookBot implements BaseBot {
    private final ApplicationConfig applicationConfig;
    private final BotContext botContext;

    @Autowired
    public WebhookBot(ApplicationConfig applicationConfig, BotContext botContext) {
        this.applicationConfig = applicationConfig;
        this.botContext = botContext;
    }

    @Override
    public String getBotUsername() {
        return applicationConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return applicationConfig.getBotToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage()) {
            executeCommand(update);
        } else if (update.hasCallbackQuery()) {
            executeCallback(update);
        }

        return null;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdate(Update update) {
        return onWebhookUpdateReceived(update);
    }

    @Override
    public String getBotPath() {
        return applicationConfig.getWebhookPath();
    }

    @Override
    public void sendMessage(SendMessage sendMessage) throws TelegramApiException {
        execute(sendMessage);
    }

    @Override
    public void sendVideo(SendVideo sendVideo) throws TelegramApiException {
        execute(sendVideo);
    }

    @Override
    public void sendPhoto(SendPhoto sendPhoto) throws TelegramApiException {
        execute(sendPhoto);
    }

    @Override
    public void sendMediaGroup(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        execute(sendMediaGroup);
    }

    @Override
    public void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) throws TelegramApiException {
        execute(editMessageReplyMarkup);
    }

    private void executeCommand(Update update) {
        var message = update.getMessage();

        if (message != null) {
            var command = botContext.handleInputCommand(update.getMessage());

            if (command != null) {
                command.execute(message);
            }
        }
    }

    private void executeCallback(Update update) {
        var callbackQuery = update.getCallbackQuery();

        var callback = botContext.handleInputCallback(callbackQuery.getData());

        if (callback != null) {
            callback.execute(callbackQuery);
        }
    }
}

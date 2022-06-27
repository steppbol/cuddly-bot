package com.balashenka.cuddly.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BaseBot {
    default BotApiMethod<?> onWebhookUpdate(Update update) {
        return null;
    }

    void sendMessage(SendMessage sendMessage) throws TelegramApiException;

    void sendVideo(SendVideo sendVideo) throws TelegramApiException;

    void sendPhoto(SendPhoto sendPhoto) throws TelegramApiException;

    void sendMediaGroup(SendMediaGroup sendMediaGroup) throws TelegramApiException;

    void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) throws TelegramApiException;
}

package com.balashenka.cuddly.bot;

import com.balashenka.cuddly.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class BotFacade {
    private final BaseBot baseBot;
    private final MessageService messageService;

    @Autowired
    public BotFacade(BaseBot baseBot, MessageService messageService) {
        this.baseBot = baseBot;
        this.messageService = messageService;
    }

    public BotApiMethod<?> onUpdateReceived(Update update) {
        return baseBot.onWebhookUpdate(update);
    }

    public void sendMessage(SendMessage sendMessage, User user, String type) {
        try {
            if (type != null) {
                log.info("Send message, chat id : {}, user : {}, user id : {}, type : {}, message : {}",
                        sendMessage.getChatId(), user.getUserName(), user.getId(), type, sendMessage.getText());
            }

            baseBot.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            log.warn(e.getMessage());
        }
    }

    public void sendVideo(SendVideo sendVideo, User user, String userChatId, String type) {
        try {
            log.info("Send video, chat id : {}, user : {}, user id : {}, type : {}",
                    sendVideo.getChatId(), user.getUserName(), user.getId(), type);

            baseBot.sendVideo(sendVideo);
        } catch (TelegramApiException e) {
            sendErrorMessage(userChatId, user, type);

            log.warn(e.getMessage());
        }
    }

    public void sendPhoto(SendPhoto sendPhoto, User user, String userChatId, String type) {
        try {
            log.info("Send photo, chat id : {}, user : {}, user id : {}, type : {}",
                    sendPhoto.getChatId(), user.getUserName(), user.getId(), type);

            baseBot.sendPhoto(sendPhoto);
        } catch (TelegramApiException e) {
            sendErrorMessage(userChatId, user, type);

            log.warn(e.getMessage());
        }
    }

    public void sendMediaGroup(SendMediaGroup sendMediaGroup, User user, String userChatId, String type) {
        try {
            log.info("Send media group, chat id : {}, user : {}, user id : {}, type : {}",
                    sendMediaGroup.getChatId(), user.getUserName(), user.getId(), type);

            baseBot.sendMediaGroup(sendMediaGroup);
        } catch (TelegramApiException e) {
            sendErrorMessage(userChatId, user, type);

            log.warn(e.getMessage());
        }
    }

    public void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup, User user, String type) {
        var chatId = editMessageReplyMarkup.getChatId();

        try {
            log.info("Edit message reply markup, chat id : {}, user : {}, user id : {}, type : {}",
                    chatId, user.getUserName(), user.getId(), type);

            baseBot.editMessageReplyMarkup(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            sendErrorMessage(chatId, user, type);

            log.warn(e.getMessage());
        }
    }

    public void sendErrorMessage(String chatId, User user, String type) {
        var sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(messageService.getText("exception.internal_error"))
                .build();
        sendMessage(sendMessage, user, type);
    }
}

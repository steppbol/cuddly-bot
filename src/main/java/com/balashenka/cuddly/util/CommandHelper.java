package com.balashenka.cuddly.util;

import com.balashenka.cuddly.bot.BotFacade;
import com.balashenka.cuddly.config.ApplicationConfig;
import com.balashenka.cuddly.entity.PostInformation;
import com.balashenka.cuddly.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CommandHelper {
    private final BotFacade botFacade;
    private final ApplicationConfig applicationConfig;
    private final MessageService messageService;

    @Autowired
    public CommandHelper(@Lazy BotFacade botFacade, ApplicationConfig applicationConfig, MessageService messageService) {
        this.botFacade = botFacade;
        this.applicationConfig = applicationConfig;
        this.messageService = messageService;
    }

    public boolean checkUserHasPermission(User user) {
        var users = Arrays.asList(applicationConfig.getUsersWhitelist().split(" "));

        return users.contains(user.getUserName());
    }

    public void sendErrorMessage(String chatId, User user, String type) {
        botFacade.sendErrorMessage(chatId, user, type);
    }

    public void sendPermissionDeniedMessage(String chatId, User user, String type) {
        botFacade.sendMessage(getSendMessageTemplate(messageService.getText("message.permission_denied"), chatId), user, type);
    }

    public void sendMessageWithReplyMarkup(String text, ReplyKeyboardMarkup replyKeyboardMarkup, String chatId,
                                           Integer messageId, User user, String type) {
        var sendMessage = getSendMessageTemplate(text, chatId);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setReplyToMessageId(messageId);

        botFacade.sendMessage(sendMessage, user, type);
    }

    public void sendMessageWithInlineMarkup(String text, InlineKeyboardMarkup inlineKeyboardMarkup, String chatId,
                                            Integer messageId, User user, String type) {
        var sendMessage = getSendMessageTemplate(text, chatId);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setReplyToMessageId(messageId);

        botFacade.sendMessage(sendMessage, user, type);
    }

    public void sendPhotoToChannel(PostInformation postInformation, String channelId, String chatId, User user, String type) {
        if (postInformation.getUrls().size() > 1) {
            sendMediaGroupWithCaption(postInformation.getCaption(), postInformation.getUrls(),
                    channelId, chatId, user, type);
        } else if (postInformation.getUrls().size() == 1) {
            try {
                var urls = postInformation.getUrls();
                var url = urls.keySet().stream().findFirst().orElse("");

                if (urls.containsValue(true)) {
                    sendVideoWithCaption(postInformation.getCaption(), url, channelId, chatId, user, type);
                } else {
                    sendPhotoWithCaption(postInformation.getCaption(), url, channelId, chatId, user, type);
                }
            } catch (IOException e) {
                log.warn(e.getMessage());
                sendErrorMessage(chatId, user, type);
            }
        }
    }

    public void deleteMessageInlineMarkup(String chatId, User user, Integer messageId, String inlineMessageId, String type) {
        var markupInline = new InlineKeyboardMarkup();
        var rowsInline = new ArrayList<List<InlineKeyboardButton>>();

        rowsInline.add(new ArrayList<>());
        markupInline.setKeyboard(rowsInline);

        editMessageReplyMarkup(chatId, user, messageId, inlineMessageId, markupInline, type);
    }

    private void sendVideoWithCaption(String caption, String url, String chatId, String userChatId, User user, String type) throws IOException {
        var sendPhoto = SendVideo
                .builder()
                .chatId(chatId)
                .caption(caption)
                .video(new InputFile(new URL(url).openStream(), applicationConfig.getApplicationName() + ".mp4"))
                .build();

        botFacade.sendVideo(sendPhoto, user, userChatId, type);
    }

    private void sendPhotoWithCaption(String caption, String url, String chatId, String userChatId, User user, String type) throws IOException {
        var sendPhoto = SendPhoto
                .builder()
                .chatId(chatId)
                .caption(caption)
                .photo(new InputFile(new URL(url).openStream(), applicationConfig.getApplicationName() + ".jpg"))
                .build();

        botFacade.sendPhoto(sendPhoto, user, userChatId, type);
    }

    private void sendMediaGroupWithCaption(String caption, Map<String, Boolean> urls, String chatId, String userChatId, User user, String type) {
        var sendMediaGroup = SendMediaGroup
                .builder()
                .chatId(chatId)
                .medias(urls.entrySet()
                        .stream()
                        .map(url -> {
                            InputMedia inputMedia;

                            if (url.getValue()) {
                                inputMedia = new InputMediaVideo();
                            } else {
                                inputMedia = new InputMediaPhoto();
                            }

                            inputMedia.setMedia(url.getKey());
                            return inputMedia;
                        })
                        .collect(Collectors.toList()))
                .build();

        sendMediaGroup.getMedias().get(0).setCaption(caption);

        botFacade.sendMediaGroup(sendMediaGroup, user, userChatId, type);
    }

    public void editMessageReplyMarkup(String chatId, User user, Integer messageId,
                                       String inlineMessageId, InlineKeyboardMarkup inlineKeyboardMarkup, String type) {
        var editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setInlineMessageId(inlineMessageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        botFacade.editMessageReplyMarkup(editMessageReplyMarkup, user, type);
    }

    private SendMessage getSendMessageTemplate(String text, String chatId) {
        var sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();

        sendMessage.enableHtml(true);

        return sendMessage;
    }
}

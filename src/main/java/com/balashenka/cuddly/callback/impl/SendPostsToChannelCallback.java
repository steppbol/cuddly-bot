package com.balashenka.cuddly.callback.impl;

import com.balashenka.cuddly.callback.BaseCallback;
import com.balashenka.cuddly.config.ApplicationConfig;
import com.balashenka.cuddly.entity.CallbackType;
import com.balashenka.cuddly.service.InstagramService;
import com.balashenka.cuddly.service.MessageService;
import com.balashenka.cuddly.util.CommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class SendPostsToChannelCallback implements BaseCallback {
    private final CommandHelper commandHelper;
    private final InstagramService instagramService;
    private final MessageService messageService;
    private final ApplicationConfig applicationConfig;

    @Autowired
    public SendPostsToChannelCallback(CommandHelper commandHelper, InstagramService instagramService,
                                      MessageService messageService, ApplicationConfig applicationConfig) {
        this.commandHelper = commandHelper;
        this.instagramService = instagramService;
        this.messageService = messageService;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public void execute(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        var callbackType = getCallbackType().toString();
        var chatId = message.getChatId().toString();
        var user = callbackQuery.getFrom();

        if (commandHelper.checkUserHasPermission(user)) {
            var amount = getAmount(callbackQuery.getData());
            instagramService.getUserLastPosts(amount)
                    .thenAccept(postsInformation -> {
                        if (postsInformation != null && postsInformation.size() > 0) {

                            var channelId = applicationConfig.getChannelId();

                            postsInformation.forEach(postInformation ->
                                    commandHelper.sendPhotoToChannel(postInformation, channelId, chatId, user, callbackType));

                        } else {
                            commandHelper.sendErrorMessage(chatId, user, callbackType);
                        }

                        commandHelper.deleteMessageInlineMarkup(chatId, user, message.getMessageId(), callbackQuery.getInlineMessageId(), callbackType);
                    });
        } else {
            commandHelper.sendPermissionDeniedMessage(chatId, user, callbackType);
        }
    }

    @Override
    public CallbackType getCallbackType() {
        return CallbackType.SEND_POSTS_TO_CHANNEL;
    }

    private int getAmount(String callbackData) {
        var amount = callbackData.replace(messageService.getText("callback.send_posts_to_channel") + "_", "");
        return Integer.parseInt(amount);
    }
}

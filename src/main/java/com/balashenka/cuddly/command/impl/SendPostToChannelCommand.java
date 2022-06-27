package com.balashenka.cuddly.command.impl;

import com.balashenka.cuddly.command.BaseCommand;
import com.balashenka.cuddly.service.InstagramService;
import com.balashenka.cuddly.config.ApplicationConfig;
import com.balashenka.cuddly.entity.CommandType;
import com.balashenka.cuddly.util.CommandHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class SendPostToChannelCommand implements BaseCommand {
    private final CommandHelper commandHelper;
    private final InstagramService instagramService;
    private final ApplicationConfig applicationConfig;

    @Autowired
    public SendPostToChannelCommand(CommandHelper commandHelper,
                                    InstagramService instagramService, ApplicationConfig applicationConfig) {
        this.commandHelper = commandHelper;
        this.instagramService = instagramService;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public void execute(Message message) {
        var commandType = getCommandType().toString();
        var chatId = message.getChatId().toString();
        var user = message.getFrom();

        if (commandHelper.checkUserHasPermission(user)) {
            instagramService.getUserLastPosts(1)
                    .thenAccept(postsInformation -> {
                        if (postsInformation != null && postsInformation.size() > 0) {
                            var postInformation = postsInformation.get(0);
                            var channelId = applicationConfig.getChannelId();

                            commandHelper.sendPhotoToChannel(postInformation, channelId, chatId, user, commandType);
                        } else {
                            commandHelper.sendErrorMessage(chatId, user, commandType);
                        }
                    });
        } else {
            commandHelper.sendPermissionDeniedMessage(chatId, message.getFrom(), commandType);
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.SEND_POST_TO_CHANNEL;
    }
}

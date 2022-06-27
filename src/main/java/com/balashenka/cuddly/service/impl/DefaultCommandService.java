package com.balashenka.cuddly.service.impl;

import com.balashenka.cuddly.service.CommandService;
import com.balashenka.cuddly.entity.CommandType;
import com.balashenka.cuddly.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

@Service
public class DefaultCommandService implements CommandService {
    private final Map<String, CommandType> commands;

    @Autowired
    public DefaultCommandService(MessageService messageService) {
        commands = Map.of(
                messageService.getText("command.start"), CommandType.START,
                messageService.getText("command.send_post_to_channel"), CommandType.SEND_POST_TO_CHANNEL,
                messageService.getText("command.send_posts_to_channel"), CommandType.SEND_POSTS_TO_CHANNEL
        );
    }

    @Override
    public CommandType getCommandType(Message message) {
        var text = message.getText();
        CommandType commandType = null;

        if (text != null) {
            commandType = commands.get(message.getText());
        }

        return commandType;
    }
}

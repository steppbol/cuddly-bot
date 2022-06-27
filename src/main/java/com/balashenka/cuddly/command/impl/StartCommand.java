package com.balashenka.cuddly.command.impl;

import com.balashenka.cuddly.command.BaseCommand;
import com.balashenka.cuddly.service.MessageService;
import com.balashenka.cuddly.entity.CommandType;
import com.balashenka.cuddly.util.CommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Component
public class StartCommand implements BaseCommand {
    private final MessageService messageService;
    private final CommandHelper commandHelper;

    @Autowired
    public StartCommand(MessageService messageService, CommandHelper commandHelper) {
        this.messageService = messageService;
        this.commandHelper = commandHelper;
    }

    @Override
    public void execute(Message message) {
        var commandType = getCommandType().toString();
        var chatId = message.getChatId().toString();
        var user = message.getFrom();

        if (commandHelper.checkUserHasPermission(user)) {
            commandHelper.sendMessageWithReplyMarkup(messageService.getText("message.start"), buildReplyKeyboardMarkup(),
                    chatId, message.getMessageId(), user, commandType);
        } else {
            commandHelper.sendPermissionDeniedMessage(chatId, user, commandType);
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.START;
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup() {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        var keyboard = new ArrayList<KeyboardRow>();
        var firstRow = new KeyboardRow();
        var secondRow = new KeyboardRow();

        firstRow.add(new KeyboardButton(messageService.getText("button_text.send_post_to_channel")));
        secondRow.add(new KeyboardButton(messageService.getText("button_text.send_posts_to_channel")));

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }
}

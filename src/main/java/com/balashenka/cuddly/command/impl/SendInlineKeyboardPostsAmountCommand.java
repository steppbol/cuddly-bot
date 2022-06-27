package com.balashenka.cuddly.command.impl;

import com.balashenka.cuddly.command.BaseCommand;
import com.balashenka.cuddly.entity.CommandType;
import com.balashenka.cuddly.service.MessageService;
import com.balashenka.cuddly.util.CommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class SendInlineKeyboardPostsAmountCommand implements BaseCommand {
    private final CommandHelper commandHelper;
    private final MessageService messageService;

    @Autowired
    public SendInlineKeyboardPostsAmountCommand(CommandHelper commandHelper, MessageService messageService) {
        this.commandHelper = commandHelper;
        this.messageService = messageService;
    }

    @Override
    public void execute(Message message) {
        var commandType = getCommandType().toString();
        var chatId = message.getChatId().toString();
        var user = message.getFrom();

        if (commandHelper.checkUserHasPermission(user)) {
            commandHelper.sendMessageWithInlineMarkup(messageService.getText("message.post_amount"),
                    buildPostsAmountInlineKeyboardMarkup(), chatId, message.getMessageId(), user, commandType);
        } else {
            commandHelper.sendPermissionDeniedMessage(chatId, message.getFrom(), commandType);
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.SEND_POSTS_TO_CHANNEL;
    }

    private InlineKeyboardMarkup buildPostsAmountInlineKeyboardMarkup() {
        var markupInline = new InlineKeyboardMarkup();
        var rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        var rowInline = new ArrayList<InlineKeyboardButton>();

        IntStream.rangeClosed(2, 7)
                .forEach(amount -> rowInline.add(buildInlineKeyboardButton(String.valueOf(amount),
                        messageService.getText("callback.send_posts_to_channel") + "_" + amount)));

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private InlineKeyboardButton buildInlineKeyboardButton(String text, String callbackData) {
        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);

        return inlineKeyboardButton;
    }
}

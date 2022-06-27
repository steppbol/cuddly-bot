package com.balashenka.cuddly.command;

import com.balashenka.cuddly.entity.CommandType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface BaseCommand {
    void execute(Message message);

    CommandType getCommandType();
}

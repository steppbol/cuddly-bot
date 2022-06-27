package com.balashenka.cuddly.service;

import com.balashenka.cuddly.entity.CommandType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandService {
    CommandType getCommandType(Message message);
}

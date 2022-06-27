package com.balashenka.cuddly.bot;

import com.balashenka.cuddly.callback.BaseCallback;
import com.balashenka.cuddly.command.BaseCommand;
import com.balashenka.cuddly.entity.CallbackType;
import com.balashenka.cuddly.entity.CommandType;
import com.balashenka.cuddly.service.CallbackService;
import com.balashenka.cuddly.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotContext {
    private final CommandService commandService;
    private final CallbackService callbackService;

    private final Map<CommandType, BaseCommand> commands = new HashMap<>();
    private final Map<CallbackType, BaseCallback> callbacks = new HashMap<>();

    @Autowired
    public BotContext(CommandService commandService, List<BaseCommand> commands,
                      CallbackService callbackService, List<BaseCallback> callbacks) {
        commands.forEach(command -> this.commands.put(command.getCommandType(), command));
        callbacks.forEach(callback -> this.callbacks.put(callback.getCallbackType(), callback));

        this.commandService = commandService;
        this.callbackService = callbackService;
    }

    public BaseCommand handleInputCommand(Message message) {
        var commandType = commandService.getCommandType(message);

        BaseCommand baseCommand = null;

        if (commandType != null) {
            baseCommand = findCommand(commandType);
        }

        return baseCommand;
    }

    public BaseCallback handleInputCallback(String callbackData) {
        var callbackType = callbackService.getCallbackType(callbackData);

        BaseCallback baseCallback = null;

        if (callbackType != null) {
            baseCallback = findCallback(callbackType);
        }

        return baseCallback;
    }

    private BaseCommand findCommand(CommandType commandType) {
        return commands.get(commandType);
    }

    private BaseCallback findCallback(CallbackType callbackType) {
        return callbacks.get(callbackType);
    }
}

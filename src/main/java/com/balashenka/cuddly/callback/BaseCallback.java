package com.balashenka.cuddly.callback;

import com.balashenka.cuddly.entity.CallbackType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface BaseCallback {
    void execute(CallbackQuery callbackQuery);

    CallbackType getCallbackType();
}

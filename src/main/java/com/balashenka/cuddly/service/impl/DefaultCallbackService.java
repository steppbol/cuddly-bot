package com.balashenka.cuddly.service.impl;

import com.balashenka.cuddly.entity.CallbackType;
import com.balashenka.cuddly.service.CallbackService;
import com.balashenka.cuddly.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultCallbackService implements CallbackService {
    private final Map<String, CallbackType> callbacks;

    @Autowired
    public DefaultCallbackService(MessageService messageService) {

        callbacks = Map.of(
                messageService.getText("callback.send_posts_to_channel"), CallbackType.SEND_POSTS_TO_CHANNEL
        );
    }

    @Override
    public CallbackType getCallbackType(String callbackData) {
        CallbackType callbackType = null;

        for (var callback : callbacks.entrySet()) {
            var query = callback.getKey();

            if (callbackData.contains(query)) {
                callbackType = callbacks.get(query);
                break;
            }
        }

        return callbackType;
    }
}

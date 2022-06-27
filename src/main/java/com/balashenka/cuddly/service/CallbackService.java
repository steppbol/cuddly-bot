package com.balashenka.cuddly.service;

import com.balashenka.cuddly.entity.CallbackType;

public interface CallbackService {
    CallbackType getCallbackType(String callbackData);
}

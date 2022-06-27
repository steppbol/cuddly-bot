package com.balashenka.cuddly.service.impl;


import com.balashenka.cuddly.service.LocaleService;
import com.balashenka.cuddly.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultMessageService implements MessageService {
    private final LocaleService localMessageService;

    @Autowired
    public DefaultMessageService(LocaleService localMessageService) {
        this.localMessageService = localMessageService;
    }

    @Override
    public String getText(String text) {
        return localMessageService.getMessage(text);
    }
}

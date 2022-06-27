package com.balashenka.cuddly.service.impl;

import com.balashenka.cuddly.config.ApplicationConfig;
import com.balashenka.cuddly.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class DefaultLocaleService implements LocaleService {
    private final Locale locale;
    private final MessageSource messageSource;

    @Autowired
    public DefaultLocaleService(MessageSource messageSource, ApplicationConfig applicationConfig) {
        this.messageSource = messageSource;
        this.locale = Locale.forLanguageTag(applicationConfig.getLocaleTag());
    }

    @Override
    public String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }
}

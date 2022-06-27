package com.balashenka.cuddly.listener;

import com.balashenka.cuddly.client.TelegramWebClient;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextEventListener {
    private final TelegramWebClient telegramClient;

    public ApplicationContextEventListener(TelegramWebClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @EventListener
    public void handleContextRefreshEvent(ContextRefreshedEvent contextRefreshedEvent) {
        telegramClient.setWebhookPath();
    }
}

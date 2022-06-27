package com.balashenka.cuddly.client.impl;

import com.balashenka.cuddly.client.TelegramWebClient;
import com.balashenka.cuddly.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Component
public class DefaultTelegramWebClient implements TelegramWebClient {
    private static final String TELEGRAM_BOT_URL = "https://api.telegram.org/bot";

    private final RestTemplate restTemplate;
    private final ApplicationConfig applicationConfig;

    public DefaultTelegramWebClient(RestTemplate restTemplate, ApplicationConfig applicationConfig) {
        this.restTemplate = restTemplate;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public void setWebhookPath() {
        var finalUrl = TELEGRAM_BOT_URL +
                applicationConfig.getBotToken() +
                "/setWebhook?url=" +
                applicationConfig.getWebhookPath();

        var response = restTemplate.exchange(finalUrl, HttpMethod.GET,
                getHttpEntityHeader(), Void.class);

        log.info("Set webhook path : {}, status : {}", applicationConfig.getWebhookPath(), response.getStatusCode());
    }

    private HttpEntity<String> getHttpEntityHeader() {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("", headers);
    }
}

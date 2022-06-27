package com.balashenka.cuddly.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ComponentScan(basePackages = "com.balashenka.cuddly")
public class ApplicationConfig {
    @Value(value = "${application.name}")
    private String applicationName;
    @Value(value = "${bot.username}")
    private String botUsername;
    @Value(value = "${bot.token}")
    private String botToken;
    @Value(value = "${bot.webhook.path}")
    private String webhookPath;
    @Value(value = "${bot.channel-id}")
    private String channelId;
    @Value(value = "${bot.users-whitelist}")
    private String usersWhitelist;
    @Value(value = "${instagram.username}")
    private String instagramUsername;
    @Value(value = "${instagram.password}")
    private String instagramPassword;
    @Value(value = "${instagram.account}")
    private String instagramAccount;
    @Value("${locale.tag}")
    private String localeTag;
}

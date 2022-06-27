package com.balashenka.cuddly.controller;


import com.balashenka.cuddly.bot.BotFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebhookController {
    private final BotFacade botFacade;

    @Autowired
    public WebhookController(BotFacade botFacade) {
        this.botFacade = botFacade;
    }

    @PostMapping(value = "/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return botFacade.onUpdateReceived(update);
    }
}

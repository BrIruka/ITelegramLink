package com.iruka.ITelegramLink.telegram;

import com.iruka.ITelegramLink.ITelegramLink;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.util.UUID;

public class TelegramBot extends TelegramLongPollingBot {
    private final ITelegramLink plugin;
    private final String botToken;
    private final String botUsername;
    private BotSession session;

    public TelegramBot(ITelegramLink plugin) {
        this.plugin = plugin;
        this.botToken = plugin.getConfig().getString("telegram.bot-token");
        this.botUsername = plugin.getConfig().getString("telegram.bot-username");
    }

    public void setSession(BotSession session) {
        this.session = session;
    }

    public void stopBot() {
        if (session != null && session.isRunning()) {
            session.stop();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (plugin.getLinkCodes().containsKey(messageText)) {
            UUID playerUUID = plugin.getLinkCodes().get(messageText);
            plugin.getStorage().linkAccount(playerUUID, chatId);
            plugin.getLinkCodes().remove(messageText);

            sendMessage(chatId, plugin.getLang().get("telegram.account-linked"));
        } else {
            sendMessage(chatId, plugin.getLang().get("telegram.send-code"));
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            plugin.getLogger().warning("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
        }
    }
}
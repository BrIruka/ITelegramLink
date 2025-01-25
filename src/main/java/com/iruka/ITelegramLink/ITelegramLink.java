package com.iruka.ITelegramLink;

import com.iruka.ITelegramLink.commands.TelegramAdminCommand;
import com.iruka.ITelegramLink.commands.TelegramCommand;
import com.iruka.ITelegramLink.listeners.PlayerListener;
import com.iruka.ITelegramLink.storage.JsonStorage;
import com.iruka.ITelegramLink.storage.Storage;
import com.iruka.ITelegramLink.telegram.TelegramBot;
import com.iruka.ITelegramLink.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ITelegramLink extends JavaPlugin {
    private TelegramBot telegramBot;
    private TelegramBotsApi telegramBotsApi;
    private final Map<String, UUID> linkCodes = new HashMap<>();
    private Storage storage;
    private Lang lang;

    @Override
    public void onEnable() {
        // Загрузка конфигурации
        saveDefaultConfig();

        storage = new JsonStorage(this);
        lang = new Lang(this);


        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);


        // Регистрация команд
        getCommand("telegram").setExecutor(new TelegramCommand(this));
        getCommand("telegramadmin").setExecutor(new TelegramAdminCommand(this));

        // Инициализация Telegram бота
        try {
            initializeBot();
        } catch (TelegramApiException e) {
            getLogger().severe("Ошибка при запуске Telegram бота: " + e.getMessage());
        }

        Bukkit.getConsoleSender().sendMessage("§2╔════════════════════════════════════");
        Bukkit.getConsoleSender().sendMessage("§2║ §bITelegramLink §7v" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("§2║ §7Author: §fIrukaMine");
        Bukkit.getConsoleSender().sendMessage("§2║ §7Site: §7https://iruka-shop.site/");
        Bukkit.getConsoleSender().sendMessage("§2║ §7Status: §aEnabled");
        Bukkit.getConsoleSender().sendMessage("§2╚════════════════════════════════════");
    }

    @Override
    public void onDisable() {
        if (telegramBot != null) {
            telegramBot.stopBot();
            telegramBot = null;
        }
        getLogger().info("ITelegramLink выключен!");
    }

    public Map<String, UUID> getLinkCodes() {
        return linkCodes;
    }

    public Storage getStorage() {
        return storage;
    }

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }

    public void initializeBot() throws TelegramApiException {
        if (telegramBot != null) {
            telegramBot.stopBot();
            telegramBot = null;
        }

        telegramBot = new TelegramBot(this);

        telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        BotSession session = telegramBotsApi.registerBot(telegramBot);
        telegramBot.setSession(session);

        getLogger().info("Telegram бот успешно инициализирован!");
    }

    public Lang getLang() {
        return lang;
    }
}
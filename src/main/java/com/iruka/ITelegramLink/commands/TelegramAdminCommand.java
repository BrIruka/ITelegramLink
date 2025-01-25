package com.iruka.ITelegramLink.commands;

import com.iruka.ITelegramLink.ITelegramLink;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class TelegramAdminCommand implements CommandExecutor, TabCompleter {
    private final ITelegramLink plugin;

    public TelegramAdminCommand(ITelegramLink plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("telegramlink.admin")) {
            sender.sendMessage(plugin.getLang().get("commands.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reloadPlugin(sender);
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void reloadPlugin(CommandSender sender) {
        sender.sendMessage(plugin.getLang().get("commands.reload.start"));

        // Выполняем перезагрузку в отдельном потоке
        new Thread(() -> {
            try {
                // Отключаем старого бота
                if (plugin.getTelegramBot() != null) {
                    plugin.getTelegramBot().stopBot();
                }

                Thread.sleep(100);

                // Возвращаемся в основной поток для безопасной работы с Bukkit API
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    try {
                        // Перезагружаем конфиг и другие компоненты
                        plugin.reloadConfig();
                        plugin.getLang().loadLanguage();
                        plugin.getStorage().load();
                        plugin.initializeBot();

                        sender.sendMessage(plugin.getLang().get("commands.reload.success"));
                    } catch (Exception e) {
                        sender.sendMessage(plugin.getLang().get("commands.reload.error", "{error}", e.getMessage()));
                        plugin.getLogger().severe("Ошибка при перезагрузке бота: " + e.getMessage());
                    }
                }, 2L); // Задержка в тиках (2 тика = 100мс)
            } catch (Exception e) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(plugin.getLang().get("commands.reload.error", "{error}", e.getMessage()));
                    plugin.getLogger().severe("Ошибка при перезагрузке бота: " + e.getMessage());
                });
            }
        }, "TelegramLink-Reload").start(); // Имя потока для лучшей отладки
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getLang().get("commands.admin-help.title"));
        sender.sendMessage(plugin.getLang().get("commands.admin-help.reload"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("telegramlink.admin")) {
            completions.add("reload");
        }

        return completions;
    }
}
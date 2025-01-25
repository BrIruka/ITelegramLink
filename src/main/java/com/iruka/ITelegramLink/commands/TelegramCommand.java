package com.iruka.ITelegramLink.commands;

import com.iruka.ITelegramLink.ITelegramLink;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TelegramCommand implements CommandExecutor {
    private final ITelegramLink plugin;

    public TelegramCommand(ITelegramLink plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLang().get("commands.player-only"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "link" -> {
                if (plugin.getStorage().isLinked(player.getUniqueId())) {
                    player.sendMessage(plugin.getLang().get("commands.link.already-linked"));
                    return true;
                }
                String code = generateLinkCode(player);
                player.sendMessage(plugin.getLang().get("commands.link.code-generated", "{code}", code));
                player.sendMessage(plugin.getLang().get("commands.link.send-to-bot",
                        "{bot}", plugin.getConfig().getString("telegram.bot-username")));
            }
            case "unlink" -> {
                if (!plugin.getStorage().isLinked(player.getUniqueId())) {
                    player.sendMessage(plugin.getLang().get("commands.unlink.not-linked"));
                    return true;
                }
                plugin.getStorage().unlinkAccount(player.getUniqueId());
                player.sendMessage(plugin.getLang().get("commands.unlink.success"));
            }
            default -> sendHelp(player);
        }
        return true;
    }

    private String generateLinkCode(Player player) {
        // Генерируем случайный 6-значный код
        int code = 100000 + (int)(Math.random() * 900000);
        plugin.getLinkCodes().put(String.valueOf(code), player.getUniqueId());
        return String.valueOf(code);
    }

    private void sendHelp(Player player) {
        player.sendMessage(plugin.getLang().get("commands.help.title"));
        player.sendMessage(plugin.getLang().get("commands.help.link"));
        player.sendMessage(plugin.getLang().get("commands.help.unlink"));
    }
}
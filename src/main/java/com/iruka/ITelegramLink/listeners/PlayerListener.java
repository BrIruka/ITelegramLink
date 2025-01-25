package com.iruka.ITelegramLink.listeners;

import com.iruka.ITelegramLink.ITelegramLink;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final ITelegramLink plugin;

    public PlayerListener(ITelegramLink plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Long telegramId = plugin.getStorage().getTelegramId(event.getPlayer().getUniqueId());
        if (telegramId != null) {
            plugin.getTelegramBot().sendMessage(telegramId,
                    plugin.getLang().get("telegram.player-join", "{player}", event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Long telegramId = plugin.getStorage().getTelegramId(event.getPlayer().getUniqueId());
        if (telegramId != null) {
            plugin.getTelegramBot().sendMessage(telegramId,
                    plugin.getLang().get("telegram.player-quit", "{player}", event.getPlayer().getName()));
        }
    }
}
package com.iruka.ITelegramLink.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.iruka.ITelegramLink.ITelegramLink;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonStorage implements Storage {
    private final File storageFile;
    private final Gson gson;
    private Map<UUID, Long> linkedAccounts;
    private final ITelegramLink plugin;

    public JsonStorage(ITelegramLink plugin) {
        this.plugin = plugin;
        this.storageFile = new File(plugin.getDataFolder(), "linked-accounts.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.linkedAccounts = new HashMap<>();
        load();
    }

    @Override
    public void linkAccount(UUID playerUUID, long telegramId) {
        linkedAccounts.put(playerUUID, telegramId);
        save();
    }

    @Override
    public void unlinkAccount(UUID playerUUID) {
        linkedAccounts.remove(playerUUID);
        save();
    }

    @Override
    public boolean isLinked(UUID playerUUID) {
        return linkedAccounts.containsKey(playerUUID);
    }

    @Override
    public Long getTelegramId(UUID playerUUID) {
        return linkedAccounts.get(playerUUID);
    }

    @Override
    public void load() {
        if (!storageFile.exists()) {
            linkedAccounts = new HashMap<>();
            return;
        }

        try (Reader reader = new FileReader(storageFile)) {
            Type type = new TypeToken<HashMap<UUID, Long>>(){}.getType();
            linkedAccounts = gson.fromJson(reader, type);
            if (linkedAccounts == null) {
                linkedAccounts = new HashMap<>();
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка при загрузке данных: " + e.getMessage());
            linkedAccounts = new HashMap<>();
        }
    }

    @Override
    public void save() {
        try (Writer writer = new FileWriter(storageFile)) {
            gson.toJson(linkedAccounts, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
}
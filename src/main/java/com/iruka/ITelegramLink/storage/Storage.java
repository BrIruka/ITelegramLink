package com.iruka.ITelegramLink.storage;

import java.util.UUID;

public interface Storage {
    void linkAccount(UUID playerUUID, long telegramId);
    void unlinkAccount(UUID playerUUID);
    boolean isLinked(UUID playerUUID);
    Long getTelegramId(UUID playerUUID);
    void load();
    void save();
}
package com.iruka.ITelegramLink.utils;

import com.iruka.ITelegramLink.ITelegramLink;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Lang {
    private final ITelegramLink plugin;
    private YamlConfiguration lang;
    private File langFile;
    private String currentLang;

    public Lang(ITelegramLink plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        // Получаем язык из конфига
        currentLang = plugin.getConfig().getString("language", "en");

        // Создаем папку lang если её нет
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // Сохраняем все файлы локализации из ресурсов
        saveDefaultLangs(langFolder);

        // Загружаем выбранный язык
        langFile = new File(langFolder, currentLang + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().warning("Файл локализации " + currentLang + ".yml не найден! Используем английский язык.");
            langFile = new File(langFolder, "en.yml");
        }

        lang = YamlConfiguration.loadConfiguration(langFile);

        // Загружаем дефолтный файл из ресурсов для сравнения
        InputStream defLangStream = plugin.getResource("lang/" + currentLang + ".yml");
        if (defLangStream != null) {
            YamlConfiguration defLang = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defLangStream, StandardCharsets.UTF_8));
            lang.setDefaults(defLang);
        }
    }

    private void saveDefaultLangs(File langFolder) {
        String[] languages = {"en", "ru", "uk", "pl", "zh", "fr", "es"};
        for (String lang : languages) {
            File langFile = new File(langFolder, lang + ".yml");
            if (!langFile.exists()) {
                plugin.saveResource("lang/" + lang + ".yml", false);
            }
        }
    }

    public String get(String path) {
        String message = lang.getString(path);
        if (message == null) {
            return "§cMissing text: " + path;
        }

        if (message.contains("{prefix}")) {
            String prefix = lang.getString("prefix", "&6[TelegramLink] &r");
            message = message.replace("{prefix}", prefix);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String get(String path, String... replacements) {
        String message = get(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }
}
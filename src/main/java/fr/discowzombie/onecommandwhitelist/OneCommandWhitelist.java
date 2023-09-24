package fr.discowzombie.onecommandwhitelist;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.discowzombie.onecommandwhitelist.commands.OneWhitelistCommand;
import fr.discowzombie.onecommandwhitelist.config.MainConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;

public final class OneCommandWhitelist extends JavaPlugin {

    private final ObjectMapper mapper = createMapper();
    private MainConfiguration configuration;

    @Override
    public void onEnable() {
        try {
            // Create the plugin data folder
            this.getDataFolder().mkdirs();

            this.configuration = this.copyAndLoadJson(
                    Objects.requireNonNull(getResource("config.json")),
                    new File(this.getDataFolder(), "config.json"),
                    MainConfiguration.class);

            Objects.requireNonNull(getCommand("onewl")).setExecutor(new OneWhitelistCommand(this.configuration));
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Unable to initialize the plugin", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public MainConfiguration getConfiguration() {
        return this.configuration;
    }

    private <T> T copyAndLoadJson(InputStream srcFile, File file, Class<T> tClass) throws IOException {
        if (!file.exists()) {
            Files.copy(srcFile, file.toPath());
        }
        return this.mapper.readValue(file, tClass);
    }

    private ObjectMapper createMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return mapper;
    }
}

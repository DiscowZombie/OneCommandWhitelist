package fr.discowzombie.onecommandwhitelist.model;

import fr.discowzombie.onecommandwhitelist.OneCommandWhitelist;
import fr.discowzombie.onecommandwhitelist.config.MainConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Consumer;

public enum WhitelistMode {

    COMMAND {
        @Override
        public void applyWhitelist(String[] usernames, Runnable onSuccess) {
            final OneCommandWhitelist plugin = OneCommandWhitelist.getPlugin(OneCommandWhitelist.class);
            final MainConfiguration configuration = plugin.getConfiguration();

            // Whitelist one player each 4 ticks, to prevent overloading the server
            int count = 0;
            for (final String username : usernames) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format(configuration.command, username));
                }, count * 4L);
                ++count;
            }

            Bukkit.getScheduler().runTaskLater(plugin, onSuccess, count * 4L);
        }
    },

    ;

    public abstract void applyWhitelist(String[] usernames, Runnable onSuccess);
}

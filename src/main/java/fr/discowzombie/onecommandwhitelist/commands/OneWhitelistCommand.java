package fr.discowzombie.onecommandwhitelist.commands;

import fr.discowzombie.onecommandwhitelist.OneCommandWhitelist;
import fr.discowzombie.onecommandwhitelist.config.MainConfiguration;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class OneWhitelistCommand implements CommandExecutor {

    private final MainConfiguration configuration;

    public OneWhitelistCommand(MainConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            final String id = args[0];

            Unirest.get(String.format("%s/%s", this.configuration.url, id))
                    .header("Accept", "application/json")
                    .asObjectAsync(String[].class)
                    .thenAccept(response -> {
                        if (!response.isSuccess()) {
                            sender.sendMessage("§cErreur: Mauvaise réponse du serveur.");
                            OneCommandWhitelist.getPlugin(OneCommandWhitelist.class).getLogger().log(Level.INFO, String.format(
                                    "Bad server response. Status = %s, Body = %s", response.getStatus(),
                                    Arrays.toString(response.getBody())));
                            response.getParsingError().ifPresent(Throwable::printStackTrace);
                            return;
                        }
                        this.configuration.mode.applyWhitelist(response.getBody(), () -> sender.sendMessage("§aWhitelist appliquée avec succès."));
                        sender.sendMessage("§aApplication de la whitelist en cours...");
                    })
                    .exceptionally(exc -> {
                        OneCommandWhitelist.getPlugin(OneCommandWhitelist.class).getLogger().log(Level.SEVERE,
                                "Unable to retrieve whitelist", exc);
                        sender.sendMessage("§cErreur: Une erreur est survenue lors du chargement de la whitelist.");
                        return null;
                    });

            return true;
        }

        sender.sendMessage("§cUsage: /onewl <code>");
        return true;
    }
}

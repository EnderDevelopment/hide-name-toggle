package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Main extends JavaPlugin implements Listener {
    private final Set<UUID> hiddenViewers = new HashSet<>();

    @Override
    public void onEnable() {
        for (String value : getConfig().getStringList("hidden-viewers")) {
            try {
                hiddenViewers.add(UUID.fromString(value));
            } catch (IllegalArgumentException ignored) {
                getLogger().warning("Ignoring invalid hidden viewer UUID: " + value);
            }
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            applyViewer(viewer);
        }
    }

    @Override
    public void onDisable() {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            clearViewerTeams(viewer);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("hide")) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }
        if (args.length != 1 || !args[0].equalsIgnoreCase("name")) {
            player.sendMessage(Component.text("Usage: /hide name", NamedTextColor.YELLOW));
            return true;
        }

        UUID viewerId = player.getUniqueId();
        boolean nowHidden;
        if (hiddenViewers.remove(viewerId)) {
            nowHidden = false;
        } else {
            hiddenViewers.add(viewerId);
            nowHidden = true;
        }
        saveHiddenViewers();
        applyViewer(player);
        player.sendMessage(Component.text("Player nametags are now " + (nowHidden ? "hidden" : "visible") + " to you.", nowHidden ? NamedTextColor.GREEN : NamedTextColor.YELLOW));
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joining = event.getPlayer();
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (hiddenViewers.contains(viewer.getUniqueId())) {
                hideTarget(viewer, joining);
            }
        }
        applyViewer(joining);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player leaving = event.getPlayer();
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (viewer.equals(leaving)) {
                continue;
            }
            Team team = viewer.getScoreboard().getTeam(teamName(leaving.getUniqueId()));
            if (team != null) {
                team.removeEntry(leaving.getName());
                team.unregister();
            }
        }
    }

    private void applyViewer(Player viewer) {
        clearViewerTeams(viewer);
        if (!hiddenViewers.contains(viewer.getUniqueId())) {
            return;
        }
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(viewer)) {
                hideTarget(viewer, target);
            }
        }
    }

    private void hideTarget(Player viewer, Player target) {
        Scoreboard scoreboard = viewer.getScoreboard();
        if (scoreboard.getEntryTeam(target.getName()) != null) {
            return;
        }
        String name = teamName(target.getUniqueId());
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        team.addEntry(target.getName());
    }

    private void clearViewerTeams(Player viewer) {
        Scoreboard scoreboard = viewer.getScoreboard();
        for (Player target : Bukkit.getOnlinePlayers()) {
            Team team = scoreboard.getTeam(teamName(target.getUniqueId()));
            if (team != null) {
                team.removeEntry(target.getName());
                team.unregister();
            }
        }
    }

    private String teamName(UUID playerId) {
        return "hn" + playerId.toString().replace("-", "").substring(0, 13);
    }

    private void saveHiddenViewers() {
        List<String> values = new ArrayList<>();
        for (UUID uuid : hiddenViewers) {
            values.add(uuid.toString());
        }
        getConfig().set("hidden-viewers", values);
        saveConfig();
    }
}

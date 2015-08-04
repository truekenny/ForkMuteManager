package com.cnaude.mutemanager;

import org.apache.commons.lang.time.DateUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Date;

import static com.cnaude.mutemanager.MuteManager.config;

/**
 *
 * @author cnaude
 */
public class MMListeners implements Listener {

    private final MuteManager plugin;

    public MMListeners(MuteManager instance) {
        this.plugin = instance;
    }

    public static String convertFullIPToIP(String fullIP) {
        return fullIP.split("/")[1].split(":")[0];
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        String[] data = plugin.getMConfig().needHitIt(event.getMessage());

        if (data != null) {
            event.setCancelled(true);
            plugin.logInfo("GOAWAY: " + event.getPlayer().getDisplayName() + ": " + event.getMessage());

            final String reason = data[0];
            String type = data[1];
            int minutes = Integer.parseInt(data[2]);
            if (type.equals("mute")) {
                minutes = (minutes == 0) ? 60 * 5 : minutes;
                plugin.mutePlayer(event.getPlayer(), (long) minutes, plugin.getServer().getConsoleSender(), reason);
            } else if (type.equals("ban")) {
                plugin.getServer().getBanList(BanList.Type.IP).addBan(
                        convertFullIPToIP(player.getAddress().toString()),
                        reason,
                        (minutes == 0) ? null : DateUtils.addMinutes(new Date(), minutes),
                        plugin.getServer().getConsoleSender().getName()
                );
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    public void run() {
                        player.kickPlayer(reason);
                    }
                });
            } else if (type.equals("kick")) {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    public void run() {
                        player.kickPlayer(reason);
                    }
                });
            }
        }

        if (plugin.isMuted(player)) {
            MutedPlayer mutedPlayer = plugin.getMutedPlayer(player);
            if (mutedPlayer == null) {
                return;
            }
            event.setCancelled(true);
            if (!config.msgYouAreMuted().isEmpty()) {                   
                player.sendMessage(config.msgYouAreMuted()
                        .replace("%DURATION%", mutedPlayer.getExpiredTime(config))
                        .replace("%REASON%", mutedPlayer.getReason())
                );
            }
            if (plugin.getMConfig().adminListen()) {
                String bCastMessage = ChatColor.WHITE + "[" + ChatColor.RED + "Mute" + ChatColor.WHITE + "]";
                bCastMessage = bCastMessage + "<" + player.getName() + "> ";
                bCastMessage = bCastMessage + ChatColor.GRAY + event.getMessage();
                Bukkit.getServer().broadcast(bCastMessage, plugin.getMConfig().broadcastNode());
            }
        } else {
            plugin.unMutePlayer(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String attemptedCmd = event.getMessage().split(" ")[0];
        if (plugin.isMuted(player) && plugin.isBlockedCmd(attemptedCmd)) {
            MutedPlayer mutedPlayer = plugin.getMutedPlayer(player);
            if (mutedPlayer == null) {
                return;
            }
            event.setCancelled(true);
            if (!config.msgYouAreMuted().isEmpty()) {                
                player.sendMessage(config.msgYouAreMuted()
                        .replace("%DURATION%", mutedPlayer.getExpiredTime(config))
                        .replace("%REASON%", mutedPlayer.getReason())
                );
            }
            if (plugin.getMConfig().adminListen()) {
                String bCastMessage = ChatColor.WHITE + "[" + ChatColor.RED + "Mute" + ChatColor.WHITE + "]";
                bCastMessage = bCastMessage + "<" + player.getName() + "> ";
                bCastMessage = bCastMessage + ChatColor.GRAY + event.getMessage();
                Bukkit.getServer().broadcast(bCastMessage, plugin.getMConfig().broadcastNode());
            }
        }
    }
}

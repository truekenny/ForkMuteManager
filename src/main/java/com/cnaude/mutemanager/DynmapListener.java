package com.cnaude.mutemanager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapWebChatEvent;

public class DynmapListener implements Listener {

    private final MuteManager plugin;
    private DynmapCommonAPI dynmapAPI;

    /**
     * @param plugin
     */
    public DynmapListener(MuteManager plugin) {
        this.plugin = plugin;
        PluginManager pm = plugin.getServer().getPluginManager();
        Plugin dynmap = pm.getPlugin("dynmap");
        dynmapAPI = (DynmapCommonAPI) dynmap;
    }

    /**
     * @param event
     */
    @EventHandler
    public void onDynmapWebChatEvent(DynmapWebChatEvent event) {

        String message = event.getMessage();
        String name = event.getName().replaceAll("\\.", "_");
        String source = event.getSource();

        System.out.println("MM onDynmapWebChatEvent: source:" + source + ", name:" + name + ", message:" + message);

        if (name.equals("")) {
            name = "WebChatSystem";
        }

        if(plugin.isMuted(name)) {
            System.out.println("MM onDynmapWebChatEvent: Muted");
            event.setCancelled(true);
            if(dynmapAPI != null) {
                dynmapAPI.sendBroadcastToWeb("SERVER", "You are muted, message not delivered");
            }
        }
    }
}
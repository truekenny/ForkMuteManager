package com.cnaude.mutemanager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dynmap.DynmapWebChatEvent;

public class DynmapListener implements Listener {

    private final MuteManager plugin;

    /**
     * @param plugin
     */
    public DynmapListener(MuteManager plugin) {
        this.plugin = plugin;
    }

    /**
     * @param event
     */
    @EventHandler
    public void onDynmapWebChatEvent(DynmapWebChatEvent event) {
        event.setCancelled(true);

        String message = event.getMessage();
        String name = event.getName().replaceAll("\\.", "_");
        String source = event.getSource();

        System.out.println("onDynmapWebChatEvent: source:" + source + ", name:" + name + ", message:" + message);

        if (name.equals("")) {
            name = "WebChatSystem";
        }
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.mutemanager;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

public class MutedPlayer implements Serializable {

    private final UUID uuid;
    private final String playerName;
    private Long expTime;
    private String reason;

    public MutedPlayer(OfflinePlayer player, Long expTime, String reason) {
        uuid = player.getUniqueId();
        playerName = player.getName();
        this.expTime = expTime;
        this.reason = reason;
    }

    public MutedPlayer(String playerName, UUID uuid, Long expTime, String reason) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.expTime = expTime;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setExptime(long expTime) {
        this.expTime = expTime;
    }

    public boolean isMuted() {
        return expTime > System.currentTimeMillis();
    }

    public String getExpiredTime(MMConfig config) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        float diffTime = ((expTime - System.currentTimeMillis()) / 1000f) / 60f;
        if (diffTime > 5256000) {
            return config.msgForever();
        }
        if (diffTime > 525600) {
            return (formatter.format(diffTime / 525600f)) + " " + config.msgYears();
        }
        if (diffTime > 1440) {
            return (formatter.format(diffTime / 1440f)) + " " + config.msgDays();
        }
        if (diffTime > 60) {
            return (formatter.format(diffTime / 60f)) + " " + config.msgHOurs();
        }
        if (diffTime < 1f) {
            return (formatter.format(diffTime * 60f)) + " " + config.msgSeconds();
        }
        return (formatter.format(diffTime)) + " " + config.msgMinutes();
    }
    
    public long getExpTime() {
        return expTime;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public UUID getUUID() {
        return uuid;
    }
}

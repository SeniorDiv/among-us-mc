package com.nktfh100.AmongUs.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.nktfh100.AmongUs.enums.GameState;
import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.main.Main;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NamedEntitySpawnListener implements PacketListener {
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SPAWN_ENTITY) return;

        Player player = event.getPlayer();
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(event);
        Entity entity = SpigotConversionUtil.getEntityById(player.getWorld(), packet.getEntityId());

        if (entity instanceof Player) {
            PlayerInfo pInfoSentTo = Main.getPlayersManager().getPlayerInfo(event.getPlayer());
            if (pInfoSentTo == null) {
                return;
            }
            Arena arena = pInfoSentTo.getArena();
            if (pInfoSentTo.getIsIngame() && arena.getGameState() == GameState.RUNNING) {
                PlayerInfo pInfoSpawned = Main.getPlayersManager().getPlayerInfo((Player) entity);
                if (pInfoSpawned == null) {
                    return;
                }
                if (pInfoSpawned.getArena() == arena) {
                    if (pInfoSpawned.getIsInVent() || pInfoSpawned.getIsInCameras()) {
                        event.setCancelled(true);
                        return;
                    }
                    if ((!pInfoSentTo.isGhost() && !pInfoSpawned.isGhost()) && !arena.getIsInMeeting()) {
                        if (!arena.getVisibilityManager().canSee(pInfoSentTo, pInfoSpawned)) {
                            event.setCancelled(true);
                            return;
                        }
                    } else if ((!pInfoSentTo.isGhost() && pInfoSpawned.isGhost())) {
                        event.setCancelled(true);
                        return;
                    }
                    if (pInfoSentTo.isGhost() && pInfoSpawned.isGhost()) {
                        event.setCancelled(false);
                    }
                }
            }
        }
    }
}
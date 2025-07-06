package com.nktfh100.AmongUs.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.main.Main;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityListeners implements PacketListener {
    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();

        int entityId = switch (event.getPacketType()) {
            case PacketType.Play.Server.ENTITY_EQUIPMENT -> new WrapperPlayServerEntityEquipment(event).getEntityId();
            case PacketType.Play.Server.ENTITY_ANIMATION -> new WrapperPlayServerEntityAnimation(event).getEntityId();
            case PacketType.Play.Server.COLLECT_ITEM -> new WrapperPlayServerCollectItem(event).getCollectorEntityId();
            case PacketType.Play.Server.SPAWN_EXPERIENCE_ORB -> new WrapperPlayServerSpawnExperienceOrb(event).getEntityId();
            case PacketType.Play.Server.ENTITY_VELOCITY -> new WrapperPlayServerEntityVelocity(event).getEntityId();
            case PacketType.Play.Server.ENTITY_RELATIVE_MOVE -> new WrapperPlayServerEntityRelativeMove(event).getEntityId();
            case PacketType.Play.Server.ENTITY_HEAD_LOOK -> new WrapperPlayServerEntityHeadLook(event).getEntityId();
            case PacketType.Play.Server.ENTITY_TELEPORT -> new WrapperPlayServerEntityTeleport(event).getEntityId();
            case PacketType.Play.Server.ENTITY_ROTATION -> new WrapperPlayServerEntityRotation(event).getEntityId();
            case PacketType.Play.Server.ENTITY_STATUS -> new WrapperPlayServerEntityStatus(event).getEntityId();
            case PacketType.Play.Server.ATTACH_ENTITY -> new WrapperPlayServerAttachEntity(event).getHoldingId();
            case PacketType.Play.Server.ENTITY_METADATA -> new WrapperPlayServerEntityMetadata(event).getEntityId();
            case PacketType.Play.Server.ENTITY_EFFECT -> new WrapperPlayServerEntityEffect(event).getEntityId();
            case PacketType.Play.Server.REMOVE_ENTITY_EFFECT -> new WrapperPlayServerRemoveEntityEffect(event).getEntityId();
            case PacketType.Play.Server.BLOCK_BREAK_ANIMATION -> new WrapperPlayServerBlockBreakAnimation(event).getEntityId();
            case PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION -> new WrapperPlayServerEntityRelativeMoveAndRotation(event).getEntityId();
            default -> -1;
        };

        if (entityId == -1) return;

        Entity entity = SpigotConversionUtil.getEntityById(player.getWorld(), entityId);

        if (entity instanceof Player) {
            PlayerInfo sendPacketPlayerInfo = Main.getPlayersManager().getPlayerInfo((Player) entity);
            if (sendPacketPlayerInfo != null) {
                if (sendPacketPlayerInfo.getIsIngame() && sendPacketPlayerInfo.isGhost()) {
                    PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(event.getPlayer());
                    if (!pInfo.isGhost()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
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

        int entityId = -1;
        if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
            entityId = new WrapperPlayServerEntityEquipment(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_ANIMATION)) {
            entityId = new WrapperPlayServerEntityAnimation(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.COLLECT_ITEM)) {
            entityId = new WrapperPlayServerCollectItem(event).getCollectorEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.SPAWN_EXPERIENCE_ORB)) {
            entityId = new WrapperPlayServerSpawnExperienceOrb(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_VELOCITY)) {
            entityId = new WrapperPlayServerEntityVelocity(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_RELATIVE_MOVE)) {
            entityId = new WrapperPlayServerEntityRelativeMove(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_HEAD_LOOK)) {
            entityId = new WrapperPlayServerEntityHeadLook(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_TELEPORT)) {
            entityId = new WrapperPlayServerEntityTeleport(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_ROTATION)) {
            entityId = new WrapperPlayServerEntityRotation(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_STATUS)) {
            entityId = new WrapperPlayServerEntityStatus(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ATTACH_ENTITY)) {
            entityId = new WrapperPlayServerAttachEntity(event).getHoldingId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) {
            entityId = new WrapperPlayServerEntityMetadata(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_EFFECT)) {
            entityId = new WrapperPlayServerEntityEffect(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.REMOVE_ENTITY_EFFECT)) {
            entityId = new WrapperPlayServerRemoveEntityEffect(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)) {
            entityId = new WrapperPlayServerBlockBreakAnimation(event).getEntityId();
        } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION)) {
            entityId = new WrapperPlayServerEntityRelativeMoveAndRotation(event).getEntityId();
        }

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
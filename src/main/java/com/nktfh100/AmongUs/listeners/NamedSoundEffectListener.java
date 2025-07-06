package com.nktfh100.AmongUs.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.sound.Sound;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.managers.PlayersManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Predicate;

public class NamedSoundEffectListener implements PacketListener {
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.NAMED_SOUND_EFFECT) return;

        Player player = event.getPlayer();
        PlayersManager playersManager = Main.getPlayersManager();
        WrapperPlayServerSoundEffect packet = new WrapperPlayServerSoundEffect(event);

        Sound sound = packet.getSound();
        if (packet.getSoundCategory() == SoundCategory.PLAYER) {
            PlayerInfo pInfo = playersManager.getPlayerInfo(player);
            if (pInfo == null) {
                return;
            }
            if (pInfo.getIsIngame() && !pInfo.isGhost()) {
                Predicate<Entity> predicate = i -> (i instanceof Player && playersManager.getPlayerInfo((Player) i).isGhost());
                Location location = new Location(player.getWorld(), packet.getEffectPosition().x, packet.getEffectPosition().y, packet.getEffectPosition().z);
                Collection<Entity> players_ = player.getWorld().getNearbyEntities(location, 1, 1, 1, predicate);
                if (players_.size() > 0) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (sound == Sounds.ENTITY_PLAYER_ATTACK_NODAMAGE || sound == Sounds.ITEM_ARMOR_EQUIP_GENERIC) {
            PlayerInfo pInfo = playersManager.getPlayerInfo(player);
            if (pInfo != null && pInfo.getIsIngame()) {
                event.setCancelled(true);
            }
        }
    }
}
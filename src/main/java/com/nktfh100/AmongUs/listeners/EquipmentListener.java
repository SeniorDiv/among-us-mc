package com.nktfh100.AmongUs.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.main.Main;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EquipmentListener implements PacketListener {
    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(event.getPlayer());
        if (pInfo == null || !pInfo.getIsIngame()) return;

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);

        List<Equipment> equipmentList = new ArrayList<>();
        for (Equipment equipment : packet.getEquipment()) {
            if (equipment.getSlot() == EquipmentSlot.MAIN_HAND || equipment.getSlot() == EquipmentSlot.OFF_HAND) {
                equipmentList.add(new Equipment(equipment.getSlot(), SpigotConversionUtil.fromBukkitItemStack(new ItemStack(Material.AIR))));
            } else {
                equipmentList.add(equipment);
            }
        }

        packet.setEquipment(equipmentList);
    }
}
package com.nktfh100.AmongUs.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerLoginSuccess;
import com.nktfh100.AmongUs.main.Main;
import org.bukkit.entity.Player;

public class LoginListener implements PacketListener {
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Login.Server.LOGIN_SUCCESS) return;

        WrapperLoginServerLoginSuccess packet = new WrapperLoginServerLoginSuccess(event);
        Main.getRemoteChatSessionManager().addSession(((Player) event.getPlayer()).getUniqueId(), packet.readRemoteChatSession());
    }
}
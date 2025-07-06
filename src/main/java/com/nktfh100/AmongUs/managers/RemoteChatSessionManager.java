package com.nktfh100.AmongUs.managers;

import com.github.retrooper.packetevents.protocol.chat.RemoteChatSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Temporary class until PacketEvents adds a method to get the chat session manager.
 */
public class RemoteChatSessionManager {
    private final Map<UUID, RemoteChatSession> sessions = new HashMap<>();

    public void addSession(UUID player, RemoteChatSession session) {
        sessions.put(player, session);
    }

    public RemoteChatSession getSession(UUID player) {
        return sessions.get(player);
    }

    public void removeSession(UUID player) {
        sessions.remove(player);
    }
}
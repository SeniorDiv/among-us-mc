package com.nktfh100.AmongUs.info;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.nktfh100.AmongUs.main.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.nktfh100.AmongUs.utils.Packets;

public class FakeArmorStand {

	private final PlayerInfo pInfo;
	private Location loc;
	private final int entityId;
	private final UUID uuid;
	private Vector3f headRotation;
	private Vector3f bodyRotation;

	private final ArrayList<Player> shownTo = new ArrayList<>();

	public FakeArmorStand(PlayerInfo pInfo, Location loc, Vector3f headRotation, Vector3f bodyRotation) {
		this.pInfo = pInfo;
		this.loc = loc;
		this.entityId = (int) (Math.random() * Integer.MAX_VALUE);
		this.uuid = UUID.randomUUID();
		this.headRotation = headRotation;
		this.bodyRotation = bodyRotation;
	}

	public void updateLocation(Location newLoc) {
		this.loc = newLoc;
		for (Player player : this.shownTo) {
			Packets.sendPacket(player, Packets.ENTITY_TELEPORT(this.entityId, newLoc));
		}
	}

	public void updateRotation(Vector3f headRotation, Vector3f bodyRotation) {
		List<EntityData<?>> data = new ArrayList<>();
		data.add(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x20));

		if (headRotation != null) {
			this.headRotation = headRotation;
			if (Main.getVersion()[0] < 19 || (Main.getVersion()[0] == 19 && Main.getVersion()[1] < 3)) {
				data.add(new EntityData<>(15, EntityDataTypes.ROTATION, headRotation));
			} else {
				data.add(new EntityData<>(16, EntityDataTypes.ROTATION, headRotation));
			}
		}
		if (bodyRotation != null) {
			this.bodyRotation = bodyRotation;
			if (Main.getVersion()[0] < 19 || (Main.getVersion()[0] == 19 && Main.getVersion()[1] < 3)) {
				data.add(new EntityData<>(16, EntityDataTypes.ROTATION, bodyRotation));
			} else {
				data.add(new EntityData<>(17, EntityDataTypes.ROTATION, bodyRotation));
			}
		}

		for (Player player : this.shownTo) {
			Packets.sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, data));
		}
	}

	public void showTo(Player player, Boolean register) {
		Packets.sendPacket(player, Packets.ARMOR_STAND(this.loc, this.entityId, this.uuid));
		updateRotation(this.headRotation, this.bodyRotation);
		Packets.sendPacket(player, Packets.ENTITY_EQUIPMENT_HEAD(this.entityId, Material.LIME_STAINED_GLASS_PANE));

		if (register) {
			this.shownTo.add(player);
		}
	}

	public void hideFrom(Player player, Boolean register) {
		Packets.sendPacket(player, Packets.DESTROY_ENTITY(this.entityId));
		if (register) {
			this.shownTo.remove(player);
		}

	}

	public void resetAllShownTo() {
		for (Player p : this.shownTo) {
			this.hideFrom(p, false);
		}
		this.shownTo.clear();
	}

	public Location getLoc() {
		return this.loc;
	}

	public int getEntityId() {
		return entityId;
	}

	public UUID getUuid() {
		return uuid;
	}

	public PlayerInfo getPlayerInfo() {
		return pInfo;
	}

}

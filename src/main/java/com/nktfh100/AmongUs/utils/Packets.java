package com.nktfh100.AmongUs.utils;

import java.util.*;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.*;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.nktfh100.AmongUs.main.Main;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.AmongUs.info.ColorInfo;

public class Packets {
	private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
	/*public static byte toPackedByte(float f) {
		return (byte) ((int) (f * 256.0F / 360.0F));
	}

	public static int toPacketRotation(float f) {
		return (int) (f * 256.0F / 360.0F);
	}*/

	public static void sendPacket(Player p, PacketWrapper<?> packet) {
		if (p.isOnline() && packet != null) {
			PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet);
		}
	}

	public static PacketWrapper<?> UPDATE_DISPLAY_NAME(UUID uuid, String orgName, String newName) {
		UserProfile profile = new UserProfile(uuid, orgName);

        if (Main.getVersion()[0] < 19 || (Main.getVersion()[0] == 19 && Main.getVersion()[1] < 3)) {
			return new WrapperPlayServerPlayerInfo(
					WrapperPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME,
					new WrapperPlayServerPlayerInfo.PlayerData(legacy.deserialize(newName), profile, GameMode.ADVENTURE, 50)
			);

        } else {
			return new WrapperPlayServerPlayerInfoUpdate(
					WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
					new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(profile, true, 50, GameMode.ADVENTURE, legacy.deserialize(newName), Main.getRemoteChatSessionManager().getSession(uuid))
			);
        }
    }

	public static PacketWrapper<?> ADD_PLAYER(Player player, UUID playerToAdd, String name, String displayName, String textureValue, String textureSignature, boolean... isFakePlayer) {
		PacketWrapper<?> packet;
		UserProfile profile = new UserProfile(playerToAdd, name);
		profile.getTextureProperties().clear();
		profile.getTextureProperties().add(new TextureProperty("textures", textureValue, textureSignature));

		if (Main.getVersion()[0] < 19 || (Main.getVersion()[0] == 19 && Main.getVersion()[1] < 3)) {
			packet = new WrapperPlayServerPlayerInfo(
					WrapperPlayServerPlayerInfo.Action.ADD_PLAYER,
					new WrapperPlayServerPlayerInfo.PlayerData(legacy.deserialize(displayName), profile, GameMode.ADVENTURE, 50)
			);

		} else {
			packet = new WrapperPlayServerPlayerInfoUpdate(
					EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER, WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME, WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED),
					new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(profile, true, 50, GameMode.ADVENTURE, legacy.deserialize(displayName), Main.getRemoteChatSessionManager().getSession(playerToAdd))
			);
		}

        boolean fakePlayer = isFakePlayer.length >= 1 && isFakePlayer[0];
		if (Main.getIsTab() && !fakePlayer) {
			Main.getTabApi().getNameTagManager().showNameTag(Main.getTabApi().getPlayer(playerToAdd), Main.getTabApi().getPlayer(player.getUniqueId()));
		}

		return packet;
	}

	public static PacketWrapper<?> REMOVE_PLAYER(Player player, UUID playerToHide, boolean... isFakePlayer) {
		PacketWrapper<?> packet;

		if (Main.getVersion()[0] < 19 || (Main.getVersion()[0] == 19 && Main.getVersion()[1] < 3)) {
			packet = new WrapperPlayServerPlayerInfo(
					WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
					new WrapperPlayServerPlayerInfo.PlayerData(legacy.deserialize(player.getDisplayName()), new UserProfile(playerToHide, player.getName()), GameMode.ADVENTURE, 50)
			);

		} else {
			packet = new WrapperPlayServerPlayerInfoRemove(playerToHide);
		}

		boolean fakePlayer = isFakePlayer.length >= 1 && isFakePlayer[0];
		if (Main.getIsTab() && !fakePlayer) {
			Main.getTabApi().getNameTagManager().showNameTag(Main.getTabApi().getPlayer(playerToHide), Main.getTabApi().getPlayer(player.getUniqueId()));
		}

		return packet;
	}

	public static WrapperPlayServerBlockChange BLOCK_CHANGE(Location loc, BlockData blockData) {
		WrappedBlockState state = SpigotConversionUtil.fromBukkitBlockData(blockData);
		// TODO: Check if correct block ID
		return new WrapperPlayServerBlockChange(new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), state.getGlobalId());
	}

	public static WrapperPlayServerSpawnEntity SPAWN_PLAYER(Location loc, int entityId, UUID uuid) {
		return new WrapperPlayServerSpawnEntity(entityId, uuid, EntityTypes.PLAYER, SpigotConversionUtil.fromBukkitLocation(loc), 0f, 0, new Vector3d());
	}

	public static WrapperPlayServerEntityHeadLook ENTITY_HEAD_ROTATION(int entityId, Location loc) {
		return new WrapperPlayServerEntityHeadLook(entityId, loc.getYaw());
	}

	public static WrapperPlayServerEntityRotation ENTITY_LOOK(int entityId, Location loc) {
		return new WrapperPlayServerEntityRotation(entityId, loc.getYaw(), loc.getPitch(), true);
	}

	public static WrapperPlayServerEntityEquipment PLAYER_ARMOR(ColorInfo color, int entityId) {
		List<Equipment> equipment = new ArrayList<>();
		equipment.add(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(new ItemStack(color.getGlass()))));
		equipment.add(new Equipment(EquipmentSlot.CHEST_PLATE, SpigotConversionUtil.fromBukkitItemStack(Utils.getArmorColor(color, Material.LEATHER_CHESTPLATE))));
		equipment.add(new Equipment(EquipmentSlot.LEGGINGS, SpigotConversionUtil.fromBukkitItemStack(Utils.getArmorColor(color, Material.LEATHER_LEGGINGS))));
		equipment.add(new Equipment(EquipmentSlot.BOOTS, SpigotConversionUtil.fromBukkitItemStack(Utils.getArmorColor(color, Material.LEATHER_BOOTS))));
		return new WrapperPlayServerEntityEquipment(entityId, equipment);
	}

	public static WrapperPlayServerEntityEquipment ENTITY_EQUIPMENT_HEAD(int entityId, Material mat) {
		return new WrapperPlayServerEntityEquipment(entityId, List.of(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(new ItemStack(mat)))));
	}

	public static WrapperPlayServerDestroyEntities DESTROY_ENTITY(int entityId) {
		return new WrapperPlayServerDestroyEntities(entityId);
	}

	public static WrapperPlayServerEntityMetadata PLAYER_SLEEPING(int entityId) {
		List<EntityData<?>> data = new ArrayList<>();
		data.add(new EntityData<>(6, EntityDataTypes.ENTITY_POSE, EntityPose.SLEEPING));
		return new WrapperPlayServerEntityMetadata(entityId, data);
	}

	public static WrapperPlayServerSpawnEntity ARMOR_STAND(Location loc, Integer entityId, UUID uuid) {
		Location newLoc = loc.clone();
		newLoc.setYaw(0);
		newLoc.setPitch(0);

		return new WrapperPlayServerSpawnEntity(
				entityId,
				uuid,
				EntityTypes.ARMOR_STAND,
				SpigotConversionUtil.fromBukkitLocation(newLoc),
				0f,
				0,
				new Vector3d()
		);
	}

	public static WrapperPlayServerEntityTeleport ENTITY_TELEPORT(int entityId, Location loc) {
		return new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(loc), true);
	}

	public static WrapperPlayServerEntityMetadata METADATA_SKIN(int entityId, boolean isGhost) {
		List<EntityData<?>> data = new ArrayList<>();

		if (isGhost) {
			data.add(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x20));
		}

		if (Main.getVersion()[0] < 21 || (Main.getVersion()[0] == 21 && Main.getVersion()[1] < 9)) {
			data.add(new EntityData<>(17, EntityDataTypes.BYTE, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40)));
		} else {
			data.add(new EntityData<>(16, EntityDataTypes.BYTE, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40)));
		}



		return new WrapperPlayServerEntityMetadata(entityId, data);
	}

	public static WrapperPlayServerEntityAnimation HURT_ANIMATION(int entityId) {
		return new WrapperPlayServerEntityAnimation(entityId, WrapperPlayServerEntityAnimation.EntityAnimationType.HURT);
	}
}
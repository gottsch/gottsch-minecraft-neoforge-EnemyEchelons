/*
 * This file is part of  Enemy Echelons.
 * Copyright (c) 2022, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * Enemy Echelons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Enemy Echelons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Enemy Echelons.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.eechelons.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 
 * @author Mark Gottschling on Jul 28, 2022
 *
 */
public record LevelMessageToClient(int id, int level) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<LevelMessageToClient> TYPE = new CustomPacketPayload.Type<>(EEchelonsNetwork.LEVEL_MESSAGE_CLIENT_ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, LevelMessageToClient> CODEC =
			StreamCodec.composite(
					ByteBufCodecs.INT, LevelMessageToClient::id,
					ByteBufCodecs.INT, LevelMessageToClient::level,
					LevelMessageToClient::new);


	public static void handleDataOnMain(final LevelMessageToClient data, final IPayloadContext context) {//        MageFlame.LOGGER.debug("server received packet: uuid ->{}, id -> {}", uuid, id);

//		ClientLevel world = Minecraft.getInstance().level;
//		if (world != null) {
//			Entity entity = world.getEntity(msg.entityId);
////			EEchelons.LOGGER.debug("handling client message to entity -> {} for level -> {}", entity.getName().getString(), msg.level);
//			entity.getCapability(EEchelonsCapabilities.LEVEL_CAPABILITY).ifPresent(cap -> {
////				EEchelons.LOGGER.debug("setting the level on the client entity");
//				cap.setLevel(msg.level);
//			});
//		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public String toString() {
		return "LevelMessageToClient{" +
				"id=" + id +
				", level=" + level +
				'}';
	}
}

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

import mod.gottsch.neoforge.eechelons.data.ModDataAttachements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 
 * @author Mark Gottschling on Jul 30, 2022
 *
 */
public record LevelRequestToServer(int id, String registryName, String location) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<LevelRequestToServer> TYPE = new CustomPacketPayload.Type<>(EEchelonsNetwork.LEVEL_REQUEST_SERVER_ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, LevelRequestToServer> CODEC =
			StreamCodec.composite(
					ByteBufCodecs.INT, LevelRequestToServer::id,
					ByteBufCodecs.STRING_UTF8, LevelRequestToServer::registryName,
					ByteBufCodecs.STRING_UTF8, LevelRequestToServer::location,
					LevelRequestToServer::new);

	public static void handleDataOnMain(final LevelRequestToServer data, final IPayloadContext context) {//        MageFlame.LOGGER.debug("server received packet: uuid ->{}, id -> {}", uuid, id);

		// get the entity by id
		Entity entity = ((ServerPlayer)context.player()).serverLevel().getEntity(data.id());

		// get level value from dataComponent
		int level = entity.getData(ModDataAttachements.LEVEL);

		// send message back to client entity
		LevelMessageToClient payload = new LevelMessageToClient(entity.getId(), level);
		PacketDistributor.sendToPlayer((ServerPlayer) context.player(), payload);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public String toString() {
		return "LevelRequestToServer{" +
				"id=" + id +
				", registryName='" + registryName + '\'' +
				", location='" + location + '\'' +
				'}';
	}
}

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

import mod.gottsch.neoforge.eechelons.EEchelons;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * TODO if usng DataComponents, won't have to use custom packets at all.
 * @author Mark Gottschling on Jul 28, 2022
 *
 */
public class EEchelonsNetwork {
	public static final String PROTOCOL_VERSION = "1.0";

	public static final ResourceLocation LEVEL_REQUEST_SERVER_ID = ResourceLocation.fromNamespaceAndPath(EEchelons.MODID, "level_request_server");
	public static final ResourceLocation LEVEL_MESSAGE_CLIENT_ID = ResourceLocation.fromNamespaceAndPath(EEchelons.MODID, "level_message_client");

	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {

		final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
		registrar.playToServer(
				LevelRequestToServer.TYPE,
				LevelRequestToServer.CODEC,
				new DirectionalPayloadHandler<>(
						LevelRequestToServer::handleDataOnMain,
						LevelRequestToServer::handleDataOnMain
				)
		);

		registrar.playToClient(
				LevelMessageToClient.TYPE,
				LevelMessageToClient.CODEC,
				new DirectionalPayloadHandler<>(
						LevelMessageToClient::handleDataOnMain,
						LevelMessageToClient::handleDataOnMain
				)
		);
	  }
}

/*
 * This file is part of  Enemy Echelons.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.neoforge.eechelons.event;

import mod.gottsch.neo.gottschcore.world.WorldInfo;
import mod.gottsch.neoforge.eechelons.EEchelons;
import mod.gottsch.neoforge.eechelons.data.ModDataAttachements;
import mod.gottsch.neoforge.eechelons.echelon.EchelonManager;
import mod.gottsch.neoforge.eechelons.network.LevelRequestToServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 *
 * @author Mark Gottschling on Jul 31, 2022
 *
 */
@EventBusSubscriber(modid = EEchelons.MODID, bus = EventBusSubscriber.Bus.GAME)
public class WorldEventHandler {

	/**
	 *
	 */
	@SubscribeEvent
	public static void onJoin(EntityJoinLevelEvent event) {

		Entity entity = event.getEntity();

		if (EchelonManager.isValidEntity(entity)) {
			EEchelons.LOGGER.debug("entity joining world -> {} : {}", entity.getName().getString(), entity.getId());
			/*
			 * if on the client, request an update from the server
			 */
			if (WorldInfo.isClientSide(event.getEntity().level())) {
				if (!entity.hasData(ModDataAttachements.LEVEL) ||
						entity.getData(ModDataAttachements.LEVEL) == -1) {

					// send message to server
					LevelRequestToServer payload = new LevelRequestToServer(event.getEntity().getId(),
							event.getEntity().level().dimension().location().toString(),
							event.getEntity().level().dimension().location().toString());
					PacketDistributor.sendToServer(payload);
				}
			}
			else {
				Mob mob = (Mob)entity;
				EchelonManager.applyModications(mob);
			}
		}
	}
}
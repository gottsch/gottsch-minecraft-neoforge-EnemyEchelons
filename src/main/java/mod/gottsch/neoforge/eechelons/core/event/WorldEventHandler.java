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
package mod.gottsch.neoforge.eechelons.core.event;

import mod.gottsch.neoforge.eechelons.EEchelonsApiMod;
import mod.gottsch.neoforge.eechelons.api.EnemyEchelonsApi;
import mod.gottsch.neoforge.eechelons.core.config.EchelonConfigsHolder;
import mod.gottsch.neoforge.eechelons.core.echelon.EchelonManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

import java.util.Optional;

/**
 *
 * @author Mark Gottschling on Jul 31, 2022
 *
 */
@EventBusSubscriber(modid = EEchelonsApiMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class WorldEventHandler {

	/**
	 * Handles the LivingExperienceDropEvent to modify XP based on Echelon.
	 * @param event The event.
	 */
	@SubscribeEvent
	public static void onExperienceDrop(LivingExperienceDropEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Mob mob)) {
			return;
		}

		Optional<EchelonConfigsHolder.Config> echelonOpt = EchelonManager.REGISTRY.getEchelonConfig(mob);
		if (echelonOpt.isEmpty()) {
			return;
		}

		EchelonConfigsHolder.Config echelon = echelonOpt.get();

		if (echelon.hasXpFactor()) {
			// Get the echelon level from the capability
			if (EnemyEchelonsApi.hasDifficultyAttachment(mob)) {
				int difficulty = EnemyEchelonsApi.getDifficulty(mob);
				if (difficulty < 0) {
					// difficulty level not yet calculated, or calculation failed.
					// for now, we'll skip XP modification.
					// alternatively, could trigger EchelonManager.applyModifications(mob) here
					// or use a default level.
					return;
				}

				double xpFactor = 1.0 + (echelon.getXpFactor() * difficulty);
				double newXpReward = event.getOriginalExperience() * xpFactor;

				if (echelon.getMaxXp() != null) {
					newXpReward = Math.min(newXpReward, echelon.getMaxXp());
				}

				event.setDroppedExperience((int) newXpReward);
			}
		}
	}
}
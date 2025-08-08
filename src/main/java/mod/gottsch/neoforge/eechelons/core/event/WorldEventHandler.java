/*
 * This file is part of  Enemy Echelons API.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * Enemy Echelons API is free software: you can redistribute it and/or modify
 * it under the terms of the Open Software Licence 3.0.
 *
 * Enemy Echelons API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Open Software Licence 3.0 for more details.
 *
 * You should have received a copy of the Open Software Licence
 * along with Enemy Echelons.  If not, see <https://www.tldrlegal.com/license/open-software-licence-3-0>.
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

	// TODO this should be a method in EchelonManager that CAN be called. Shouldn't be active by default.
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
			// TODO for server only, remove Difficulty and determine from echelon
			// Get the echelon level from the capability
			if (EnemyEchelonsApi.hasDifficulty(mob)) {
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
/*
 * This file is part of  Enemy Echelons.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.neoforge.eechelons.core.echelon;

import mod.gottsch.neoforge.eechelons.core.registry.EchelonRegistry;
import mod.gottsch.neoforge.eechelons.EEchelonsApiMod;
import mod.gottsch.neoforge.eechelons.core.config.EchelonConfigsHolder;
import mod.gottsch.neoforge.eechelons.core.data.ModDataAttachements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Optional;

/**
 *
 * @author Mark Gottschling on Jul 26, 2022
 *
 */
public class EchelonManager {
	// f_21364_ => xpReward
//	private static final String XP_REWARD_FIELDNAME = "xpReward"; //"f_21364_";
//	private static final ResourceLocation ALL_DIMENSION = ResourceLocation.fromNamespaceAndPath(".", ".");

	private static final int DIFFICULTY_NOT_SET = -1;

	private static final ResourceLocation ECHELON_MAX_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(EEchelonsApiMod.MODID, "max_health_modifier");
	private static final ResourceLocation ECHELON_ATTACK_DAMAGE_MODIFIER = ResourceLocation.fromNamespaceAndPath(EEchelonsApiMod.MODID, "attack_damage_modifier");
	private static final ResourceLocation ECHELON_ARMOR_MODIFIER = ResourceLocation.fromNamespaceAndPath(EEchelonsApiMod.MODID, "armor_modifier");
	private static final ResourceLocation ECHELON_ARMOR_TOUGHNESS_MODIFIER = ResourceLocation.fromNamespaceAndPath(EEchelonsApiMod.MODID, "armor_toughness_modifier");
	private static final ResourceLocation ECHELON_MOVEMENT_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(EEchelonsApiMod.MODID, "movement_speed_modifier");
	private static final ResourceLocation ECHELON_FLYING_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(EEchelonsApiMod.MODID, "flying_speed_modifier");
	private static final ResourceLocation ECHELON_ATTACK_KNOCKBACK_MODIFIER = ResourceLocation.fromNamespaceAndPath(EEchelonsApiMod.MODID, "attack_knockback_modifier");
	private static final ResourceLocation ECHELON_KNOCKBACK_RESISTANCE_MODIFIER = ResourceLocation.fromNamespaceAndPath(EEchelonsApiMod.MODID, "knockback_resistence_modifier");

	public static final EchelonRegistry REGISTRY = new EchelonRegistry();
	/*
	 * map of echelons by id
	 * currently not implemented in any meaningful way.
	 */
//	private static final Map<String, EchelonsHolder.Echelon> ECHELONS_BY_ID = Maps.newHashMap();
//
//	/*
//	 * map of echelons by dimension
//	 */
//	private static final Multimap<ResourceLocation, Echelon> ECHELONS = ArrayListMultimap.create();
//
//	/*
//	 * map of echelons by dimension-mod (namespace) pair
//	 */
//	private static final Map<Pair<ResourceLocation, String>, Echelon> ECHELONS_BY_MOD = Maps.newHashMap();
//
//	/*
//	 * map of echelons by dimension-mob pair.
//	 * this is for white-list mobs.
//	 */
//	private static final Map<Pair<ResourceLocation, ResourceLocation>, Echelon> ECHELONS_BY_MOB = Maps.newHashMap();

	/**
	 * @param entity
	 * @return
	 */
	public static boolean isValidEntity(final Entity entity) {
		return entity instanceof Mob;
	}

	/*
	 * default behaviour. uses the internal mod registry
	 */
	public static void applyModifications(Mob mob) {
		applyModifications(REGISTRY, mob, DIFFICULTY_NOT_SET);
	}

	/*
	 * for custom registries
	 */
	public static void applyModifications(EchelonRegistry registry, Mob mob) {
		applyModifications(registry, mob, DIFFICULTY_NOT_SET);
	}

	/*
	 * this method is intended for other mods to make use of, if they have a non-echelon mod registry or would
	 * like to directly select an echelon to apply to a programmatically spawned mob.
	 */
	public static void applyModifications(EchelonRegistry registry, Mob mob, ResourceLocation echelonId, Integer selectedDifficulty) {
		if (!mob.hasData(ModDataAttachements.DIFFICULTY)) {
			return;
		}

		Integer currentDifficulty = mob.getData(ModDataAttachements.DIFFICULTY);

		// check if mob capability values have already been set
		if (currentDifficulty > DIFFICULTY_NOT_SET) {
			return;
		}

		// get the config by mob
		Optional<EchelonConfigsHolder.Config> echelonConfig = registry.getEchelonConfig(mob);

		if (echelonConfig.isEmpty()) {
			mob.setData(ModDataAttachements.DIFFICULTY, 0);
			return;
		}

		// select the difficulty from the config if not provided (default behavior)
		if (selectedDifficulty == DIFFICULTY_NOT_SET) {
			for (EchelonConfigsHolder.Echelon echelon : echelonConfig.get().getEchelons()) {
				if (echelonId.toString().equals(echelon.getId())) {
					// get the next weighted random integer
					selectedDifficulty = echelon.getWeightedDifficulties().next();
					break;
				}
			}
		}

		// if setting the difficulty was unsuccessful
		if (selectedDifficulty == DIFFICULTY_NOT_SET) {
			return;
		}

		applyModifications(echelonConfig.get(), mob, selectedDifficulty);
	}

	/**
	 *
	 * @param registry
	 * @param mob
	 * @param selectedDifficulty
	 */
	public static void applyModifications(EchelonRegistry registry, Mob mob, Integer selectedDifficulty) {

		if (!mob.hasData(ModDataAttachements.DIFFICULTY)) {
			return;
		}

		Integer currentDifficulty = mob.getData(ModDataAttachements.DIFFICULTY);

		// check if mob capability values have already been set
		if (currentDifficulty > DIFFICULTY_NOT_SET) {
			return;
		}


		// determine the altitude (y-value)
		int y = mob.getBlockY();

		/*
		 *  apply the attribute modifications
		 */
		Optional<EchelonConfigsHolder.Config> echelonConfig = registry.getEchelonConfig(mob);

		if (echelonConfig.isEmpty()) {
			mob.setData(ModDataAttachements.DIFFICULTY, 0);
			return;
		}

		// select the difficulty from the config if not provided (default behavior)
		if (selectedDifficulty == DIFFICULTY_NOT_SET) {
			selectedDifficulty = echelonConfig.get().getDifficulty(y);
//				EEchelons.LOGGER.debug("selected difficulty -> {} for dimension -> {} @ y -> {}", echelonLevel, dimension, y);
		}

		applyModifications(echelonConfig.get(), mob, selectedDifficulty);
	}

	/**
	 *
	 * @param mob
	 */
	public static void applyModifications(EchelonConfigsHolder.Config config, Mob mob, Integer selectedDifficulty) {
		Integer echelonDifficulty = -1;

		if (mob.hasData(ModDataAttachements.DIFFICULTY)) {

			// health
			modifyHealth(mob, selectedDifficulty, config);

			// damage
			modifyDamage(mob, selectedDifficulty, config);

			// armor
			modifyArmor(mob, selectedDifficulty, config);

			// armor
			modifyArmorToughness(mob, selectedDifficulty, config);

			// knockback
			modifyKnockback(mob, selectedDifficulty, config);

			// knockback resist
			modifyKnockbackResist(mob, selectedDifficulty, config);

			// speed
			modifySpeed(mob, selectedDifficulty, config);

			// experience
			// NOTE this is handled by the LivingExperienceDropEvent
//			modifyXp(mob, echelonLevel, echelon.get());

			// update the data
			mob.setData(ModDataAttachements.DIFFICULTY, selectedDifficulty);
		}
	}


	private static void modifySpeed(Mob mob, Integer difficulty, EchelonConfigsHolder.Config echelon) {
		if (echelon.hasSpeedFactor()) {
			AttributeInstance attribute = mob.getAttribute(Attributes.MOVEMENT_SPEED);
			if (attribute != null) {
				attribute.removeModifier(ECHELON_MOVEMENT_SPEED_MODIFIER);

				double speedMultiplier = echelon.getSpeedFactor() * difficulty;

				if (Math.abs(speedMultiplier) > 1.0E-7) {
					AttributeModifier speedModifier = new AttributeModifier(
							ECHELON_MOVEMENT_SPEED_MODIFIER,
							speedMultiplier,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					);
					attribute.addPermanentModifier(speedModifier);
				}
				// EEchelons.LOGGER.debug("mob new speed -> {}", mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
			}
		}
	}

	private static void modifyFlyingSpeed(Mob mob, Integer difficulty, EchelonConfigsHolder.Config echelon) {
		if (echelon.hasSpeedFactor()) {
			AttributeInstance attribute = mob.getAttribute(Attributes.FLYING_SPEED);
			if (attribute != null) {
				attribute.removeModifier(ECHELON_FLYING_SPEED_MODIFIER);

				double speedMultiplier = echelon.getSpeedFactor() * difficulty;

				if (Math.abs(speedMultiplier) > 1.0E-7) {
					AttributeModifier speedModifier = new AttributeModifier(
							ECHELON_MOVEMENT_SPEED_MODIFIER,
							speedMultiplier,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					);
					attribute.addPermanentModifier(speedModifier);
				}
				// EEchelons.LOGGER.debug("mob new speed -> {}", mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
			}
		}
	}

//    private static void modifyXp(Mob mob, Integer difficulty, Config echelon) {
//        if (echelon.hasXpFactor()) {
//            double xp = 1.0 + (echelon.getXpFactor() * difficulty);
//            try {
//                int xpReward = (int) ObfuscationReflectionHelper.getPrivateValue(Mob.class, mob, XP_REWARD_FIELDNAME);
//                double newXpReward = xpReward * xp;
//                if (echelon.getMaxXp() != null) {
//                    newXpReward = Math.min(newXpReward, echelon.getMaxXp());
//                }
//                ObfuscationReflectionHelper.setPrivateValue(Mob.class, mob, (int) newXpReward, XP_REWARD_FIELDNAME);
//            } catch (UnableToAccessFieldException e) {
//                return;
//            }
//        }
//    }

	private static void modifyHealth(Mob mob, int difficulty, EchelonConfigsHolder.Config config) {
		if (config.hasHpFactor()) {
			AttributeInstance attribute = mob.getAttribute(Attributes.MAX_HEALTH);
			if (attribute != null) {
				// remove any existing modifier from this mod
				attribute.removeModifier(ECHELON_MAX_HEALTH_MODIFIER);

				double healthMultiplier = (config.getHpFactor() * difficulty);

				// Apply new modifier only if it has a significant effect
				if (Math.abs(healthMultiplier) > 1.0E-7) { // Check if modifierAmount is not effectively zero
					AttributeModifier healthModifier = new AttributeModifier(
							ECHELON_MAX_HEALTH_MODIFIER,
							healthMultiplier,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					);
					attribute.addPermanentModifier(healthModifier);
//				EEchelons.LOGGER.debug("mob new health -> {}", mob.getMaxHealth());
				}
				// heal the mob to its new maximum health
				mob.setHealth(mob.getMaxHealth());
			}
		}
	}

	private static void modifyDamage(Mob mob, int difficulty, EchelonConfigsHolder.Config echelon) {
		if (echelon.hasDamageFactor()) {
			AttributeInstance attribute = mob.getAttribute(Attributes.ATTACK_DAMAGE);
			if (attribute != null) {
				// Remove any existing modifier from this mod
				attribute.removeModifier(ECHELON_ATTACK_DAMAGE_MODIFIER);

				double damageMultiplier = (echelon.getDamageFactor() * difficulty);

				// Apply new modifier only if it has a significant effect
				if (Math.abs(damageMultiplier) > 1.0E-7) { // Check if modifierAmount is not effectively zero
					AttributeModifier damageModifier = new AttributeModifier(
							ECHELON_ATTACK_DAMAGE_MODIFIER,
							damageMultiplier,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					);
					attribute.addPermanentModifier(damageModifier);
				}
//				EEchelons.LOGGER.debug("mob new damage -> {}", mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
			}
		}
	}

	private static void modifyArmor(Mob mob, Integer difficulty, EchelonConfigsHolder.Config echelon) {
		if (echelon.hasArmorFactor()) {
			AttributeInstance attribute = mob.getAttribute(Attributes.ARMOR);
			if (attribute != null) {
				attribute.removeModifier(ECHELON_ARMOR_MODIFIER);

				double armorMultiplier = (echelon.getArmorFactor() * difficulty);

				if (Math.abs(armorMultiplier) > 1.0E-7) {
					AttributeModifier armorModifier = new AttributeModifier(
							ECHELON_ARMOR_MODIFIER,
							armorMultiplier,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					);
					attribute.addPermanentModifier(armorModifier);
				}
				//		EEchelons.LOGGER.debug("mob new armor -> {}", mob.getAttributeValue(Attributes.ARMOR));
			}
		}
	}

	private static void modifyArmorToughness(Mob mob, Integer difficulty, EchelonConfigsHolder.Config echelon) {
		if (echelon.hasArmorToughnessFactor()) {
			AttributeInstance attribute = mob.getAttribute(Attributes.ARMOR_TOUGHNESS);
			if (attribute != null) {
				attribute.removeModifier(ECHELON_ARMOR_TOUGHNESS_MODIFIER);

				double armorToughnessMultiplier = echelon.getArmorToughnessFactor() * difficulty;

				if (Math.abs(armorToughnessMultiplier) > 1.0E-7) {
					AttributeModifier armorToughnessModifier = new AttributeModifier(
							ECHELON_ARMOR_TOUGHNESS_MODIFIER,
							armorToughnessMultiplier,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					);
					attribute.addPermanentModifier(armorToughnessModifier);
				}
				//		EEchelons.LOGGER.debug("mob new armor toughness -> {}", mob.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
			}
		}
	}

	private static void modifyKnockback(Mob mob, int difficulty, EchelonConfigsHolder.Config echelon) {
		if (echelon.hasKnockbackIncrement()) {
			AttributeInstance attribute = mob.getAttribute(Attributes.ATTACK_KNOCKBACK);
			if (attribute != null) {
				attribute.removeModifier(ECHELON_ATTACK_KNOCKBACK_MODIFIER);

				double modifierAmount = echelon.getKnockbackIncrement() * difficulty;

				if (Math.abs(modifierAmount) > 1.0E-7) {
					AttributeModifier knockbackModifier = new AttributeModifier(
							ECHELON_ATTACK_KNOCKBACK_MODIFIER,
							modifierAmount,
							AttributeModifier.Operation.ADD_VALUE
					);
					attribute.addPermanentModifier(knockbackModifier);
				}
				//			EEchelons.LOGGER.debug("mob new knockback -> {}", mob.getAttributeValue(Attributes.ATTACK_KNOCKBACK));
			}
		}
	}

	private static void modifyKnockbackResist(Mob mob, int difficulty, EchelonConfigsHolder.Config echelon) {
		if (echelon.hasKnockbackResistIncrement()) {
			AttributeInstance attribute = mob.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
			if (attribute != null) {
				attribute.removeModifier(ECHELON_KNOCKBACK_RESISTANCE_MODIFIER);

				double modifierAmount = echelon.getKnockbackResistIncrement() * difficulty;

				if (Math.abs(modifierAmount) > 1.0E-7) {
					AttributeModifier knockbackResistModifier = new AttributeModifier(
							ECHELON_KNOCKBACK_RESISTANCE_MODIFIER,
							modifierAmount,
							AttributeModifier.Operation.ADD_VALUE
					);
					attribute.addPermanentModifier(knockbackResistModifier);
				}
				//			EEchelons.LOGGER.debug("mob new knockback resist -> {}", mob.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			}
		}
	}
}

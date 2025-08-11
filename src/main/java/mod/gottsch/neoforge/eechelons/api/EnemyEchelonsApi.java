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
package mod.gottsch.neoforge.eechelons.api;

import mod.gottsch.neoforge.eechelons.core.config.EchelonConfigsHolder;
import mod.gottsch.neoforge.eechelons.core.echelon.EchelonManager;
import mod.gottsch.neoforge.eechelons.core.registry.EchelonRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.Optional;

/**
 * @author by Mark Gottschling on 6/3/2025
 */
public class EnemyEchelonsApi {
    public static void register(List<EchelonConfigsHolder.Config> configs) {
        EchelonManager.REGISTRY.register(configs);
    }

    public static void register(EchelonConfigsHolder.Config config) {
        EchelonManager.REGISTRY.register(config);
    }

    public static Optional<EchelonConfigsHolder.Config> getEchelonConfig(Mob mob) {
        return EchelonManager.REGISTRY.getEchelonConfig(mob);
    }

    // generate a new custom registry
    public static EchelonRegistry customRegistry() {
        return new EchelonRegistry();
    }

    // TODO make call to get mob by desired level
    public static void apply(Mob mob) {
        EchelonManager.applyModifications(mob);
    }

    public static void apply(EchelonRegistry registry, Mob mob) {
        EchelonManager.applyModifications(registry, mob);
    }

    public static void apply(EchelonRegistry registry, Mob mob, int difficulty) {
        EchelonManager.applyModifications(registry, mob, difficulty);
    }

    public static boolean isValidEntity(Entity entity) {
        return EchelonManager.isValidEntity(entity);
    }

    public static boolean hasDifficulty(Entity entity) {
        return EchelonManager.hasDifficulty(entity);
    }

    public static Integer getDifficulty(Entity entity) {
        return EchelonManager.getDifficulty(entity);
    }

    public static void setDifficulty(Entity entity, int difficulty) {
        EchelonManager.setDifficulty(entity, difficulty);
    }

    public static boolean hasDifficultyName(Entity entity) {
        return EchelonManager.hasDifficultyName(entity);
    }

    public static Optional<String> getDifficultyName(Entity entity) {
        return EchelonManager.getDifficultyName(entity);
    }

    public static void setDifficultyName(Entity entity, String difficultyName) {
        EchelonManager.setDifficultyName(entity, difficultyName);
    }
}
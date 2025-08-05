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
package mod.gottsch.neoforge.eechelons;

import com.mojang.logging.LogUtils;
import mod.gottsch.neoforge.eechelons.core.config.Config;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * A Server-Side API.
 * @author Mark Gottschling on Jul 24, 2022
 *
 */
@Mod(EEchelonsApiMod.MODID)
public class EEchelonsApiMod {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "eechelonsapi";

	/**
	 * 
	 */
	public EEchelonsApiMod(IEventBus eventBus, ModContainer modContainer) {
		// register the server config
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
	}
}

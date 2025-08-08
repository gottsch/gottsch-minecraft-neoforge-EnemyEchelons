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
	public static final String MODID = "eechelonsapi_server";

	/**
	 * 
	 */
	public EEchelonsApiMod(IEventBus eventBus, ModContainer modContainer) {
		// register the server config
		modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
	}
}

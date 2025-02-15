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

import com.electronwill.nightconfig.core.CommentedConfig;
import mod.gottsch.neoforge.eechelons.config.Config;
import mod.gottsch.neoforge.eechelons.echelon.EchelonManager;
import mod.gottsch.neoforge.eechelons.setup.ClientSetup;
import mod.gottsch.neoforge.eechelons.setup.CommonSetup;
import mod.gottsch.neoforge.eechelons.setup.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Many thanks go out to TheIllusiveC4 as the EchelonConfig 
 * loading code was derived from Champions.
 * @see <a href="https://github.com/TheIllusiveC4/Champions">Champions</a>
 * 
 * @author Mark Gottschling on Jul 24, 2022
 *
 */
@Mod(EEchelons.MODID)
public class EEchelons {
	public static final Logger LOGGER = LogManager.getLogger(EEchelons.MODID);

	public static final String MODID = "eechelons";

	private static final String ECHELONS_CONFIG_VERSION = "1.20.1-v3";
	/**
	 * 
	 */
	public EEchelons(IEventBus eventBus, ModContainer modContainer) {
		// register the deferred registries
		Registration.init();
		// register the server config
		modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
		modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
		// create the default config
		createServerConfig(modContainer, Config.ECHELONS_SPEC, "echelons", ECHELONS_CONFIG_VERSION);

		// register 'ModSetup::init' to be called at mod setup time (server and client)
		eventBus.addListener(CommonSetup::init);
		eventBus.addListener(this::config);
		
		// register 'ClientSetup::init' to be called at mod setup time (client only)
		eventBus.addListener(ClientSetup::init);
	}
	
	/**
	 * 
	 * @param spec
	 * @param suffix
	 */
	private static void createServerConfig(ModContainer modContainer,ModConfigSpec spec, String suffix, String version) {
		// TODO ensure to include a version # to the filename so it can be overridden in future
		String fileName = "eechelons-" + suffix + "-" + version + ".toml";
		modContainer.registerConfig(ModConfig.Type.SERVER, spec, fileName);
		File defaults = new File(FMLPaths.GAMEDIR.get() + "/defaultconfigs/" + fileName);

		if (!defaults.exists()) {
			try {
				FileUtils.copyInputStreamToFile(
						Objects.requireNonNull(EEchelons.class.getClassLoader().getResourceAsStream(fileName)),
						defaults);
			} catch (IOException e) {
				LOGGER.error("Error creating default config for " + fileName);
			}
		}
	}

	/**
	 * On a config event.
	 * @param event
	 */
	private void config(final ModConfigEvent event) {
		if (event.getConfig().getModId().equals(MODID)) {
			if (event.getConfig().getType() == ModConfig.Type.SERVER) {
				IConfigSpec spec = event.getConfig().getSpec();
				// get the toml config data
				CommentedConfig commentedConfig = event.getConfig().getLoadedConfig().config();

				if (spec == Config.ECHELONS_SPEC) {
					// transform/copy the toml into the config
					Config.transformEchelons(commentedConfig);
					EchelonManager.build();					
				} 
			}
		}
	}
}

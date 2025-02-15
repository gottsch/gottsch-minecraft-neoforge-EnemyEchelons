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
package mod.gottsch.neoforge.eechelons.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import mod.gottsch.neo.gottschcore.config.AbstractConfig;
import mod.gottsch.neoforge.eechelons.EEchelons;
import mod.gottsch.neoforge.eechelons.config.EchelonsHolder.Echelon;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Mark Gottschling on Jul 25, 2022
 *
 */
@EventBusSubscriber(modid = EEchelons.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Config extends AbstractConfig {
	public static final String CATEGORY_DIV = "##############################";
	public static final String UNDERLINE_DIV = "------------------------------";

	public static final ModConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;

	public static final ModConfigSpec SERVER_SPEC;
	public static final ServerConfig SERVER;

	public static final ModConfigSpec COMMON_SPEC;
	public static final CommonConfig COMMON;

	static {
		final Pair<CommonConfig, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder()
				.configure(CommonConfig::new);
		COMMON_SPEC = commonSpecPair.getRight();
		COMMON = commonSpecPair.getLeft();

		final Pair<ClientConfig, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder()
				.configure(ClientConfig::new);
		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();

		final Pair<ServerConfig, ModConfigSpec> serverSpecPair = new ModConfigSpec.Builder()
				.configure(ServerConfig::new);
		SERVER_SPEC = serverSpecPair.getRight();
		SERVER = serverSpecPair.getLeft();
	}

	/**
	 *
	 * @param event
	 */
	@SubscribeEvent
	static void onLoad(final ModConfigEvent event)
	{
		// TODO copy values from private builder spec definitions to public variables
		CLIENT.load(event);
		SERVER.load(event);
	}


	public static class CommonConfig {
		public final Logging logging;
		public CommonConfig(ModConfigSpec.Builder builder) {
			logging = new Logging(builder);
		}
	}

	/*
	 * TODO client should probably copy format of Server with regards to BUILDER
	 */
	public static class ClientConfig {
		private final ModConfigSpec.IntValue HUD_XOFFSET;
		private final ModConfigSpec.IntValue HUD_YOFFSET;
		private final ModConfigSpec.BooleanValue USE_DARK_HUD;

		public int hudXOffset;
		public int hudYOffset;
		public boolean useDarkHud;

//		// TODO this might be moot for 1.21+
//		private static final ModConfigSpec.BooleanValue ENABLE_WAILA_INTEGRATION = BUILDER
//				.comment(" Moves the Enemy Echelons HUD beside (to the left) of the WAILA HUD.",
//						" This setting is ignored if hudXOffset or hudYOffset are set (not 0).")
//				.define("enableWailaIntegration", true);
//
//		// TODO this IS moot for 1.21+
//		private static final ModConfigSpec.BooleanValue ENABLE_CHAMPIONS_INTEGRATION = BUILDER
//				.comment(" Moves the Enemy Echelons HUD beside (to the left) of the Champions HUD.",
//						" This setting will supercede enableWailaIntegration.",
//						" This setting is ignored if hudXOffset or hudYOffset are set (not 0).")
//				.define("enableChampionsIntegration", true);
//
//		public final boolean enableWailaIntegration;
//		public final boolean enableChampionsIntegration;

		public ClientConfig(ModConfigSpec.Builder builder) {
			HUD_XOFFSET = builder
					.comment("")
					.defineInRange("hudXOffset", 0, -1000, 1000);

			HUD_YOFFSET = builder
					.comment("")
					.defineInRange("hudYOffset", 0, -1000, 1000);

			USE_DARK_HUD = builder
					.comment(" Use dark theme HUD.")
					.define("useDarkHud", true);
		}

		public void load(final ModConfigEvent event) {
			this.hudXOffset = HUD_XOFFSET.get();
			this.hudYOffset = HUD_YOFFSET.get();
			this.useDarkHud = USE_DARK_HUD.get();
		}
	}


	/**
	 * For server mod config options
	 *
	 */
	public static class ServerConfig {

		private final ModConfigSpec.BooleanValue SHOW_HUD;
		private final ModConfigSpec.BooleanValue HUD_RANGE_ENABLED;
		private final ModConfigSpec.IntValue HUD_RANGE;

		public boolean showHud;
		public boolean hudRangeEnabled;
		public int hudRange;

		public ServerConfig(ModConfigSpec.Builder builder) {
			builder.push("general");
			builder.pop();

			builder.push("hud");

			// showHud remains in server config so server admin can determine if users are able to see the level or not.
			SHOW_HUD = builder
					.comment(" Enable HUD display.")
					.define("showHud", true);

			HUD_RANGE_ENABLED = builder
					.comment(" Enable custom HUD range.",
							" NOTE enabling is more computationally expensive on the client side.")
					.define("hudRangeEnabled", false);

			HUD_RANGE = builder
					.comment(" The distance that the HUD can be seen from (in blocks).",
							" Vanilla default = 3.")
					.defineInRange("hudRange", 3, 0, 100);

			builder.pop();
		}

		public void load(final ModConfigEvent event) {
			showHud = SHOW_HUD.get();
			hudRangeEnabled = HUD_RANGE_ENABLED.get();
			hudRange = HUD_RANGE.get();
		}
	}

	/**
	 * Echelons Config
	 */
	public static final ModConfigSpec ECHELONS_SPEC;
	public static final EchelonsConfig ECHELONS_CONFIG;
	/*
	 * list of echelon configurations
	 */
	public static List<Echelon> echelons;

	static {
		final Pair<EchelonsConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder()
				.configure(EchelonsConfig::new);
		ECHELONS_SPEC = specPair.getRight();
		ECHELONS_CONFIG = specPair.getLeft();
	}

	public static class EchelonsConfig {
		public EchelonsHolder echelonsHolder;
		public EchelonsConfig(ModConfigSpec.Builder builder) {
			builder.comment(" list of echelons").define("echelons", new ArrayList<>());
			builder.build();
		}
	}

	/**
	 * 
	 * @param configData
	 */
	public static void transformEchelons(CommentedConfig configData) {
		// convert the data to an object and set the holder in the _CONFIG
		ECHELONS_CONFIG.echelonsHolder = new ObjectConverter().toObject(configData, EchelonsHolder::new);
		// get the list from the holder and set the config property
		echelons = ECHELONS_CONFIG.echelonsHolder.echelons;
	}
	
	@Override
	public String getLogsFolder() {
		return COMMON.logging.folder.get();
	}
	
	public void setLogsFolder(String folder) {
		COMMON.logging.folder.set(folder);
	}
	
	@Override
	public String getLoggingLevel() {
		return COMMON.logging.level.get();
	}
}

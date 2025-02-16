/*
 * This file is part of  Enemy Echelons.
 * Copyright (c) 2022, Mark Gottschling (gottsch)
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

import mod.gottsch.neoforge.eechelons.EEchelons;
import mod.gottsch.neoforge.eechelons.client.HudUtil;
import mod.gottsch.neoforge.eechelons.client.MouseUtil;
import mod.gottsch.neoforge.eechelons.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.util.Optional;

/**
 * This class was derived from Champions by TheIllusiveC4
 * @see <a href="https://github.com/TheIllusiveC4/Champions">Champions</a>
 *
 */
public class HudEventHandler {

//	public static boolean isRendering = false;

	// NOTE these are only used to check where the offset is set to, so other integrations can move if they overlap
	// these are NOT used in the actual echelons rendering of background and level text 
	public static int startX = 0;
	public static int startY = 0;

	/**
	 * Forge Bus Event Subscriber class
	 */
	@EventBusSubscriber(modid = EEchelons.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
	public static class ForgeBusSubscriber {

		@SubscribeEvent
		public static void renderHealthHud(final RenderGuiLayerEvent.Pre evt) {
			if (Config.SERVER.showHud) {
				Minecraft mc = Minecraft.getInstance();

				Optional<LivingEntity> livingEntity;
				if (Config.SERVER.hudRangeEnabled) {
					livingEntity = MouseUtil.getMouseOverEchelonMob(mc, evt.getPartialTick().getRealtimeDeltaTicks());
				} else {
					HitResult hitResult = mc.hitResult;
					if (hitResult.getType() == HitResult.Type.ENTITY
							&& ((EntityHitResult)hitResult).getEntity() instanceof LivingEntity) {
						livingEntity = Optional.of((LivingEntity)((EntityHitResult)hitResult).getEntity());
					} else {
						livingEntity = Optional.empty();
					}
				}

				livingEntity.ifPresent(entity -> {
					GuiGraphics matrixStack = evt.getGuiGraphics();
					HudUtil.renderLevelBar(matrixStack, entity);
				});
			}
		}
	}
}

/*
 * This file is part of Enemy Echelons API.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * Enemy Echelons API is free software: you can redistribute it and/or modify
 * it under the terms of the Open Software Licence 3.0.
 *
 * Enemy Echelons API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Open Software Licence 3.0 for more details.
 *
 * You should have received a copy of the Open Software Licence
 * along with Enemy Echelons. If not, see <https://www.tldrlegal.com/license/open-software-licence-3-0>.
 */
package mod.gottsch.neoforge.eechelons.core.data;

import com.mojang.serialization.Codec;
import mod.gottsch.neoforge.eechelons.EEchelonsApiMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * @author Mark Gottschling on 2/15/2025
 */
public class ModDataAttachements {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EEchelonsApiMod.MODID);

    public static final Supplier<AttachmentType<Integer>> DIFFICULTY = ATTACHMENT_TYPES.register(
            "difficulty", () -> AttachmentType.builder(() -> -1).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<String>> DIFFICULTY_NAME = ATTACHMENT_TYPES.register(
            "difficulty_name", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}

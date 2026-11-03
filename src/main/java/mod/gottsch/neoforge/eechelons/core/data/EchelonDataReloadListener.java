/*
 * This file is part of  Enemy Echelons API.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
 * along with Enemy Echelons. If not, see <https://www.tldrlegal.com/license/open-software-licence-3-0>.
 */
package mod.gottsch.neoforge.eechelons.core.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Reads every json file in a data folder on server start and on /reload, and hands them to a handler.
 * @author Mark Gottschling on 11/03/2026
 */
public class EchelonDataReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    private final Consumer<Map<ResourceLocation, JsonElement>> handler;

    // PORT-CHECK: 1.21.1 SimpleJsonResourceReloadListener still takes (Gson, String directory) and apply(Map<ResourceLocation,
    // JsonElement>, ...); 1.21.2 changed both. Confirm the directory scan still includes the echelons/exclusions sub-folder.
    public EchelonDataReloadListener(String directory, Consumer<Map<ResourceLocation, JsonElement>> handler) {
        super(GSON, directory);
        this.handler = handler;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        handler.accept(files);
    }
}

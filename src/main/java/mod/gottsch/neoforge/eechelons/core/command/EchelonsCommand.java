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
package mod.gottsch.neoforge.eechelons.core.command;

import com.mojang.brigadier.CommandDispatcher;
import mod.gottsch.neoforge.eechelons.EEchelonsApiMod;
import mod.gottsch.neoforge.eechelons.core.echelon.EchelonManager;
import mod.gottsch.neoforge.eechelons.core.registry.DifficultyNameRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * /eechelons dump - lists the global echelon registry (in order of precedence, with the source of each config),
 * the configs left out, the exclusions and the difficulty names. Also written to the log.
 * @author Mark Gottschling on 11/03/2026
 */
public class EchelonsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("eechelons")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("dump")
                        .executes(context -> dump(context.getSource()))));
    }

    private static int dump(CommandSourceStack source) {
        List<String> lines = new ArrayList<>(RegistryDump.echelons(EchelonManager.REGISTRY));
        lines.addAll(RegistryDump.names(DifficultyNameRegistry.getEntries(), DifficultyNameRegistry.getDataEntries()));
        lines.forEach(line -> {
            EEchelonsApiMod.LOGGER.info(line);
            source.sendSuccess(() -> Component.literal(line), false);
        });
        return lines.size();
    }
}

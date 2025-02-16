package mod.gottsch.neoforge.eechelons.data;

import com.mojang.serialization.Codec;
import mod.gottsch.neoforge.eechelons.EEchelons;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * @author Mark Gottschling on 2/15/2025
 */
public class ModDataAttachements {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EEchelons.MODID);

    public static final Supplier<AttachmentType<Integer>> LEVEL = ATTACHMENT_TYPES.register(
            "level", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}

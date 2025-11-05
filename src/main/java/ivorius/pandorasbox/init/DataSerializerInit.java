package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effects.PBEffect;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;

public class DataSerializerInit {
    public static final EntityDataSerializer<PBEffect> PBEFFECTSERIALIZER = EntityDataSerializer.forValueType(PBEffect.STREAM_CODEC);
    public static void registerDataSerializers() {
        FabricTrackedDataRegistry.register(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "pandora_effect"), PBEFFECTSERIALIZER);
    }
}

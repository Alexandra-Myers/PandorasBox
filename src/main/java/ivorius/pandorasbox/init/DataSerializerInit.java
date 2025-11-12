package ivorius.pandorasbox.init;

import ivorius.pandorasbox.effects.PBEffect;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class DataSerializerInit {
    public static final EntityDataSerializer<PBEffect> PBEFFECTSERIALIZER = EntityDataSerializer.forValueType(PBEffect.STREAM_CODEC);
    public static void registerDataSerializers() {
        EntityDataSerializers.registerSerializer(PBEFFECTSERIALIZER);
    }
}

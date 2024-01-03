package ivorius.pandorasbox.init;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class DataSerializerInit {
    public static final EntityDataSerializer<PBEffect> PBEFFECTSERIALIZER = EntityDataSerializer.simple((friendlyByteBuf, pbEffect) -> {
        CompoundTag compound = new CompoundTag();
        CompoundTag effectCompound = new CompoundTag();
        PBEffectRegistry.writeEffect(pbEffect, effectCompound);
        compound.put("boxEffect", effectCompound);
        friendlyByteBuf.writeNbt(compound);
    }, friendlyByteBuf -> PBEffectRegistry.loadEffect(friendlyByteBuf.readNbt().getCompound("boxEffect")));
    public static void registerDataSerializers() {
        EntityDataSerializers.registerSerializer(PBEFFECTSERIALIZER);
    }
}

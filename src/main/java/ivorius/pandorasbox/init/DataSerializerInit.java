package ivorius.pandorasbox.init;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectDuplicateBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import xyz.nucleoid.packettweaker.PacketContext;

public class DataSerializerInit {
    public static final EntityDataSerializer<PBEffect> PBEFFECTSERIALIZER = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf friendlyByteBuf, PBEffect effect) {
            PacketContext packetContext = PacketContext.get();
            CompoundTag tag = new CompoundTag();
            if (packetContext.getTarget() == null) {
                friendlyByteBuf.writeNbt(tag);
                return;
            }
            RegistryAccess registryAccess = packetContext.getTarget().level().registryAccess();
            tag.put("boxEffect", PandorasBoxEntity.writeEffect(effect, registryAccess));
            friendlyByteBuf.writeNbt(tag);
        }

        @Override
        public PBEffect read(FriendlyByteBuf friendlyByteBuf) {
            PacketContext packetContext = PacketContext.get();
            CompoundTag tag = friendlyByteBuf.readNbt();
            if (tag == null || !tag.contains("boxEffect") || packetContext.getTarget() == null) return new PBEffectDuplicateBox(PBEffectDuplicateBox.MODE_BOX_IN_BOX);
            RegistryAccess registryAccess = packetContext.getTarget().level().registryAccess();
            return PandorasBoxEntity.loadEffect(tag.get("boxEffect"), registryAccess);
        }

        @Override
        public PBEffect copy(PBEffect object) {
            return object;
        }
    };
    public static void registerDataSerializers() {
        EntityDataSerializers.registerSerializer(PBEFFECTSERIALIZER);
    }
}

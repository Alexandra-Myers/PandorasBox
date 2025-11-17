package ivorius.pandorasbox.init;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectDuplicateBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.SidedUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Optionull;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Optional;

public class DataSerializerInit {
    public static final EntityDataSerializer<PBEffect> PBEFFECTSERIALIZER = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf friendlyByteBuf, PBEffect effect) {
            PacketContext packetContext = PacketContext.get();
            CompoundTag tag = new CompoundTag();
            Optional<RegistryAccess> access = Optional.ofNullable(Optionull.map(packetContext.getTarget(), target -> target.level().registryAccess()));
            tag.put("boxEffect", PandorasBoxEntity.writeEffectWithoutRegistries(effect, access));
            friendlyByteBuf.writeNbt(tag);
        }

        @Override
        public PBEffect read(FriendlyByteBuf friendlyByteBuf) {
            PacketContext packetContext = PacketContext.get();
            CompoundTag tag = friendlyByteBuf.readNbt();
            if (tag == null || !tag.contains("boxEffect")) return new PBEffectDuplicateBox(PBEffectDuplicateBox.MODE_BOX_IN_BOX);
            Optional<RegistryAccess> access;
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) access = SidedUtil.getAccessOnClient();
            else access = Optional.empty();
            access = access.or(() -> Optional.ofNullable(Optionull.map(packetContext.getTarget(), target -> target.level().registryAccess())));
            return PandorasBoxEntity.loadEffectWithoutRegistries(tag.get("boxEffect"), access);
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

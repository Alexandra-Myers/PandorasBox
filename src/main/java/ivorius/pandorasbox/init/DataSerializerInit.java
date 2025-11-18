package ivorius.pandorasbox.init;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectDuplicateBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.SidedUtil;
import net.minecraft.Optionull;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Optional;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class DataSerializerInit {
    private static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, MOD_ID);
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
            Optional<RegistryAccess> access = DistExecutor.unsafeRunForDist(() -> SidedUtil::getAccessOnClient, () -> Optional::empty);
            access = access.or(() -> Optional.ofNullable(Optionull.map(packetContext.getTarget(), target -> target.level().registryAccess())));
            return PandorasBoxEntity.loadEffectWithoutRegistries(tag.get("boxEffect"), access);
        }

        @Override
        public PBEffect copy(PBEffect object) {
            return object;
        }
    };
    public static void registerDataSerializers(IEventBus bus) {
        SERIALIZERS.register("box_effect", () -> PBEFFECTSERIALIZER);
        SERIALIZERS.register(bus);
    }
}

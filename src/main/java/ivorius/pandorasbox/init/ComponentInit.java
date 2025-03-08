package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.component.PBEffectComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public class ComponentInit {
    public static final DataComponentType<PBEffectComponent> EFFECT_COMPONENT = register("effect_holders", builder -> builder.persistent(PBEffectComponent.CODEC).networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(PBEffectComponent.CODEC)));
    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceKey.create(Registries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), unaryOperator.apply(DataComponentType.builder()).build());
    }
    public static void registerComponents() {

    }
}

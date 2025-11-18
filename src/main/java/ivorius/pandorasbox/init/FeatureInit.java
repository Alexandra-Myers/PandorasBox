package ivorius.pandorasbox.init;

import ivorius.pandorasbox.worldgen.LollipopFeature;
import ivorius.pandorasbox.worldgen.RainbowFeature;
import ivorius.pandorasbox.worldgen.configuration.LollipopConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class FeatureInit {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);
    @SuppressWarnings("unused")
    public static final RegistryObject<Feature<@NotNull LollipopConfiguration>> LOLLIPOP = register("lollipop", () -> new LollipopFeature(LollipopConfiguration.CODEC));
    public static final ResourceKey<@NotNull ConfiguredFeature<?, ?>> LOLLIPOPS = createKey("lollipops");
    @SuppressWarnings("unused")
    public static final RegistryObject<Feature<@NotNull LollipopConfiguration>> RAINBOW = register("rainbow", () -> new RainbowFeature(LollipopConfiguration.CODEC));
    public static final ResourceKey<@NotNull ConfiguredFeature<?, ?>> RAINBOWS = createKey("rainbows");
    private static <C extends FeatureConfiguration> RegistryObject<Feature<C>> register(String name, Supplier<Feature<C>> feature) {
        return FEATURES.register(name, feature);
    }
    private static ResourceKey<@NotNull ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MOD_ID, name));
    }
    public static void registerFeatures(IEventBus bus) {
        FEATURES.register(bus);
    }
}

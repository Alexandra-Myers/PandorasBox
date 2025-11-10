package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.worldgen.LollipopFeature;
import ivorius.pandorasbox.worldgen.RainbowFeature;
import ivorius.pandorasbox.worldgen.configuration.LollipopConfiguration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.NotNull;

public class FeatureInit {
    @SuppressWarnings("unused")
    public static final Feature<@NotNull LollipopConfiguration> LOLLIPOP = register("lollipop", new LollipopFeature(LollipopConfiguration.CODEC));
    public static final ResourceKey<@NotNull ConfiguredFeature<?, ?>> LOLLIPOPS = createKey("lollipops");
    @SuppressWarnings("unused")
    public static final Feature<@NotNull LollipopConfiguration> RAINBOW = register("rainbow", new RainbowFeature(LollipopConfiguration.CODEC));
    public static final ResourceKey<@NotNull ConfiguredFeature<?, ?>> RAINBOWS = createKey("rainbows");
    private static <C extends FeatureConfiguration> Feature<C> register(String name, Feature<C> feature) {
        return Registry.register(BuiltInRegistries.FEATURE, ResourceKey.create(BuiltInRegistries.FEATURE.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), feature);
    }
    private static ResourceKey<@NotNull ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name));
    }
    public static void registerFeatures() {

    }
}

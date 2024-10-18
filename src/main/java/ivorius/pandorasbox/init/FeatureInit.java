package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.worldgen.WorldGenColorfulTree;
import ivorius.pandorasbox.worldgen.WorldGenLollipop;
import ivorius.pandorasbox.worldgen.WorldGenMegaJungleCustom;
import ivorius.pandorasbox.worldgen.WorldGenRainbow;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class FeatureInit {
    public static final Feature<TreeConfiguration> LOLIPOP = register("lolipop", new WorldGenLollipop(TreeConfiguration.CODEC, 20));
    public static final Feature<TreeConfiguration> COLOURFUL_TREE = register("colourful_tree", new WorldGenColorfulTree(TreeConfiguration.CODEC, 20));
    public static final Feature<TreeConfiguration> RAINBOW = register("rainbow", new WorldGenRainbow(TreeConfiguration.CODEC, 20));
    public static final Feature<TreeConfiguration> MEGA_JUNGLE = register("mega_jungle", new WorldGenMegaJungleCustom(TreeConfiguration.CODEC, 20));
    private static <C extends FeatureConfiguration> Feature<C> register(String name, Feature<C> feature) {
        return Registry.register(BuiltInRegistries.FEATURE, ResourceKey.create(BuiltInRegistries.FEATURE.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name)), feature);
    }
    public static void registerFeatures() {

    }
}

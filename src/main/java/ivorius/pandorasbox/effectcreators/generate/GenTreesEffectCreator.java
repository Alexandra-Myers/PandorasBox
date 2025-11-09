package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.GenTreesEffect;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZConstant;
import ivorius.pandorasbox.random.ZValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.data.worldgen.features.TreeFeatures.*;
import static net.minecraft.data.worldgen.features.TreeFeatures.BIRCH;
import static net.minecraft.data.worldgen.features.TreeFeatures.DARK_OAK;
import static net.minecraft.data.worldgen.features.TreeFeatures.JUNGLE_TREE;
import static net.minecraft.data.worldgen.features.TreeFeatures.MEGA_JUNGLE_TREE;
import static net.minecraft.data.worldgen.features.TreeFeatures.SPRUCE;

public record GenTreesEffectCreator(DValue chancePerBlock, ZValue requiresSolidGround, IValue possibleTreeFlags, List<ResourceKey<ConfiguredFeature<?, ?>>> features) implements GenerateEffectCreator {
    public static final MapCodec<GenTreesEffectCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("chance_per_block").forGetter(GenTreesEffectCreator::chancePerBlock),
                            ZValue.CODEC.optionalFieldOf("requires_solid_ground", new ZConstant(true)).forGetter(GenTreesEffectCreator::requiresSolidGround),
                            IValue.CODEC.fieldOf("possible_tree_flags").forGetter(GenTreesEffectCreator::possibleTreeFlags),
                            ResourceKey.codec(Registries.CONFIGURED_FEATURE).listOf().fieldOf("tree_features").forGetter(GenTreesEffectCreator::features))
                    .apply(instance, GenTreesEffectCreator::new));

    @Override
    public GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random) {
        boolean requiresSolidGround = this.requiresSolidGround.getValue(random);
        double chancePerBlock = this.chancePerBlock.getValue(random) * (!requiresSolidGround ? 0.01 : 1);
        int possibleTreeFlags = this.possibleTreeFlags.getValue(random);
        return new GenTreesEffect(requiresSolidGround, chancePerBlock, possibleTreeFlags, features);
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffectCreator> codec() {
        return CODEC;
    }
}

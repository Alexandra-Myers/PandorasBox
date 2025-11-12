package ivorius.pandorasbox.effects.generate;

import com.mojang.datafixers.util.Either;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static ivorius.pandorasbox.effects.PBEffect.isBlockAnyOf;

public interface GenerateConvertEffect extends GenerateEffect {
    @Override
    default void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (!level.isClientSide()) {
            if (pass == 0 && !isBlockAnyOf(level.getBlockState(pos).getBlock(), excludedTargets())) {
                for (BlockMapper blockMapper : mappers()) {
                    if (!blockMapper.convert(level, entity, effectCenter, random, pass, pos, range, unifiedSeed)) break;
                }
            } else if (pass == 1) generators().forEach(generator -> generator.generate(level, entity, effectCenter, random, pass, pos, range, ratio, unifiedSeed));
            else if (pass != 0) spawners().forEach(entitySpawner -> entitySpawner.spawnEntities(level, entity, effectCenter, random, pass, pos, range, unifiedSeed));
            runPostConvert(level, entity, effectCenter, random, pass, pos, range, unifiedSeed);
        }
    }

    void runPostConvert(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, int unifiedSeed);

    Either<Block, TagKey<Block>>[] excludedTargets();

    List<BlockMapper> mappers();

    List<EntitySpawner> spawners();

    List<FeatureGenerator> generators();
}

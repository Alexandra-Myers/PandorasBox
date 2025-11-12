package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ivorius.pandorasbox.effects.PBEffect.setBlockToAirSafe;
import static ivorius.pandorasbox.effects.PBEffect.setBlockVarying;
import static net.minecraft.world.level.block.Block.dropResources;

public record GenReplaceEffect(Block[] blocks, Block[] blocksToReplace) implements GenerateEffect {
    public static final MapCodec<GenReplaceEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).fieldOf("blocks").forGetter(GenReplaceEffect::blocks),
                            PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).fieldOf("blocks_to_replace").forGetter(GenReplaceEffect::blocksToReplace))
                    .apply(instance, GenReplaceEffect::new));
    @Override
    public void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (!level.isClientSide()) {
            Block newBlock = blocks[random.nextInt(blocks.length)];
            BlockState prevState = level.getBlockState(pos);
            Block prevBlock = prevState.getBlock();
            boolean replace = false;
            for (Block block : blocksToReplace) {
                if (block == Blocks.WATER) {
                    FluidState fluidstate = level.getFluidState(pos);
                    if (fluidstate.is(FluidTags.WATER)) {
                        if (prevBlock instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock(null, level, pos, prevState).isEmpty()) continue;

                        if (!(prevState.getBlock() instanceof LiquidBlock)) {
                            if (!prevState.is(Blocks.KELP) && !prevState.is(Blocks.KELP_PLANT) && !prevState.is(Blocks.SEAGRASS) && !prevState.is(Blocks.TALL_SEAGRASS)) continue;

                            BlockEntity blockEntity = prevState.hasBlockEntity() ? level.getBlockEntity(pos) : null;
                            dropResources(prevState, level, pos, blockEntity);
                        }
                        setBlockToAirSafe(level, pos);
                        for (Direction direction : Direction.values()) {
                            BlockPos pos1 = pos.relative(direction);
                            BlockState state1 = level.getBlockState(pos1);
                            Block block1 = state1.getBlock();
                            FluidState fluidstate1 = level.getFluidState(pos1);
                            if (fluidstate1.is(FluidTags.WATER)) {
                                if (block1 instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock(null, level, pos1, state1).isEmpty())
                                    return;

                                if (!(state1.getBlock() instanceof LiquidBlock)) {
                                    if (!state1.is(Blocks.KELP) && !state1.is(Blocks.KELP_PLANT) && !state1.is(Blocks.SEAGRASS) && !state1.is(Blocks.TALL_SEAGRASS))
                                        return;

                                    BlockEntity blockEntity = state1.hasBlockEntity() ? level.getBlockEntity(pos1) : null;
                                    dropResources(state1, level, pos1, blockEntity);
                                }
                                setBlockToAirSafe(level, pos1);
                            }
                        }
                    }
                }
                if (prevBlock == block) {
                    replace = true;
                    break;
                }
            }

            if (replace) {
                setBlockVarying(level, pos, newBlock, unifiedSeed);
            }
        }
    }

    @Override
    public @Nullable ResourceKey<Biome> biome() {
        return null;
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffect> codec() {
        return CODEC;
    }
}

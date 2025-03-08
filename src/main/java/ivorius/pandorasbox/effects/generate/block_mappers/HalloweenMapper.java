package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record HalloweenMapper() implements BlockMapper {
    public static final MapCodec<HalloweenMapper> CODEC = MapCodec.unit(HalloweenMapper::new);
    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        BlockPos posBelow = blockPos.below();
        BlockState blockBelowState = serverLevel.getBlockState(posBelow);

        return Block.isShapeFullBlock(blockBelowState.getBlockSupportShape(serverLevel, posBelow)) && state.isAir() && state.getBlock() != Blocks.WATER && random.nextInt(5 * 5) == 0;
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        BlockPos posBelow = pos.below();
        int b = serverLevel.random.nextInt(7);

        if (b == 0) {
            setBlockSafe(serverLevel, posBelow, Blocks.NETHERRACK.defaultBlockState());
            setBlockSafe(serverLevel, pos, Blocks.FIRE.defaultBlockState());
        } else if (b == 1) {
            setBlockSafe(serverLevel, pos, Blocks.JACK_O_LANTERN.defaultBlockState());
        } else if (b == 2) {
            setBlockSafe(serverLevel, pos, Blocks.CARVED_PUMPKIN.defaultBlockState());
        } else if (b == 3) {
            setBlockSafe(serverLevel, posBelow, Blocks.FARMLAND.defaultBlockState());
            setBlockSafe(serverLevel, pos, Blocks.PUMPKIN_STEM.getStateDefinition().getPossibleStates().get(serverLevel.random.nextInt(4) + 4));
        } else if (b == 4) {
            setBlockSafe(serverLevel, pos, Blocks.CAKE.defaultBlockState());
        } else if(b == 5) {
            ItemEntity entityItem = new ItemEntity(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
            entityItem.setPickUpDelay(20);
            serverLevel.addFreshEntity(entityItem);
        } else {
            setBlockSafe(serverLevel, posBelow, Blocks.SOUL_SOIL.defaultBlockState());
            setBlockSafe(serverLevel, pos, Blocks.SOUL_FIRE.defaultBlockState());
        }
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}

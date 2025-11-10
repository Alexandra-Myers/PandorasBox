package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record ChrismasGiftsMapper() implements BlockMapper {
    public static final MapCodec<ChrismasGiftsMapper> CODEC = MapCodec.unit(ChrismasGiftsMapper::new);

    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return state.isAir();
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        boolean setSnow = true;

        BlockPos posBelow = pos.below();
        if (serverLevel.loadedAndEntityCanStandOn(posBelow, entity)) {
            if (random.nextInt(100) == 0) {
                setBlockSafe(serverLevel, pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(serverLevel.random)));
                ChestBlockEntity chestBlockEntity = (ChestBlockEntity) serverLevel.getBlockEntity(pos);

                if (chestBlockEntity != null) {
                    Collection<RandomizedItemStack> itemSelection = PandorasBoxHelper.assembleRandomisedStacks(BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK, PandorasBoxHelper.blocksAndItems);
                    RandomizedItemStack chestContent = WeightedSelector.selectItem(random, itemSelection);
                    ItemStack stack = chestContent.itemStack().copy();
                    stack.setCount(chestContent.min() + random.nextInt(chestContent.max() - chestContent.min() + 1));

                    chestBlockEntity.setItem(serverLevel.random.nextInt(chestBlockEntity.getContainerSize()), stack);
                }

                setSnow = false;
            } else if (random.nextInt(100) == 0) {
                setBlockSafe(serverLevel, pos, Blocks.REDSTONE_LAMP.defaultBlockState());
                setBlockSafe(serverLevel, posBelow, Blocks.REDSTONE_BLOCK.defaultBlockState());
                setSnow = false;
            } else if (random.nextInt(100) == 0) {
                setBlockSafe(serverLevel, pos, Blocks.CAKE.defaultBlockState());
                setSnow = false;
            } else if (random.nextInt(100) == 0) {
                ItemEntity entityItem = new ItemEntity(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                entityItem.setPickUpDelay(20);
                serverLevel.addFreshEntity(entityItem);
            }
        }

        if (setSnow && Blocks.SNOW.defaultBlockState().canSurvive(serverLevel, pos)) {
            setBlockSafe(serverLevel, pos, Blocks.SNOW.defaultBlockState());
        }
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}

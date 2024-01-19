/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.block;

import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.items.PandorasBoxItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created by lukas on 15.04.14.
 */
public class PandorasBoxBlock extends BaseEntityBlock implements SimpleWaterloggedBlock
{
    public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public PandorasBoxBlock() {
        super(Block.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(0.5f));
        registerDefaultState(stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public @NotNull BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_.setValue(DIRECTION, p_185499_2_.rotate(p_185499_1_.getValue(DIRECTION)));
    }

    public @NotNull BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(DIRECTION)));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        return Block.box(3.2, 0.0, 3.2, 12.8, 9.6, 12.8);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult rayTraceResult) {
        PandorasBoxItem.executeRandomEffect(worldIn, player, pos, false);
        worldIn.removeBlock(pos, false);
        worldIn.removeBlockEntity(pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity livingEntity, @NotNull ItemStack itemStack) {
        worldIn.setBlock(pos, this.defaultBlockState().setValue(DIRECTION, livingEntity.getDirection().getOpposite()), 2);
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);

        if (tileEntity instanceof PandorasBoxBlockEntity)
            ((PandorasBoxBlockEntity) tileEntity).setRotationYaw(livingEntity.yBodyRot + 180);
        super.setPlacedBy(worldIn, pos, state, livingEntity, itemStack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION, WATERLOGGED);
    }

    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction[] var2 = context.getNearestLookingDirections();

        for (Direction lvt_5_1_ : var2) {
            BlockState lvt_6_2_;
            if (lvt_5_1_.getAxis() == Direction.Axis.Y) {
                lvt_6_2_ = this.defaultBlockState().setValue(DIRECTION, context.getHorizontalDirection());
            } else {
                lvt_6_2_ = this.defaultBlockState().setValue(DIRECTION, lvt_5_1_.getOpposite());
            }
            Level level = context.getLevel();
            BlockPos blockpos = context.getClickedPos();
            boolean flag = level.getFluidState(blockpos).getType() == Fluids.WATER;
            lvt_6_2_ = lvt_6_2_.setValue(WATERLOGGED, flag);

            if (lvt_6_2_.canSurvive(context.getLevel(), context.getClickedPos())) {
                return lvt_6_2_;
            }
        }

        return null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos p_153215_, @NotNull BlockState p_153216_) {
        return Registry.TEPB.get().create(p_153215_, p_153216_);
    }
}

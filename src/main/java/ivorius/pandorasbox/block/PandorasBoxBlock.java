/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.block;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.BlockEntityInit;
import ivorius.pandorasbox.init.ItemInit;
import ivorius.pandorasbox.item.PandorasBoxItem;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 15.04.14.
 */
public class PandorasBoxBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> DIRECTION = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public PandorasBoxBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false));
    }
    public PandorasBoxBlock() {
        this(Block.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(0.5f));
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(DIRECTION, rotation.rotate(state.getValue(DIRECTION)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(DIRECTION)));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        return Block.box(3.2, 0.0, 3.2, 12.8, 9.6, 12.8);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        PandorasBoxEntity result = PandorasBoxItem.createEffect(level, player, pos, false, ItemInit.PBI.getDefaultInstance());
        if (result == null) return InteractionResult.PASS;
        level.removeBlock(pos, false);
        level.removeBlockEntity(pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity livingEntity, @NotNull ItemStack itemStack) {
        level.setBlock(pos, this.defaultBlockState().setValue(DIRECTION, livingEntity.getDirection().getOpposite()), 2);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof PandorasBoxBlockEntity)
            ((PandorasBoxBlockEntity) blockEntity).setRotationYaw(livingEntity.getYRot() + 180);
        super.setPlacedBy(level, pos, state, livingEntity, itemStack);
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
        return BlockEntityInit.BEPB.create(p_153215_, p_153216_);
    }
}

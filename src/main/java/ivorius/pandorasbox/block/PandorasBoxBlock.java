/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.block;

import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.items.PandorasBoxItem;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by lukas on 15.04.14.
 */
public class PandorasBoxBlock extends Block implements ITileEntityProvider, IWaterLoggable {
    public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public PandorasBoxBlock() {
        super(Block.Properties.of(Material.WOOD).strength(0.5f));
        registerDefaultState(stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_.setValue(DIRECTION, p_185499_2_.rotate(p_185499_1_.getValue(DIRECTION)));
    }

    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(DIRECTION)));
    }
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Registry.TEPB.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return Block.box(3.2, 0.0, 3.2, 12.8, 9.6, 12.8);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        PandorasBoxItem.executeRandomEffect(worldIn, player, pos, false);
        worldIn.removeBlock(pos, false);
        worldIn.removeBlockEntity(pos);

        return ActionResultType.SUCCESS;
    }
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        worldIn.setBlock(pos, this.defaultBlockState().setValue(DIRECTION, livingEntity.getDirection().getOpposite()), 2);
        TileEntity tileEntity = worldIn.getBlockEntity(pos);

        if (tileEntity instanceof PandorasBoxBlockEntity)
            ((PandorasBoxBlockEntity) tileEntity).setRotationYaw(livingEntity.yRot + 180);
        super.setPlacedBy(worldIn, pos, state, livingEntity, itemStack);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction[] var2 = context.getNearestLookingDirections();

        for (Direction lvt_5_1_ : var2) {
            BlockState lvt_6_2_;
            if (lvt_5_1_.getAxis() == Direction.Axis.Y) {
                lvt_6_2_ = this.defaultBlockState().setValue(DIRECTION, context.getHorizontalDirection());
            } else {
                lvt_6_2_ = this.defaultBlockState().setValue(DIRECTION, lvt_5_1_.getOpposite());
            }
            IWorld iworld = context.getLevel();
            BlockPos blockpos = context.getClickedPos();
            boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
            lvt_6_2_ = lvt_6_2_.setValue(WATERLOGGED, flag);

            if (lvt_6_2_.canSurvive(context.getLevel(), context.getClickedPos())) {
                return lvt_6_2_;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return Registry.TEPB.get().create();
    }
}

package ivorius.pandorasbox.worldgen.structure.processor;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import ivorius.pandorasbox.init.BlockInit;
import ivorius.pandorasbox.init.StructureInit;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Map;

public class EndStoneReplaceProcessor extends StructureProcessor {
	public static final Codec<EndStoneReplaceProcessor> CODEC = Codec.unit(() -> EndStoneReplaceProcessor.INSTANCE);
	public static final EndStoneReplaceProcessor INSTANCE = new EndStoneReplaceProcessor();
	private final Map<Block, Block> replacements = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put(Blocks.COBBLESTONE, Blocks.END_STONE);
		hashMap.put(Blocks.MOSSY_COBBLESTONE, Blocks.END_STONE);
		hashMap.put(Blocks.STONE, Blocks.END_STONE);
		hashMap.put(Blocks.STONE_BRICKS, Blocks.END_STONE_BRICKS);
		hashMap.put(Blocks.MOSSY_STONE_BRICKS, Blocks.END_STONE_BRICKS);
		hashMap.put(Blocks.COBBLESTONE_STAIRS, BlockInit.END_STONE_STAIRS);
		hashMap.put(Blocks.MOSSY_COBBLESTONE_STAIRS, BlockInit.END_STONE_STAIRS);
		hashMap.put(Blocks.STONE_STAIRS, BlockInit.END_STONE_STAIRS);
		hashMap.put(Blocks.STONE_BRICK_STAIRS, Blocks.END_STONE_BRICK_STAIRS);
		hashMap.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.END_STONE_BRICK_STAIRS);
		hashMap.put(Blocks.COBBLESTONE_SLAB, BlockInit.END_STONE_SLAB);
		hashMap.put(Blocks.MOSSY_COBBLESTONE_SLAB, BlockInit.END_STONE_SLAB);
		hashMap.put(Blocks.SMOOTH_STONE_SLAB, BlockInit.END_STONE_SLAB);
		hashMap.put(Blocks.STONE_SLAB, BlockInit.END_STONE_SLAB);
		hashMap.put(Blocks.STONE_BRICK_SLAB, Blocks.END_STONE_BRICK_SLAB);
		hashMap.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.END_STONE_BRICK_SLAB);
		hashMap.put(Blocks.STONE_BRICK_WALL, Blocks.END_STONE_BRICK_WALL);
		hashMap.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.END_STONE_BRICK_WALL);
		hashMap.put(Blocks.COBBLESTONE_WALL, BlockInit.END_STONE_WALL);
		hashMap.put(Blocks.MOSSY_COBBLESTONE_WALL, BlockInit.END_STONE_WALL);
		hashMap.put(Blocks.CHISELED_STONE_BRICKS, BlockInit.CHISELED_END_STONE_BRICKS);
		hashMap.put(Blocks.CRACKED_STONE_BRICKS, Blocks.END_STONE_BRICKS);
        hashMap.put(Blocks.INFESTED_COBBLESTONE, BlockInit.INFESTED_END_STONE);
        hashMap.put(Blocks.INFESTED_STONE, BlockInit.INFESTED_END_STONE);
        hashMap.put(Blocks.INFESTED_STONE_BRICKS, BlockInit.INFESTED_END_STONE_BRICKS);
        hashMap.put(Blocks.INFESTED_CRACKED_STONE_BRICKS, BlockInit.INFESTED_END_STONE_BRICKS);
        hashMap.put(Blocks.INFESTED_MOSSY_STONE_BRICKS, BlockInit.INFESTED_END_STONE_BRICKS);
        hashMap.put(Blocks.INFESTED_CHISELED_STONE_BRICKS, BlockInit.INFESTED_CHISELED_END_STONE_BRICKS);
	});

	private EndStoneReplaceProcessor() {
	}

	@Override
	public StructureTemplate.StructureBlockInfo processBlock(
		LevelReader level,
		BlockPos pos,
		BlockPos pos2,
		StructureTemplate.StructureBlockInfo info,
		StructureTemplate.StructureBlockInfo info1,
		StructurePlaceSettings settings
	) {
		Block block = this.replacements.get(info1.state().getBlock());
		if (block == null) {
			return info1;
		} else {
			BlockState blockState = info1.state();
			BlockState blockState2 = block.defaultBlockState();
			if (blockState.hasProperty(StairBlock.FACING)) {
				blockState2 = blockState2.setValue(StairBlock.FACING, blockState.getValue(StairBlock.FACING));
			}

			if (blockState.hasProperty(StairBlock.HALF)) {
				blockState2 = blockState2.setValue(StairBlock.HALF, blockState.getValue(StairBlock.HALF));
			}

			if (blockState.hasProperty(SlabBlock.TYPE)) {
				blockState2 = blockState2.setValue(SlabBlock.TYPE, blockState.getValue(SlabBlock.TYPE));
			}

			return new StructureTemplate.StructureBlockInfo(info1.pos(), blockState2, info1.nbt());
		}
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureInit.END_STONE_REPLACE;
	}
}
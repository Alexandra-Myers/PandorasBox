/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ivorius.pandorasbox.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * See {@link BlockTags} for vanilla tags.
 * Note that addition to some vanilla tags implies having certain functionality.
 */
public final class ConventionalBlockTags {
	private ConventionalBlockTags() {
	}

	/**
	 * Natural stone-like blocks that can be used as a base ingredient in recipes that take stone.
	 */
	public static final TagKey<Block> STONES = register("stones");
	public static final TagKey<Block> COBBLESTONES = register("cobblestones");
	public static final TagKey<Block> NETHERITE_SCRAP_ORES = register("ores/netherite_scrap");
	public static final TagKey<Block> QUARTZ_ORES = register("ores/quartz");
	public static final TagKey<Block> BARRELS = register("barrels");
	public static final TagKey<Block> CHESTS = register("chests");
	public static final TagKey<Block> GLASS_BLOCKS = register("glass_blocks");
	public static final TagKey<Block> GLASS_PANES = register("glass_panes");
	public static final TagKey<Block> CONCRETES = register("concretes");
	public static final TagKey<Block> VILLAGER_JOB_SITES = register("villager_job_sites");
	public static final TagKey<Block> SANDSTONE_BLOCKS = register("sandstone/blocks");
	public static final TagKey<Block> STORAGE_BLOCKS_COAL = register("storage_blocks/coal");
	public static final TagKey<Block> STORAGE_BLOCKS_COPPER = register("storage_blocks/copper");
	public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = register("storage_blocks/diamond");
	public static final TagKey<Block> STORAGE_BLOCKS_DRIED_KELP = register("storage_blocks/dried_kelp");
	public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = register("storage_blocks/emerald");
	public static final TagKey<Block> STORAGE_BLOCKS_GOLD = register("storage_blocks/gold");
	public static final TagKey<Block> STORAGE_BLOCKS_IRON = register("storage_blocks/iron");
	public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = register("storage_blocks/lapis");
	public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = register("storage_blocks/netherite");
	public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = register("storage_blocks/redstone");
	public static final TagKey<Block> STORAGE_BLOCKS_SLIME = register("storage_blocks/slime");
	public static final TagKey<Block> STORAGE_BLOCKS_WHEAT = register("storage_blocks/wheat");
	public static final TagKey<Block> PLAYER_WORKSTATIONS_CRAFTING_TABLES = register("player_workstations/crafting_tables");
	public static final TagKey<Block> PLAYER_WORKSTATIONS_FURNACES = register("player_workstations/furnaces");

	private static TagKey<Block> register(String tagId) {
		return PandoraItemTags.registerC(Registries.BLOCK, tagId);
	}
}

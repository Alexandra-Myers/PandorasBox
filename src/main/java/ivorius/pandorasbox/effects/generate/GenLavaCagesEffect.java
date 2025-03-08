package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.BlockPositions;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static ivorius.pandorasbox.effects.PBEffect.setBlockVarying;
import static ivorius.pandorasbox.effects.PBEffect.setBlockVaryingUnsafeSrc;

public record GenLavaCagesEffect(Optional<Block> lavaBlock, Block cageBlock, Optional<Block> fillBlock, Block floorBlock, Integer heightOffset, Integer wallDist) implements GenerateEffect {
    public static final MapCodec<GenLavaCagesEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("lava_block").forGetter(GenLavaCagesEffect::lavaBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("cage_block").forGetter(GenLavaCagesEffect::cageBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fill_block").forGetter(GenLavaCagesEffect::fillBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("floor_block").forGetter(GenLavaCagesEffect::floorBlock),
                            Codec.INT.fieldOf("height_offset").forGetter(GenLavaCagesEffect::heightOffset),
                            Codec.INT.fieldOf("wall_distance").forGetter(GenLavaCagesEffect::wallDist))
                    .apply(instance, GenLavaCagesEffect::new));

    @Override
    public void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (!level.isClientSide()) {
            if (!level.loadedAndEntityCanStandOn(pos, entity)) {
                List<Player> outerList = level.getEntitiesOfClass(Player.class, BlockPositions.expandToAABB(pos, 3.5, 3.5, 3.5));

                if (!outerList.isEmpty()) {
                    for(Player player : outerList) {
                        int playerY = player.blockPosition().getY();
                        int playerX = player.blockPosition().getX();
                        int playerZ = player.blockPosition().getZ();
                        int floor = playerY - 1;
                        int ceil = playerY + heightOffset;
                        createFloorOrCeil(level, playerX, floor, playerZ, wallDist, unifiedSeed);
                        createFloorOrCeil(level, playerX, ceil, playerZ, wallDist, unifiedSeed);
                        createWalls(level, playerX, playerY, ceil, playerZ, wallDist, unifiedSeed);
                        fill(level, playerX, playerY, ceil, playerZ, wallDist, unifiedSeed);
                    }
                }
            }
        }
    }
    public void createFloorOrCeil(Level world, int originX, int y, int originZ, int offset, int unifiedSeed) {
        for (int x = originX - offset; x <= originX + offset; x++) {
            for(int z = originZ - offset; z <= originZ + offset; z++) {
                setBlockVaryingUnsafeSrc(world, new BlockPos(x, y, z), floorBlock, unifiedSeed);
            }
        }
    }
    public void createWalls(Level world, int originX, int originY, int ceilheight, int originZ, int offset, int unifiedSeed) {
        for (int x = originX - offset; x <= originX + offset; x++) {
            for (int y = originY; y < ceilheight; y++) {
                for(int z = originZ - offset; z <= originZ + offset; z++) {
                    if(x == originX - offset || x == originX + offset || z == originZ - offset || z == originZ + offset) {
                        setBlockVarying(world, new BlockPos(x, y, z), cageBlock, unifiedSeed);
                    }
                }
            }
        }
    }
    public void fill(Level world, int originX, int originY, int ceilheight, int originZ, int offset, int unifiedSeed) {
        offset -= 1;
        for (int x = originX - offset; x <= originX + offset; x++) {
            for (int y = originY; y < ceilheight - 1; y++) {
                for(int z = originZ - offset; z <= originZ + offset; z++) {
                    setBlockVarying(world, new BlockPos(x, y, z), fillBlock.orElseGet(lavaBlock::get), unifiedSeed);
                }
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

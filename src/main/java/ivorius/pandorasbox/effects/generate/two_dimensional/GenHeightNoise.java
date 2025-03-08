package ivorius.pandorasbox.effects.generate.two_dimensional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record GenHeightNoise(int minShift, int maxShift, int minTowerSize, int maxTowerSize, int blockSize) implements Generate2D {
    public static final MapCodec<GenHeightNoise> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("min_shift").forGetter(GenHeightNoise::minShift),
                            Codec.INT.fieldOf("max_shift").forGetter(GenHeightNoise::maxShift),
                            Codec.INT.fieldOf("min_tower_size").forGetter(GenHeightNoise::minTowerSize),
                            Codec.INT.fieldOf("max_tower_size").forGetter(GenHeightNoise::maxTowerSize),
                            Codec.INT.fieldOf("block_size").forGetter(GenHeightNoise::blockSize))
                    .apply(instance, GenHeightNoise::new));
    @Override
    public void generateOnSurface(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, BlockPos pos, double distance, double range, int pass, int unifiedSeed) {
        if (!world.isClientSide()) {
            int randomX = pos.getX() - (pos.getX() % blockSize);
            int randomZ = pos.getZ() - (pos.getZ() % blockSize);
            Random usedRandom = new Random(new Random(randomX).nextLong() ^ new Random(randomZ).nextLong());

            int shift = minShift + usedRandom.nextInt(maxShift - minShift + 1);
            int towerSize = minTowerSize + usedRandom.nextInt(maxTowerSize - minTowerSize + 1);

            int towerMinY = pos.getY() - towerSize / 2;
            int minEffectY = towerMinY + Math.min(0, shift);
            int maxEffectY = towerMinY + towerSize + Math.max(0, shift);

            List<Player> entityList = world.getEntitiesOfClass(Player.class, new AABB(pos.getX() - 2.0, minEffectY - 4, pos.getZ() - 3.0, pos.getX() + 4.0, maxEffectY + 4, pos.getZ() + 4.0));

            if (entityList.isEmpty()) {
                BlockState[] blockStates = new BlockState[towerSize];
                for (int y = 0; y < towerSize; y++)
                    blockStates[y] = world.getBlockState(pos.above(towerSize / 2 - y));

                for (int y = 0; y < towerSize; y++)
                    setBlockSafe(world, new BlockPos(pos.getX(), towerMinY + y, pos.getZ()), blockStates[y]);
            }
        }
    }

    @Override
    public @NotNull MapCodec<? extends Generate2D> codec() {
        return CODEC;
    }
}

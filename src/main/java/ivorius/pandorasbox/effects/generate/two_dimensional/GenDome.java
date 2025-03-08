package ivorius.pandorasbox.effects.generate.two_dimensional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static ivorius.pandorasbox.effects.PBEffect.setBlockVarying;

public record GenDome(Block block, Optional<Block> fillBlock) implements Generate2D {
    public static final MapCodec<GenDome> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(GenDome::block),
                            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fill_block").forGetter(GenDome::fillBlock))
                    .apply(instance, GenDome::new));
    @Override
    public void generateOnSurface(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, BlockPos pos, double distance, double range, int pass, int unifiedSeed) {
        if (!world.isClientSide) {
            int domeHeightY = Mth.ceil(range);

            for (int y = -domeHeightY; y <= domeHeightY; y++) {
                BlockPos shiftedPos = pos.above(y);

                if (pass == 0) {
                    if (isSpherePart(shiftedPos.getX() + 0.5, shiftedPos.getY() + 0.5, shiftedPos.getZ() + 0.5, effectCenter.x, effectCenter.y, effectCenter.z, range - 1.5, range)) {
                        if (world.getBlockState(shiftedPos).canSurvive(world, shiftedPos)) {
                            setBlockVarying(world, shiftedPos, this.block, unifiedSeed);
                        }
                    }
                } else if (pass == 1 && fillBlock.isPresent()) {
                    if (isSpherePart(shiftedPos.getX() + 0.5, shiftedPos.getY() + 0.5, shiftedPos.getZ() + 0.5, effectCenter.x, effectCenter.y, effectCenter.z, 0.0, range - 1.5)) {
                        if (world.getBlockState(shiftedPos).canSurvive(world, shiftedPos)) {
                            setBlockVarying(world, shiftedPos, this.fillBlock.get(), unifiedSeed);
                        }
                    }
                }
            }
        }
    }

    public static boolean isSpherePart(double x, double y, double z, double centerX, double centerY, double centerZ, double distStart, double distEnd) {
        double xDist = centerX - x;
        double yDist = centerY - y;
        double zDist = centerZ - z;
        double rangeSQ = xDist * xDist + yDist * yDist + zDist * zDist;

        return rangeSQ >= distStart * distStart && rangeSQ < distEnd * distEnd;
    }

    @Override
    public @NotNull MapCodec<? extends Generate2D> codec() {
        return CODEC;
    }
}

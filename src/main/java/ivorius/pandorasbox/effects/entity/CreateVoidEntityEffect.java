package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.setBlockToAirSafe;

public record CreateVoidEntityEffect() implements EntityEffect {
    public static final MapCodec<CreateVoidEntityEffect> CODEC = MapCodec.unit(CreateVoidEntityEffect::new);

    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        if (entity instanceof Player) {
            int baseY = Mth.floor(entity.getY());
            int baseX = Mth.floor(entity.getX());
            int baseZ = Mth.floor(entity.getZ());

            for (int x = -1; x <= 1; x++) {
                for (int y = -8; y <= 2; y++) {
                    for (int z = -1; z <= 1; z++) {
                        setBlockToAirSafe(serverLevel, new BlockPos(baseX + x, baseY + y, baseZ + z));
                    }
                }
            }
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

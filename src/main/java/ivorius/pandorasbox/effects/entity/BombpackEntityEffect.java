package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public record BombpackEntityEffect() implements EntityEffect {
    public static final MapCodec<BombpackEntityEffect> CODEC = MapCodec.unit(BombpackEntityEffect::new);
    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        Random itemRandom = new Random(entity.getId());
        double expectedBomb = itemRandom.nextDouble();
        if (newRatio >= expectedBomb && prevRatio < expectedBomb) {
            PrimedTnt primedTnt = new PrimedTnt(serverLevel, entity.getX(), entity.getY(), entity.getZ(), null);
            primedTnt.setFuse(60 + random.nextInt(160));

            serverLevel.addFreshEntity(primedTnt);
            primedTnt.startRiding(entity, true, true);
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

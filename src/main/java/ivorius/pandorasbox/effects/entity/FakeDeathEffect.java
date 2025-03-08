package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public record FakeDeathEffect() implements EntityEffect {
    public static final MapCodec<FakeDeathEffect> CODEC = MapCodec.unit(FakeDeathEffect::new);
    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        Random itemRandom = new Random(entity.getId());
        double expectedDeath = itemRandom.nextDouble();
        if (newRatio >= expectedDeath) {
            if (prevRatio < expectedDeath) {
                if (entity instanceof ServerPlayer player) {
                    ServerPlayNetworking.send(player, new PandorasBox.ClientboundUpdateFakeDeathPacket());
                    entity.hurtServer(serverLevel, entity.damageSources().genericKill(), 0.1F);
                    entity.setPose(Pose.DYING);
                } else {
                    entity.kill(serverLevel);
                    entity.setHealth(entity.getMaxHealth());
                }
            }
            entity.hurtDuration = 10;
            entity.hurtTime = entity.hurtDuration;
            entity.hurtMarked = true;
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

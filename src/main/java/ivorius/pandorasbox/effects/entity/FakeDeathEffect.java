package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public record FakeDeathEffect() implements EntityEffect {
    public static final MapCodec<FakeDeathEffect> CODEC = MapCodec.unit(FakeDeathEffect::new);
    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        Random itemRandom = new Random(entity.getId());
        double expectedDeath = itemRandom.nextDouble();
        if (newRatio >= expectedDeath) {
            if (prevRatio < expectedDeath) {
                if (entity instanceof ServerPlayer player) {
                    ServerPlayNetworking.send(player, new PandorasBox.ClientboundUpdateFakeDeathPacket());
                    double x = effectCenter.x() - player.getX();
                    double z = effectCenter.z() - player.getZ();
                    player.indicateDamage(x, z);
                    player.stopUsingItem();
                    serverLevel.broadcastEntityEvent(player, (byte) 3);
                    entity.setPose(Pose.DYING);
                } else {
                    float health = entity.getHealth();
                    entity.kill(serverLevel);
                    entity.setHealth(health);
                }
                entity.hurtMarked = true;
            }
        }
    }

    @Override
    public void affectEntityClient(ClientLevel clientLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        Random itemRandom = new Random(entity.getId());
        double expectedDeath = itemRandom.nextDouble();
        if (newRatio >= expectedDeath) {
            entity.deathTime++;
        }
        if (newRatio >= 1 && !(entity instanceof Player)) {
            entity.deathTime = 0;
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

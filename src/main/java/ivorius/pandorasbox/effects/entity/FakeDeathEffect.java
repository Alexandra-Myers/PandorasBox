package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record FakeDeathEffect() implements EntityEffect {
    public static final MapCodec<FakeDeathEffect> CODEC = MapCodec.unit(FakeDeathEffect::new);
    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        if (!(entity instanceof ServerPlayer player)) {
            entity.kill(serverLevel);
            entity.setHealth(entity.getMaxHealth());
        } else {
            ServerPlayNetworking.send(player, new PandorasBox.ClientboundUpdateFakeDeathPacket());
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

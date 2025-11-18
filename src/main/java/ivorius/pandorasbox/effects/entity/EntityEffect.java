package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface EntityEffect {
    Codec<EntityEffect> CODEC = Init.ENTITY_EFFECT_TYPE_REGISTRY.get().getCodec()
            .dispatch(EntityEffect::codec, MapCodec::codec);
    default void affectEntity(Level level, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        if (level instanceof ServerLevel serverLevel) affectEntityServer(serverLevel, box, effectCenter, random, entity, newRatio, prevRatio, strength);
        else if (level instanceof ClientLevel clientLevel) affectEntityClient(clientLevel, box, effectCenter, random, entity, newRatio, prevRatio, strength);
    }
    void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength);
    @SuppressWarnings("unused")
    default void affectEntityClient(ClientLevel clientLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {

    }

    default void finalise(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {

    }

    @NotNull MapCodec<? extends EntityEffect> codec();
}

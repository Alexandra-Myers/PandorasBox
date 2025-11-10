package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record CrushEntityEffect(int cycles, double speed) implements EntityEffect {
    public static final MapCodec<CrushEntityEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("cycles").forGetter(CrushEntityEffect::cycles),
                            Codec.DOUBLE.fieldOf("speed").forGetter(CrushEntityEffect::speed))
                    .apply(instance, CrushEntityEffect::new));
    @Override
    public void affectEntity(Level level, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        boolean lift = ((newRatio * cycles) % 1.000001) < 0.7; // We want 1.0 inclusive

        double x = entity.getDeltaMovement().x;
        double y = entity.getDeltaMovement().y;
        double z = entity.getDeltaMovement().z;
        if (lift) {
            entity.setDeltaMovement(x, y * (1.0f - strength) + strength * speed, z);
            entity.hurtMarked = true;
        } else {
            entity.setDeltaMovement(x, y - strength * speed, z);
            entity.hurtMarked = true;
        }
    }

    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {

    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

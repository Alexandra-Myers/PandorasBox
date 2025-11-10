package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record BombermanEntityEffect(int bombs) implements EntityEffect {
    public static final MapCodec<BombermanEntityEffect> CODEC = Codec.INT.xmap(BombermanEntityEffect::new, BombermanEntityEffect::bombs).fieldOf("bombs");
    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        int prevBombs = Mth.floor(prevRatio * bombs);
        int newBombs = Mth.floor(newRatio * bombs);
        int bombs = newBombs - prevBombs;

        for (int i = 0; i < bombs; i++) {
            PrimedTnt entitytntprimed = new PrimedTnt(serverLevel, entity.getX(), entity.getY(), entity.getZ(), null);
            entitytntprimed.setFuse(45 + random.nextInt(20));

            serverLevel.addFreshEntity(entitytntprimed);
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

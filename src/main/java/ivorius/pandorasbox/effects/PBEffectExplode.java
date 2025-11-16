package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectExplode extends PBEffectNormal {
    public static final ResourceLocation EXPLODE = new ResourceLocation(PandorasBox.MOD_ID, "render_explode");
    public static final MapCodec<PBEffectExplode> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.FLOAT.fieldOf("explosion_radius").forGetter(PBEffectExplode::getExplosionRadius),
                            Codec.BOOL.fieldOf("burning").forGetter(PBEffectExplode::isBurning),
                            ExplosionInteraction.CODEC.fieldOf("explosion_interaction").forGetter(PBEffectExplode::getInteraction))
                    .apply(instance, PBEffectExplode::new));
    public final ExplosionInteraction interaction;
    public final float explosionRadius;
    public final boolean burning;

    public ExplosionInteraction getInteraction() {
        return interaction;
    }

    public float getExplosionRadius() {
        return explosionRadius;
    }

    public boolean isBurning() {
        return burning;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {

    }

    public PBEffectExplode(int maxTicksAlive, float explosionRadius, boolean burning, ExplosionInteraction interaction) {
        super(maxTicksAlive);
        this.explosionRadius = explosionRadius;
        this.burning = burning;
        this.interaction = interaction;
    }

    @Override
    public void finalizeEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {
        super.finalizeEffect(level, entity, effectCenter, random);

        if (!level.isClientSide())
            level.explode(entity, entity.getX(), entity.getY(), entity.getZ(), explosionRadius, burning, interaction.toVanilla());
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation rendererResourceLocationForEffect() {
        return EXPLODE;
    }

    public enum ExplosionInteraction implements StringRepresentable {
        NONE("none", Level.ExplosionInteraction.NONE),
        BLOCK("block", Level.ExplosionInteraction.BLOCK),
        MOB("mob", Level.ExplosionInteraction.MOB),
        TNT("tnt", Level.ExplosionInteraction.TNT);
        public static final Codec<ExplosionInteraction> CODEC = StringRepresentable.fromEnum(ExplosionInteraction::values);

        private final String name;
        private final Level.ExplosionInteraction vanilla;

        ExplosionInteraction(String name, Level.ExplosionInteraction vanilla) {
            this.name = name;
            this.vanilla = vanilla;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        public Level.ExplosionInteraction toVanilla() {
            return vanilla;
        }
    }
}

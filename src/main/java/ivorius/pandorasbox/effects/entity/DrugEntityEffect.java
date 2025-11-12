package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.mods.PsychedelicraftHooks;
import ivorius.psychedelicraft.entity.drug.influence.DrugInfluence;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DrugEntityEffect(List<DrugInfluence> drugs) implements EntityEffect {
    public static final MapCodec<DrugEntityEffect> CODEC = DrugInfluence.CODEC.listOf().fieldOf("drugs").xmap(DrugEntityEffect::new, DrugEntityEffect::drugs);
    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        for (DrugInfluence effect : drugs) {
            float prevStrength = (float) (prevRatio * strength * effect.target());
            float newStrength = (float) (newRatio * strength * effect.target());
            float drugStrength = newStrength - prevStrength;

            if (drugStrength > 0)
                PsychedelicraftHooks.addDrugValue(entity, effect, drugStrength);
        }
    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

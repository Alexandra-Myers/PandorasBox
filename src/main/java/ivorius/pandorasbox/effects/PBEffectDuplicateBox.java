package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effects.spawn_entities.SpawnEntityIDListEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.EntityInit;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 03.12.14.
 */
public class PBEffectDuplicateBox extends PBEffectNormal {
    public static final int MODE_BOX_IN_BOX = 0;
    public static final MapCodec<PBEffectDuplicateBox> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("spawn_mode").forGetter(PBEffectDuplicateBox::getSpawnMode))
                    .apply(instance, PBEffectDuplicateBox::new));

    public int spawnMode;

    public static int timeNeededForSpawnMode(int mode) {
        if (mode == MODE_BOX_IN_BOX) {
            return 60;
        }

        return 0;
    }

    public PBEffectDuplicateBox(int spawnMode) {
        super(timeNeededForSpawnMode(spawnMode));
        this.spawnMode = spawnMode;
    }

    public int getSpawnMode() {
        return spawnMode;
    }

    @Override
    public void setUpEffect(Level level, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random) {
        if (!level.isClientSide()) {
            PBEffect effect = PBECRegistry.createRandomEffect(level, random, box.getX(), box.getY(), box.getZ(), true);
            PandorasBoxEntity newBox = new PandorasBoxEntity(EntityInit.BOX, level, true, true);

            newBox.setBoxEffect(effect);
            newBox.setBoxWaitingTime(40);
            newBox.setRenderItem(box.getRenderItem());
            SpawnEntityIDListEffect.moveTo(newBox, box.position(), box.getYRot(), box.getXRot());

            if (spawnMode == MODE_BOX_IN_BOX) {
                newBox.beginFloating();
                newBox.beginScalingIn();
                level.addFreshEntity(newBox);
            }
        }
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {

    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}

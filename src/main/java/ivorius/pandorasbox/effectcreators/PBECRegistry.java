/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.utils.StringConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECRegistry
{
    public static final int MAX_DELAY_IN_MULTIEFFECT = 60;
    private static final List<EffectHolder> fixedChanceCreators = new ArrayList<>();
    private static final List<EffectHolder> goodCreators = new ArrayList<>();
    private static final List<EffectHolder> badCreators = new ArrayList<>();

    public static void register(PBEffectCreator creator, String id) {
        PandorasBox.logger.info("Effect Name: " + id);
        EffectHolder holder = Registry.EFFECT_HOLDER_REGISTRY.get(new ResourceLocation(id));
        holder.defineEffectCreator(creator);
        if (holder.fixedChance() != -1)
            fixedChanceCreators.add(holder);
        else if (holder.isGood())
            goodCreators.add(holder);
        else
            badCreators.add(holder);

    }

    public static PBEffectCreator randomEffectCreatorOfType(RandomSource random, boolean good) {
        List<EffectHolder> list = good ? goodCreators : badCreators;
        return list.get(random.nextInt(list.size())).effectCreator;
    }

    public static PBEffect createEffect(Level world, RandomSource random, double x, double y, double z, PBEffectCreator creator) {
        if (!isAnyNull(world, random, creator))
            return constructEffectSafe(creator, world, x, y, z, random);

        return null;
    }

    public static PBEffect createRandomEffect(Level world, RandomSource random, double x, double y, double z, boolean multi) {
        float currentMinChance = 1.0f;
        ArrayList<PBEffect> effects = new ArrayList<>();
        boolean bl = world.getDifficulty().equals(Difficulty.PEACEFUL);

        do {
            PBEffectCreator creator = null;

            for (EffectHolder fixedChanceCreator : fixedChanceCreators) {
                if (random.nextDouble() < fixedChanceCreator.fixedChance()) {
                    if (fixedChanceCreator.canBeGoodOrBad() && !fixedChanceCreator.isGood() && bl)
                        continue;
                    creator = fixedChanceCreator.effectCreator;
                    break;
                }
            }

            if (creator == null)
                creator = randomEffectCreatorOfType(random, random.nextFloat() < PandorasBox.CONFIG.goodEffectChance.get() || bl);

            PBEffect effect = constructEffectSafe(creator, world, x, y, z, random);

            if (effect != null)
                effects.add(effect);

            currentMinChance = Math.min(currentMinChance, creator.chanceForMoreEffects(world, x, y, z, random));
        } while (effects.isEmpty() || random.nextFloat() < newEffectChance(currentMinChance) && effects.size() < PandorasBox.CONFIG.maxEffectsPerBox.get() && multi);

        if (effects.size() == 1)
            return effects.get(0);
        else {
            PBEffect[] effectArray = effects.toArray(new PBEffect[effects.size()]);
            int[] delays = new int[effectArray.length];

            for (int i = 1; i < delays.length; i++)
                delays[i] = random.nextInt(MAX_DELAY_IN_MULTIEFFECT);

            return new PBEffectMulti(effectArray, delays);
        }
    }

    private static double newEffectChance(double effectFactor) {
        double intensity = PandorasBox.CONFIG.boxIntensity.get();
        return intensity == 0 ? 0 : Math.pow(effectFactor, 1.0 / intensity);
    }

    public static PBEffect constructEffectSafe(PBEffectCreator creator, Level world, double x, double y, double z, RandomSource random) {
        return creator.constructEffect(world, x, y, z, random);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, boolean multi, Entity entity, BlockPos pos, boolean floatAway) {
        PBEffect effect = createRandomEffect(world, random, pos.getX(), pos.getY() + 1.2, pos.getZ(), multi);
        return spawnPandorasBox(world, effect, entity, pos, floatAway);
    }
    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, boolean multi, Entity entity) {
        PBEffect effect = createRandomEffect(world, random, entity.getX(), entity.getY() + 1.2, entity.getZ(), multi);
        return spawnPandorasBox(world, effect, entity, null, true);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, PBEffectCreator creator, Entity entity) {
        PBEffect effect = createEffect(world, random, entity.getX(), entity.getY() + 1.2, entity.getZ(), creator);
        return spawnPandorasBox(world, effect, entity, null, true);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, PBEffect effect, Entity entity, BlockPos pos, boolean floatAway) {
        if (effect != null && !world.isClientSide()) {
            PandorasBoxEntity pandorasBox = Registry.Box.get().create(world);

            if(pos == null) {
                pos = new BlockPos(
                        (int) (entity.getX() + entity.getDirection().getStepX()),
                        (int) (entity.getY() + entity.getDirection().getStepY()),
                        (int) (entity.getZ() + entity.getDirection().getStepZ()));
            }
            while(!world.getBlockState(pos).isAir()) {
                pos = pos.above();
            }

            assert pandorasBox != null;

            pandorasBox.setBoxEffect(effect);
            pandorasBox.setTimeBoxWaiting(40);
            pandorasBox.moveTo(pos, entity.getYRot() + 180.0f, 0.0f);

            if (floatAway)
                pandorasBox.beginFloatingAway();
            else
                pandorasBox.beginFloatingUp();

            if (entity instanceof Player player) pandorasBox.setBoxOwner(player);

            world.addFreshEntity(pandorasBox);

            return pandorasBox;
        }

        return null;
    }
    public static boolean isAnyNull(Object... objects) {
        for(Object object : objects) {
            if(object == null) return true;
        }
        return false;
    }
}

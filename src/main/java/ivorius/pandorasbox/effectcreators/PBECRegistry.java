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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.*;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECRegistry {
    public static final int MAX_DELAY_IN_MULTIEFFECT = 60;
    private static final List<EffectHolder> fixedChanceCreators = new ArrayList<>();
    private static final List<EffectHolder> goodCreators = new ArrayList<>();
    private static final List<EffectHolder> badCreators = new ArrayList<>();

    public static void register(PBEffectCreator creator, String id) {
        PandorasBox.logger.info("Effect Name: " + id);
        EffectHolder holder = Registry.EFFECT_HOLDER_REGISTRY.get().getValue(new ResourceLocation(id));
        holder.defineEffectCreator(creator);
        if (holder.fixedChance() != -1)
            fixedChanceCreators.add(holder);
        else if (holder.isGood())
            goodCreators.add(holder);
        else
            badCreators.add(holder);

    }

    public static PBEffectCreator randomEffectCreatorOfType(Random random, boolean good) {
        List<EffectHolder> list = good ? goodCreators : badCreators;
        return list.get(random.nextInt(list.size())).effectCreator;
    }

    public static PBEffect createEffect(World world, Random random, double x, double y, double z, PBEffectCreator creator) {
        if (!isAnyNull(world, random, creator))
            return constructEffectSafe(creator, world, x, y, z, random);

        return null;
    }

    public static PBEffect createRandomEffect(World world, Random random, double x, double y, double z, boolean multi) {
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

    public static PBEffect constructEffectSafe(PBEffectCreator creator, World world, double x, double y, double z, Random random) {
        return creator.constructEffect(world, x, y, z, random);
    }

    public static PandorasBoxEntity spawnPandorasBox(World world, Random random, boolean multi, PlayerEntity player, BlockPos pos, boolean floatAway) {
        PBEffect effect = createRandomEffect(world, random, pos.getX(), pos.getY() + 1.2, pos.getZ(), multi);
        return spawnPandorasBox(world, effect, player, pos, floatAway);
    }
    public static PandorasBoxEntity spawnPandorasBox(World world, Random random, boolean multi, PlayerEntity player) {
        PBEffect effect = createRandomEffect(world, random, player.getX(), player.getY() + 1.2, player.getZ(), multi);
        return spawnPandorasBox(world, effect, player, null, true);
    }

    public static PandorasBoxEntity spawnPandorasBox(World world, Random random, PBEffectCreator creator, PlayerEntity player) {
        PBEffect effect = createEffect(world, random, player.getX(), player.getY() + 1.2, player.getZ(), creator);
        return spawnPandorasBox(world, effect, player, null, true);
    }

    public static PandorasBoxEntity spawnPandorasBox(World world, PBEffect effect, PlayerEntity player, BlockPos pos, boolean floatAway) {
        if (effect != null && !world.isClientSide()) {
            PandorasBoxEntity pandorasBox = Registry.Box.get().create(world);

            if (pos == null)
                pos = player.blockPosition().offset(player.getDirection().getStepX(), 0, player.getDirection().getStepZ());

            assert pandorasBox != null;

            pandorasBox.setBoxEffect(effect);
            pandorasBox.setTimeBoxWaiting(40);
            pandorasBox.moveTo(pos, player.yRot + 180.0f, 0.0f);

            if (floatAway)
                pandorasBox.beginFloatingAway();
            else
                pandorasBox.beginFloatingUp();

            pandorasBox.setBoxOwner(player);

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
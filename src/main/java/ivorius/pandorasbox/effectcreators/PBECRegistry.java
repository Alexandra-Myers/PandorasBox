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
import ivorius.pandorasbox.init.EntityInit;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
        EffectHolder holder = Init.EFFECT_HOLDER_REGISTRY.get(ResourceLocation.withDefaultNamespace(id));
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

        do {
            PBEffectCreator creator = null;
            boolean bl = world.getDifficulty().equals(Difficulty.PEACEFUL);

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
            return effects.getFirst();
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

    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, boolean multi, Player player, BlockPos pos, boolean floatAway) {
        PBEffect effect = createRandomEffect(world, random, pos.getX(), pos.getY() + 1.2, pos.getZ(), multi);
        return spawnPandorasBox(world, effect, player, pos, floatAway, true);
    }
    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, boolean multi, Player player) {
        PBEffect effect = createRandomEffect(world, random, player.getX(), player.getY() + 1.2, player.getZ(), multi);
        return spawnPandorasBox(world, effect, player, null, true, true);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, PBEffectCreator creator, Player player) {
        PBEffect effect = createEffect(world, random, player.getX(), player.getY() + 1.2, player.getZ(), creator);
        return spawnPandorasBox(world, effect, player, null, true, false);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, PBEffect effect, Player player, BlockPos pos, boolean floatAway, boolean canGenerateMoreEffectsAfterwards) {
        if (effect != null && !world.isClientSide()) {
            PandorasBoxEntity pandorasBox = new PandorasBoxEntity(EntityInit.BOX, world, canGenerateMoreEffectsAfterwards, !floatAway);

            if(pos == null) {
                pos = BlockPos.containing(
                        player.getX() + player.getDirection().getStepX(),
                        player.getY(),
                        player.getZ() + player.getDirection().getStepZ());
            }
            while (world.getBlockState(pos).blocksMotion()) {
                if (!world.getBlockState(pos.below()).blocksMotion()) {
                    pos = pos.below();
                    continue;
                }
                pos = pos.above();
            }

            pandorasBox.setBoxEffect(effect);
            pandorasBox.setBoxWaitingTime(40);
            pandorasBox.moveTo(pos, player.getYRot() + 180.0f, 0.0f);

            pandorasBox.beginFloating();

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

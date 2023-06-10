/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ivorius.pandorasbox.PBConfig;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenLavaCages;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.effects.PBEffectRegistry;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.StringConverter;
import ivorius.pandorasbox.utils.WrappedBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECRegistry
{
    public static final int MAX_DELAY_IN_MULTIEFFECT = 60;

    private static final ArrayList<String> goodCreators = new ArrayList<>();
    private static final ArrayList<String> badCreators = new ArrayList<>();

    private static final Map<String, Float> fixedChanceCreators = new HashMap<>();

    private static final WrappedBiMap<ResourceLocation, PBEffectCreator> effectCreators = new WrappedBiMap<>(HashBiMap.create());

    public static void register(PBEffectCreator creator, String id, boolean good)
    {
        id = StringConverter.convertCamelCase(id);
        (good ? goodCreators : badCreators).add(id);

        PandorasBox.logger.info("Effect Name: " + id);
        effectCreators.put(new ResourceLocation(PandorasBox.MOD_ID, id), creator);
    }

    public static void register(PBEffectCreator creator, String id, float fixedChance)
    {
        id = StringConverter.convertCamelCase(id);
        fixedChanceCreators.put(id, fixedChance);
        effectCreators.put(new ResourceLocation(PandorasBox.MOD_ID, id), creator);
    }

    public static String getID(PBEffectCreator creator)
    {
        return effectCreators.inverse().get(creator).toString();
    }

    public static PBEffectCreator effectCreatorWithName(String name)
    {
        name = StringConverter.convertCamelCase(name);
        return effectCreators.get(new ResourceLocation(name));
    }
    public static BiMap<ResourceLocation, PBEffectCreator> getEffectCreators() {
        return (BiMap<ResourceLocation, PBEffectCreator>) effectCreators.map();
    }

    public static boolean hasEffect(String name)
    {
        name = StringConverter.convertCamelCase(name);
        return effectCreators.containsKey(new ResourceLocation(name));
    }

    public static boolean isEffectGood(String name)
    {
        return goodCreators.contains(name);
    }

    public static boolean isEffectBad(String name)
    {
        return badCreators.contains(name);
    }

    public static PBEffectCreator randomEffectCreatorOfType(RandomSource random, boolean good)
    {
        ArrayList<String> list = good ? goodCreators : badCreators;
        return effectCreators.get(new ResourceLocation(PandorasBox.MOD_ID, list.get(random.nextInt(list.size()))));
    }

    public static PBEffect createEffect(Level world, RandomSource random, double x, double y, double z, PBEffectCreator creator)
    {
        if (!isAnyNull(world, random, creator))
            return constructEffectSafe(creator, world, x, y, z, random);

        return null;
    }

    public static PBEffect createRandomEffectOfType(Level world, RandomSource random, double x, double y, double z, boolean good)
    {
        PBEffectCreator creator = randomEffectCreatorOfType(random, good);

        if (creator != null)
            return constructEffectSafe(creator, world, x, y, z, random);

        return null;
    }

    public static PBEffect createRandomEffect(Level world, RandomSource random, double x, double y, double z, boolean multi)
    {
        float currentMinChance = 1.0f;
        ArrayList<PBEffect> effects = new ArrayList<>();

        do
        {
            PBEffectCreator creator = null;

            for (String fixedChanceCreator : fixedChanceCreators.keySet())
            {
                if (random.nextFloat() < fixedChanceCreators.get(fixedChanceCreator))
                {
                    creator = effectCreators.get(new ResourceLocation(PandorasBox.MOD_ID, fixedChanceCreator));
                    break;
                }
            }

            if (creator == null)
                creator = randomEffectCreatorOfType(random, random.nextFloat() < PBConfig.goodEffectChance);

//            if(isAnyNull(creator, world, random)) return null;
            PBEffect effect = constructEffectSafe(creator, world, x, y, z, random);

            if (effect != null)
                effects.add(effect);

            currentMinChance = Math.min(currentMinChance, creator.chanceForMoreEffects(world, x, y, z, random));
        }
        while (random.nextFloat() < newEffectChance(currentMinChance) && effects.size() < PBConfig.maxEffectsPerBox && multi);

        if (effects.size() == 1)
        {
            return effects.get(0);
        }
        else
        {
            PBEffect[] effectArray = effects.toArray(new PBEffect[effects.size()]);
            int[] delays = new int[effectArray.length];

            for (int i = 1; i < delays.length; i++)
                delays[i] = random.nextInt(MAX_DELAY_IN_MULTIEFFECT);

            return new PBEffectMulti(effectArray, delays);
        }
    }

    private static double newEffectChance(double effectFactor)
    {
        return Math.pow(effectFactor, 1.0 / PBConfig.boxIntensity);
    }

    public static PBEffect constructEffectSafe(PBEffectCreator creator, Level world, double x, double y, double z, RandomSource random)
    {
//        if(isAnyNull(creator, world, random)) return null;
        return creator.constructEffect(world, x, y, z, random);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, boolean multi, Entity entity, BlockPos pos, boolean floatAway)
    {
        PBEffect effect = createRandomEffect(world, random, pos.getX(), pos.getY() + 1.2, pos.getZ(), multi);
        return spawnPandorasBox(world, effect, entity, pos, floatAway);
    }
    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, boolean multi, Entity entity)
    {
        PBEffect effect = createRandomEffect(world, random, entity.getX(), entity.getY() + 1.2, entity.getZ(), multi);
        return spawnPandorasBox(world, effect, entity, null, true);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, PBEffectCreator creator, Entity entity)
    {
        PBEffect effect = createEffect(world, random, entity.getX(), entity.getY() + 1.2, entity.getZ(), creator);
        return spawnPandorasBox(world, effect, entity, null, true);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, PBEffect effect, Entity entity, BlockPos pos, boolean floatAway)
    {
        if (effect != null && !world.isClientSide())
        {
            PandorasBoxEntity entityPandorasBox = Registry.Box.get().create(world);

            if(pos == null) {
                pos = new BlockPos(
                        (int) (entity.getX() + entity.getDirection().getStepX()),
                        (int) (entity.getY() + entity.getDirection().getStepY()),
                        (int) (entity.getZ() + entity.getDirection().getStepZ()));
            }
            while(!world.getBlockState(pos).isAir()) {
                pos = pos.above();
            }

            assert entityPandorasBox != null;

            entityPandorasBox.setBoxEffect(effect);
            entityPandorasBox.setTimeBoxWaiting(40);
            entityPandorasBox.moveTo(pos, entity.getYRot() + 180.0f, 0.0f);

            if (floatAway) {
                entityPandorasBox.beginFloatingAway();
            } else {
                entityPandorasBox.beginFloatingUp();
            }

            world.addFreshEntity(entityPandorasBox);

            return entityPandorasBox;
        }

        return null;
    }

    public static Set<String> getAllIDs()
    {
        Set<String> set = new HashSet<>();
        effectCreators.keySet().forEach((location) -> set.add(location.toString()));
        return set;
    }
    public static ArrayListExtensions<ResourceLocation> getAllIDsAsRL()
    {
        Set<ResourceLocation> set = effectCreators.keySet();
        ArrayListExtensions<ResourceLocation> resourceLocationList = new ArrayListExtensions<>();
        resourceLocationList.addAll(set);
        return resourceLocationList;
    }
    public static boolean isAnyNull(Object... objects) {
        for(Object object : objects) {
            if(object == null) return true;
        }
        return false;
    }

    public static String[] getIDArray()
    {
        Set<String> allIDs = getAllIDs();
        return allIDs.toArray(new String[allIDs.size()]);
    }
}

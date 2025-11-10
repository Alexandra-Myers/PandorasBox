/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.effects.spawn_entities.SpawnEntityIDListEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.EntityInit;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECRegistry {
    public static final int MAX_DELAY_IN_MULTIEFFECT = 60;

    public static PBEffectCreator randomEffectCreatorOfType(RandomSource random, List<EffectHolder> holders) {
        return holders.get(random.nextInt(holders.size())).effectCreator;
    }

    public static PBEffect createEffect(Level world, RandomSource random, double x, double y, double z, PBEffectCreator creator) {
        if (!isAnyNull(world, random, creator))
            return constructEffectSafe(creator, world, x, y, z, random);

        return null;
    }

    public static PBEffect createRandomEffect(Level world, RandomSource random, double x, double y, double z, boolean multi, Optional<HolderSet<EffectHolder>> selection, ResourceKey<? extends Registry<EffectHolder>> registryKey) {
        List<PBEffect> effects = createRandomEffects(world, random, x, y, z, multi, selection, registryKey);

        if (effects.size() == 1)
            return effects.getFirst();
        else {
            PBEffect[] effectArray = effects.toArray(new PBEffect[0]);
            int[] delays = new int[effectArray.length];

            for (int i = 1; i < delays.length; i++)
                delays[i] = random.nextInt(MAX_DELAY_IN_MULTIEFFECT);

            return new PBEffectMulti(effectArray, delays);
        }
    }

    public static List<PBEffect> createRandomEffects(Level world, RandomSource random, double x, double y, double z, boolean multi, Optional<HolderSet<EffectHolder>> selection, ResourceKey<? extends Registry<EffectHolder>> registryKey) {
        HolderSet<EffectHolder> holders = selection.filter(set -> set.size() > 0).orElseGet(() -> HolderSet.direct(world.registryAccess().lookupOrThrow(registryKey).listElements().toList()));
        boolean isPeaceful = world.getDifficulty().equals(Difficulty.PEACEFUL);
        List<EffectHolder> fixedChanceHolders = holders.stream().map(Holder::value).filter(effectHolder -> effectHolder.fixedChance() != -1).toList();
        List<EffectHolder> positiveEffects = holders.stream().map(Holder::value).filter(effectHolder -> !fixedChanceHolders.contains(effectHolder) && effectHolder.isGood()).toList();
        List<EffectHolder> negativeEffects = holders.stream().map(Holder::value).filter(effectHolder -> !fixedChanceHolders.contains(effectHolder) && !effectHolder.isGood()).toList();
        float currentMinChance = 1.0f;
        ArrayList<PBEffect> effects = new ArrayList<>();

        do {
            PBEffectCreator creator = null;

            for (EffectHolder fixedChanceCreator : fixedChanceHolders) {
                if (random.nextDouble() < fixedChanceCreator.fixedChance()) {
                    if (fixedChanceCreator.canBeGoodOrBad() && !fixedChanceCreator.isGood() && isPeaceful)
                        continue;
                    creator = fixedChanceCreator.effectCreator;
                    break;
                }
            }

            if (creator == null)
                creator = randomEffectCreatorOfType(random, isPeaceful || random.nextFloat() < PandorasBox.CONFIG.goodEffectChance.get() ? positiveEffects : negativeEffects);

            PBEffect effect = constructEffectSafe(creator, world, x, y, z, random);

            if (effect != null)
                effects.add(effect);

            currentMinChance = Math.min(currentMinChance, creator.chanceForMoreEffects(world, x, y, z, random));
        } while (effects.isEmpty() || multi && random.nextFloat() < newEffectChance(currentMinChance) && effects.size() < PandorasBox.CONFIG.maxEffectsPerBox.get());

        return effects;
    }

    private static double newEffectChance(double effectFactor) {
        double intensity = PandorasBox.CONFIG.boxIntensity.get();
        return intensity == 0 ? 0 : Math.pow(effectFactor, 1.0 / intensity);
    }

    public static PBEffect constructEffectSafe(PBEffectCreator creator, Level world, double x, double y, double z, RandomSource random) {
        return creator.constructEffect(world, x, y, z, random);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, ItemStack heldItem, Optional<ItemStack> renderItem, boolean multi, Player player, BlockPos pos, boolean floatAway, HolderSet<EffectHolder> holders) {
        PBEffect effect = createRandomEffect(world, random, pos.getX(), pos.getY() + 1.2, pos.getZ(), multi, Optional.of(holders), Init.EFFECT_HOLDER_REGISTRY_KEY);
        return spawnPandorasBox(world, effect, heldItem, renderItem, player, pos, floatAway, true);
    }
    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, Optional<ItemStack> renderItem, boolean multi, Player player) {
        PBEffect effect = createRandomEffect(world, random, player.getX(), player.getY() + 1.2, player.getZ(), multi, Optional.empty(), Init.EFFECT_HOLDER_REGISTRY_KEY);
        return spawnPandorasBox(world, effect, ItemStack.EMPTY, renderItem, player, null, true, true);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, RandomSource random, Optional<ItemStack> renderItem, PBEffectCreator creator, Player player) {
        PBEffect effect = createEffect(world, random, player.getX(), player.getY() + 1.2, player.getZ(), creator);
        return spawnPandorasBox(world, effect, ItemStack.EMPTY, renderItem, player, null, true, false);
    }

    public static PandorasBoxEntity spawnPandorasBox(Level world, PBEffect effect, ItemStack heldItem, Optional<ItemStack> renderItem, Player player, BlockPos pos, boolean floatAway, boolean canGenerateMoreEffectsAfterwards) {
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
            SpawnEntityIDListEffect.moveTo(pandorasBox, new Vec3(pos), player.getYRot() + 180.0f, 0.0f);
            if (renderItem.isPresent()) {
                ItemStack chosen = heldItem.copyWithCount(1);
                if (!renderItem.get().isEmpty()) chosen = renderItem.get();
                pandorasBox.setRenderItem(chosen);
            }

            pandorasBox.beginFloating();

            pandorasBox.setOwner(player);

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

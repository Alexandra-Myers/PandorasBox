/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.mods.PsychedelicraftHooks;
import ivorius.psychedelicraft.entity.drug.influence.DrugInfluence;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesDrug extends PBEffectEntityBased {
    public final List<DrugInfluence> drugs = new ArrayList<>();

    public PBEffectEntitiesDrug() {
    }

    public PBEffectEntitiesDrug(int maxTicksAlive, double range, Collection<DrugInfluence> drugs) {
        super(maxTicksAlive, range);
        this.drugs.addAll(drugs);
    }

    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        for (DrugInfluence effect : drugs) {
            float prevStrength = (float) (prevRatio * strength * effect.getMaxInfluence());
            float newStrength = (float) (newRatio * strength * effect.getMaxInfluence());
            float drugStrength = newStrength - prevStrength;

            if (drugStrength > 0)
                PsychedelicraftHooks.addDrugValue(entity, effect, drugStrength);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        ListTag drugList = new ListTag();
        for (DrugInfluence drug : drugs) {
            Tag drugCmp = new CompoundTag();
            drugCmp = DrugInfluence.CODEC.encode(drug, RegistryOps.create(NbtOps.INSTANCE, registryAccess), drugCmp).getOrThrow();
            drugList.add(drugCmp);
        }
        compound.put("drugs", drugList);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        drugs.clear();
        ListTag drugList = compound.getList("drugs", 10);
        for (int i = 0; i < drugList.size(); i++) {
            CompoundTag drugCmp = drugList.getCompound(i);
            drugs.add(DrugInfluence.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), drugCmp).getOrThrow());
        }
    }
}
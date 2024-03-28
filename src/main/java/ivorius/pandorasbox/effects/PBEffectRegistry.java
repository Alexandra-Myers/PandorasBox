/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.init.Registry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectRegistry {
    public static Map<Class<? extends PBEffect>, String> effectToStringID = new HashMap<>();
    public static RegisteredPBEffect getEffect(String id) {
        return Registry.BOX_EFFECT_REGISTRY.get().getValue(new ResourceLocation(id));
    }

    public static void writeEffect(PBEffect effect, CompoundNBT compound) {
        if (effect != null) {
            compound.putString("pbEffectID", effect.getEffectID());
            CompoundNBT pbEffectCompound = new CompoundNBT();
            effect.writeToNBT(pbEffectCompound);
            compound.put("pbEffectCompound", pbEffectCompound);
        }
    }

    public static PBEffect loadEffect(CompoundNBT compound) {
        return loadEffect(compound.getString("pbEffectID"), compound.getCompound("pbEffectCompound"));
    }

    public static PBEffect loadEffect(String id, CompoundNBT compound) {
        RegisteredPBEffect registeredPBEffect = getEffect(id);

        PBEffect effect = null;

        if (registeredPBEffect != null) {
            try {
                Class<? extends PBEffect> clazz = registeredPBEffect.clazz;
                effect = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (effect != null && compound != null) {
            effect.readFromNBT(compound);
            return effect;
        } else {
            System.err.println("Pandoras Box: Could not load effect with id '" + id + "'!");
        }

        return null;
    }

    public static String getEffectID(PBEffect effect) {
        return effectToStringID.get(effect.getClass());
    }
}
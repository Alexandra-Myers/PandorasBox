/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

/**
 * Created by lukas on 31.03.14.
 */
public class PBEffectMulti extends PBEffect
{
    public PBEffect[] effects;
    public int[] delays;
    public PBEffectMulti() {

    }

    public PBEffectMulti(PBEffect[] effects, int[] delays)
    {
        this.effects = effects;
        this.delays = delays;
    }

    @Override
    public void doTick(PandorasBoxEntity entity, Vec3d effectCenter, int ticksAlive)
    {
        for (int i = 0; i < effects.length; i++)
        {
            int effectTicks = ticksAlive - delays[i];
            effects[i].doTick(entity, effectCenter, effectTicks);
        }
    }

    @Override
    public boolean isDone(PandorasBoxEntity entity, int ticksAlive)
    {
        for (int i = 0; i < effects.length; i++)
        {
            int effectTicks = ticksAlive - delays[i];
            if (!effects[i].isDone(entity, effectTicks))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        ListNBT list = new ListNBT();

        for (int i = 0; i < effects.length; i++)
        {
            CompoundNBT cmp = new CompoundNBT();

            cmp.putInt("delay", delays[i]);

            cmp.putString("pbEffectID", effects[i].getEffectID());
            CompoundNBT effectCmp = new CompoundNBT();
            effects[i].writeToNBT(effectCmp);
            cmp.put("pbEffectCompound", effectCmp);

            list.add(cmp);
        }

        compound.put("effects", list);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        ListNBT list = compound.getList("effects", Constants.NBT.TAG_COMPOUND);

        effects = new PBEffect[list.size()];
        delays = new int[effects.length];

        for (int i = 0; i < effects.length; i++)
        {
            CompoundNBT cmp = list.getCompound(i);

            delays[i] = cmp.getInt("delay");
            effects[i] = PBEffectRegistry.loadEffect(cmp.getString("pbEffectID"), cmp.getCompound("pbEffectCompound"));
        }
    }

    @Override
    public boolean canGenerateMoreEffectsAfterwards(PandorasBoxEntity entity)
    {
        for (PBEffect effect : effects)
        {
            if (!effect.canGenerateMoreEffectsAfterwards(entity))
            {
                return false;
            }
        }

        return true;
    }
}

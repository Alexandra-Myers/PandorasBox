/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectSpawnItemStacks extends PBEffectSpawnEntities {
    public ItemStack[] stacks;
    public PBEffectSpawnItemStacks() {}

    public PBEffectSpawnItemStacks(int time, ItemStack[] stacks) {
        super(time, stacks.length);

        setDoesSpawnFromBox(0.1, 0.4, 0.2, 1.0);
        this.stacks = stacks;
    }

    public PBEffectSpawnItemStacks(int time, double range, double shiftY, ItemStack[] stacks) {
        super(time, stacks.length);

        setDoesNotSpawnFromBox(range, shiftY);
        this.stacks = stacks;
    }

    @Override
    public Entity spawnEntity(World world, PandorasBoxEntity entity, Random random, int number, double x, double y, double z) {
        if(world.isClientSide()) return null;
        ItemEntity entityItem = new ItemEntity(world, x, y, z, stacks[number]);
        entityItem.setPickUpDelay(10);
        world.addFreshEntity(entityItem);
        return entityItem;
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTStacks("stacks", stacks, compound);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        stacks = PBNBTHelper.readNBTStacks("stacks", compound);
    }
}

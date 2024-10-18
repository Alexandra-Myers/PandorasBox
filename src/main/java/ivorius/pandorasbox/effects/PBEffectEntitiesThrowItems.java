/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesThrowItems extends PBEffectEntityBased {
    public double chancePerItem;
    public double itemDeletionChance;
    public PBEffectEntitiesThrowItems() {}

    public ItemStack[] smuggledInItems;

    public PBEffectEntitiesThrowItems(int maxTicksAlive, double range, double chancePerItem, double itemDeletionChance, ItemStack[] smuggledInItems) {
        super(maxTicksAlive, range);
        this.chancePerItem = chancePerItem;
        this.itemDeletionChance = itemDeletionChance;
        this.smuggledInItems = smuggledInItems;
    }

    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        if (entity instanceof Player player) {
            Random itemRandom = new Random(entity.getId());
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                double expectedThrow = itemRandom.nextDouble();
                if (newRatio >= expectedThrow && prevRatio < expectedThrow) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (random.nextDouble() < chancePerItem) {
                        if (random.nextDouble() >= itemDeletionChance) {
                            player.drop(stack, false);
                        }

                        player.getInventory().setItem(i, ItemStack.EMPTY);
                    }
                }
            }

            for (ItemStack smuggledInItem : smuggledInItems) {
                double expectedThrow = itemRandom.nextDouble();
                if (newRatio >= expectedThrow && prevRatio < expectedThrow) {
                    player.drop(smuggledInItem, false);
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putDouble("chancePerItem", chancePerItem);
        compound.putDouble("itemDeletionChance", itemDeletionChance);
        PBNBTHelper.writeNBTStacks("smuggledInItems", smuggledInItems, compound, registryAccess);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        chancePerItem = compound.getDouble("chancePerItem");
        itemDeletionChance = compound.getDouble("itemDeletionChance");
        smuggledInItems = PBNBTHelper.readNBTStacks("smuggledInItems", compound, registryAccess);
    }
}

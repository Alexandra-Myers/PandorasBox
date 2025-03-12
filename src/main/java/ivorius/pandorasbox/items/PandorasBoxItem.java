/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.items;

import ivorius.pandorasbox.component.PBEffectComponent;
import ivorius.pandorasbox.init.ComponentInit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class PandorasBoxItem extends BlockItem {
    public PandorasBoxItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        itemstack.getOrDefault(ComponentInit.EFFECT_COMPONENT, PBEffectComponent.DEFAULT).createEffect(world, player, player.blockPosition(), true);
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        return InteractionResultHolder.success(itemstack);
    }
    public @NotNull InteractionResult useOn(@NotNull UseOnContext p_40581_) {
        return this.place(new BlockPlaceContext(p_40581_));
    }
}

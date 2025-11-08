package ivorius.pandorasbox.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;

public record PBEffectComponent(HolderSet<EffectHolder> holders, HolderSet<EffectHolder> randomSelection, Optional<ItemStack> renderItem, boolean selectsRandom) implements TooltipProvider {
    public static final PBEffectComponent DEFAULT = new PBEffectComponent(HolderSet.empty(), HolderSet.empty(), Optional.empty(), true);
    public static final Codec<PBEffectComponent> FULL_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(EffectHolder.CODEC.optionalFieldOf("effect_holders", HolderSet.empty()).forGetter(PBEffectComponent::holders),
                            EffectHolder.CODEC.optionalFieldOf("random_selection", HolderSet.empty()).forGetter(PBEffectComponent::randomSelection),
                            ItemStack.OPTIONAL_CODEC.optionalFieldOf("render_item").forGetter(PBEffectComponent::renderItem),
                            Codec.BOOL.optionalFieldOf("selects_random", false).forGetter(PBEffectComponent::selectsRandom))
                    .apply(instance, PBEffectComponent::new));
    public static final Codec<PBEffectComponent> CODEC = Codec.withAlternative(FULL_CODEC, EffectHolder.CODEC.orElse(HolderSet.empty()).xmap(holders -> new PBEffectComponent(holders, HolderSet.empty(), Optional.empty(), holders.size() == 0), PBEffectComponent::holders));
    public PBEffectComponent(HolderSet<EffectHolder> holders, HolderSet<EffectHolder> randomSelection, Optional<ItemStack> renderItem, boolean selectsRandom) {
        this.holders = holders;
        this.randomSelection = randomSelection;
        this.renderItem = renderItem;
        this.selectsRandom = selectsRandom || holders.size() == 0 || randomSelection.size() > 0;
    }

    public PandorasBoxEntity createEffect(Level level, Player player, BlockPos pos, boolean floatAway, ItemStack heldStack) {
        if (level.isClientSide()) return null;
        else if (holders.size() == 0)
            return PBECRegistry.spawnPandorasBox(level, level.random, heldStack, renderItem, true, player, pos, floatAway, randomSelection);
        else {
            List<PBEffect> pbEffects  = new ArrayList<>();
            for (Holder<EffectHolder> holder : holders) {
                pbEffects.add(holder.value().effectCreator.constructEffect(level, pos.getX(), pos.getY() + 1.2, pos.getZ(), level.random));
            }
            if (selectsRandom) pbEffects.addAll(PBECRegistry.createRandomEffects(level, level.random, pos.getX(), pos.getY() + 1.2, pos.getZ(), true, Optional.of(randomSelection), Init.EFFECT_HOLDER_REGISTRY_KEY));
            return PBECRegistry.spawnPandorasBox(level, new PBEffectMulti(pbEffects.toArray(new PBEffect[0]), new int[pbEffects.size()]), heldStack, renderItem, player, pos, floatAway, false);
        }
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
        if (selectsRandom) {
            consumer.accept(Component.translatable("component.pandora_effects.random").withStyle(ChatFormatting.BLUE));
            consumer.accept(Component.empty());
        }
        if (randomSelection.size() > 0) {
            consumer.accept(Component.translatable("component.pandora_effects.random_selection").withStyle(ChatFormatting.BLUE));
            for (Holder<EffectHolder> holder : randomSelection) {
                consumer.accept(Component.literal(" - ").append(holder.value().component()).withStyle(ChatFormatting.GRAY));
            }
            consumer.accept(Component.empty());
        }
        if (holders.size() > 0) {
            consumer.accept(Component.translatable("component.pandora_effects.holders").withStyle(ChatFormatting.BLUE));
            for (Holder<EffectHolder> holder : holders) {
                consumer.accept(Component.literal(" - ").append(holder.value().component()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}

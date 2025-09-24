package ivorius.pandorasbox.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBECRegistry;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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

public record PBEffectComponent(List<Holder<EffectHolder>> holders, Optional<ItemStack> renderItem) implements TooltipProvider {
    public static final PBEffectComponent DEFAULT = new PBEffectComponent(Collections.emptyList(), Optional.empty());
    public static final Codec<PBEffectComponent> FULL_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(EffectHolder.CODEC.listOf().optionalFieldOf("effect_holders", Collections.emptyList()).forGetter(PBEffectComponent::holders),
                            ItemStack.OPTIONAL_CODEC.optionalFieldOf("render_item").forGetter(PBEffectComponent::renderItem))
                    .apply(instance, PBEffectComponent::new));
    public static final Codec<PBEffectComponent> CODEC = Codec.withAlternative(FULL_CODEC, EffectHolder.CODEC.listOf().orElse(Collections.emptyList()).xmap(holders -> new PBEffectComponent(holders, Optional.empty()), PBEffectComponent::holders));

    public PandorasBoxEntity createEffect(Level level, Player player, BlockPos pos, boolean floatAway, ItemStack heldStack) {
        if (level.isClientSide) return null;
        else if (holders.isEmpty()) {
            PandorasBoxEntity pandorasBoxEntity = PBECRegistry.spawnPandorasBox(level, level.random, true, player, pos, floatAway);
            if (renderItem.isPresent()) {
                ItemStack chosen = heldStack;
                if (!renderItem.get().isEmpty()) chosen = renderItem.get();
                pandorasBoxEntity.setRenderItem(chosen);
            }
            return pandorasBoxEntity;
        } else {
            List<PBEffect> pbEffects  = new ArrayList<>();
            for (Holder<EffectHolder> holder : holders) {
                pbEffects.add(holder.value().effectCreator.constructEffect(level, pos.getX(), pos.getY() + 1.2, pos.getZ(), level.random));
            }
            PandorasBoxEntity pandorasBoxEntity = PBECRegistry.spawnPandorasBox(level, new PBEffectMulti(pbEffects.toArray(new PBEffect[0]), new int[pbEffects.size()]), player, pos, floatAway, false);
            if (renderItem.isPresent()) {
                ItemStack chosen = heldStack;
                if (!renderItem.get().isEmpty()) chosen = renderItem.get();
                pandorasBoxEntity.setRenderItem(chosen);
            }
            return pandorasBoxEntity;
        }
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
        if (holders.isEmpty()) consumer.accept(Component.translatable("component.pandora_effects.random").withStyle(ChatFormatting.BLUE));
        else {
            consumer.accept(Component.translatable("component.pandora_effects.holders").withStyle(ChatFormatting.BLUE));
            for (Holder<EffectHolder> holder : holders) {
                consumer.accept(Component.literal(" - ").append(holder.value().component()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}

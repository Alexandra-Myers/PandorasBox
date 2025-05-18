package ivorius.pandorasbox.component;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;

public record PBEffectComponent(List<Holder<EffectHolder>> holders) implements TooltipProvider {
    public static final PBEffectComponent DEFAULT = new PBEffectComponent(Collections.emptyList());
    public static final Codec<PBEffectComponent> CODEC = Codec.withAlternative(EffectHolder.CODEC.listOf().orElse(Collections.emptyList()).xmap(PBEffectComponent::new, PBEffectComponent::holders), EffectHolder.CODEC.listOf().optionalFieldOf("effect_holders", Collections.emptyList()).xmap(PBEffectComponent::new, PBEffectComponent::holders).codec());

    public PandorasBoxEntity createEffect(Level level, Player player, BlockPos pos, boolean floatAway) {
        if (level.isClientSide) return null;
        else if (holders.isEmpty()) return PBECRegistry.spawnPandorasBox(level, level.random, true, player, pos, floatAway);
        else {
            List<PBEffect> pbEffects  = new ArrayList<>();
            for (Holder<EffectHolder> holder : holders) {
                pbEffects.add(holder.value().effectCreator.constructEffect(level, pos.getX(), pos.getY() + 1.2, pos.getZ(), level.random));
            }
            return PBECRegistry.spawnPandorasBox(level, new PBEffectMulti(pbEffects.toArray(new PBEffect[0]), new int[pbEffects.size()]), player, pos, floatAway, false);
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

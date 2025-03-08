package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public record ThrowItemsEntityEffect(double chancePerItem, double itemDeletionChance, ItemStack[] smuggledInItems) implements EntityEffect {
    public static final MapCodec<ThrowItemsEntityEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.DOUBLE.fieldOf("chance_per_item").forGetter(ThrowItemsEntityEffect::chancePerItem),
                            Codec.DOUBLE.fieldOf("item_deletion_chance").forGetter(ThrowItemsEntityEffect::itemDeletionChance),
                            PBNBTHelper.arrayCodec(ItemStack.CODEC, () -> new ItemStack[0]).fieldOf("smuggled_in_items").forGetter(ThrowItemsEntityEffect::smuggledInItems))
                    .apply(instance, ThrowItemsEntityEffect::new));
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
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}

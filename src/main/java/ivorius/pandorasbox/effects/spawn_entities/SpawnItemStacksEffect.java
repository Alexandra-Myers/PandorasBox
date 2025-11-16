package ivorius.pandorasbox.effects.spawn_entities;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record SpawnItemStacksEffect(ItemStack[] stacks, EntitySpawnConfiguration entitySpawnConfiguration) implements SpawnEntitiesEffect {
    public static final MapCodec<SpawnItemStacksEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(RandomizedItemStack.OPTIONAL_ITEM_CODEC, () -> new ItemStack[0]).fieldOf("stacks").forGetter(SpawnItemStacksEffect::stacks),
                            EntitySpawnConfiguration.MAP_CODEC.forGetter(SpawnItemStacksEffect::entitySpawnConfiguration))
                    .apply(instance, SpawnItemStacksEffect::new));
    @Override
    public Entity spawnEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, int number, double x, double y, double z) {
        if (world.isClientSide()) return null;
        if (stacks.length == 0) return null;
        if (number == stacks.length) return null;
        ItemEntity entityItem = new ItemEntity(world, x, y, z, stacks[number]);
        entityItem.setPickUpDelay(10);
        world.addFreshEntity(entityItem);
        return entityItem;
    }

    @Override
    public @NotNull MapCodec<? extends SpawnEntitiesEffect> codec() {
        return CODEC;
    }
}

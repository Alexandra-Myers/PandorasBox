package ivorius.pandorasbox.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class PandoraLootModifier extends LootModifier {
  public static final Supplier<Codec<PandoraLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).and(
          inst.group(
                  Codec.INT.fieldOf("chance").forGetter(m -> m.chance),
                  BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item)
          )).apply(inst, PandoraLootModifier::new)
  ));
  protected final int chance;
  protected final Item item;
  public PandoraLootModifier(LootItemCondition[] conditionsIn, int chance, Item item) {
    super(conditionsIn);
    this.chance = chance;
    this.item = item;
  }

  /**
   * Applies the modifier to the generated loot (all loot conditions have already been checked
   * and have returned true).
   *
   * @param generatedLoot the list of ItemStacks that will be dropped, generated by loot tables
   * @param context       the LootContext, identical to what is passed to loot tables
   * @return modified loot drops
   */
  @Override
  protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    generatedLoot = maybeInject(generatedLoot, chance);
    return generatedLoot;
  }
  public ObjectArrayList<ItemStack> maybeInject(ObjectArrayList<ItemStack> generatedLoot, int chance) {
    float percentageChance = chance / 100F;
    if(new Random().nextFloat() <= percentageChance) {
      generatedLoot.add(item.getDefaultInstance());
    }
    return generatedLoot;
  }

  /**
   * Returns the registered codec for this modifier
   */
  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return CODEC.get();
  }
}
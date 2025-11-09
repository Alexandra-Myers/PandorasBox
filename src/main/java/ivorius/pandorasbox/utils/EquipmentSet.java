package ivorius.pandorasbox.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record EquipmentSet(double weight, ItemStack[] items, Component name) implements WeightedSelector.Item {
    public static final Codec<EquipmentSet> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(EquipmentSet::weight),
                            PBNBTHelper.arrayCodec(ItemStack.CODEC, () -> new ItemStack[0]).fieldOf("items").forGetter(EquipmentSet::items),
                            ComponentSerialization.CODEC.fieldOf("name").forGetter(EquipmentSet::name))
                    .apply(instance, EquipmentSet::new));
    public static final Codec<EquipmentSet> INDIRECT_CODEC = Identifier.CODEC.xmap(identifier -> PandorasBoxHelper.registeredSets.get(identifier), set -> PandorasBoxHelper.registeredSets.inverse().get(set));
}

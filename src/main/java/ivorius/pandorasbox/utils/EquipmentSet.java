package ivorius.pandorasbox.utils;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.init.Init;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record EquipmentSet(double weight, ItemStack[] items, Component name) implements WeightedSelector.Item {
    public static final Codec<EquipmentSet> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(EquipmentSet::weight),
                            PBNBTHelper.arrayCodec(ItemStack.CODEC, () -> new ItemStack[0]).fieldOf("items").forGetter(EquipmentSet::items),
                            PBNBTHelper.COMPONENT_CODEC.fieldOf("name").forGetter(EquipmentSet::name))
                    .apply(instance, EquipmentSet::new));
    public static final Codec<HolderSet<@NotNull EquipmentSet>> INDIRECT_CODEC = RegistryCodecs.homogeneousList(Init.EQUIPMENT_SET_REGISTRY_KEY);
}

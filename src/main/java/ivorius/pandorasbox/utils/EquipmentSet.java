package ivorius.pandorasbox.utils;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.PandorasBoxHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record EquipmentSet(ItemStack[] set) {
    public static final Codec<EquipmentSet> CODEC = PBNBTHelper.arrayCodec(ItemStack.CODEC, () -> new ItemStack[0]).xmap(EquipmentSet::new, EquipmentSet::set);
    public static final Codec<EquipmentSet> INDIRECT_CODEC = Identifier.CODEC.xmap(identifier -> PandorasBoxHelper.registeredSets.get(identifier), set -> PandorasBoxHelper.registeredSets.inverse().get(set));
}

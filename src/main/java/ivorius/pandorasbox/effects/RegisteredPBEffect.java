package ivorius.pandorasbox.effects;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class RegisteredPBEffect extends ForgeRegistryEntry<RegisteredPBEffect> {
    public final Class<? extends PBEffect> clazz;
    public RegisteredPBEffect(Class<? extends PBEffect> base) {
        clazz = base;
    }
}

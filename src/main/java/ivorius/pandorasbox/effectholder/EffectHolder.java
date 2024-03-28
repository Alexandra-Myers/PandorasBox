package ivorius.pandorasbox.effectholder;

import ivorius.pandorasbox.effectcreators.PBEffectCreator;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class EffectHolder extends ForgeRegistryEntry<EffectHolder> {
    public PBEffectCreator effectCreator;
    public void defineEffectCreator(PBEffectCreator effectCreator) {
        this.effectCreator = effectCreator;
    }
    public abstract boolean canBeGoodOrBad();
    public abstract boolean isGood();
    public abstract double fixedChance();
}
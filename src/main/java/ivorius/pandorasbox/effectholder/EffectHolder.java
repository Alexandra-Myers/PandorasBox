package ivorius.pandorasbox.effectholder;

import ivorius.pandorasbox.effectcreators.PBEffectCreator;

public abstract class EffectHolder {
    public PBEffectCreator effectCreator;
    public void defineEffectCreator(PBEffectCreator effectCreator) {
        this.effectCreator = effectCreator;
    }
    public abstract boolean canBeGoodOrBad();
    public abstract boolean isGood();
    public abstract double fixedChance();
}

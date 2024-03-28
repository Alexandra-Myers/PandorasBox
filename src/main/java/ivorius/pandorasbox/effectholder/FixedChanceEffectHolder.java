package ivorius.pandorasbox.effectholder;

public class FixedChanceEffectHolder extends EffectHolder {
    public final double fixedChance;
    public FixedChanceEffectHolder(double fixedChance) {
        this.fixedChance = fixedChance;
    }

    @Override
    public boolean canBeGoodOrBad() {
        return false;
    }

    @Override
    public boolean isGood() {
        return false;
    }

    @Override
    public double fixedChance() {
        return fixedChance;
    }
}
package ivorius.pandorasbox.effectholder;

public class FixedChancePositiveOrNegativeEffectHolder extends EffectHolder {
    public final boolean good;
    public final double fixedChance;
    public FixedChancePositiveOrNegativeEffectHolder(double fixedChance, boolean good) {
        this.fixedChance = fixedChance;
        this.good = good;
    }

    @Override
    public boolean canBeGoodOrBad() {
        return true;
    }

    @Override
    public boolean isGood() {
        return good;
    }

    @Override
    public double fixedChance() {
        return fixedChance;
    }
}
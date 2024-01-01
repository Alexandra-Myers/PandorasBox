package ivorius.pandorasbox.effectholder;

public class PositiveOrNegativeEffectHolder extends EffectHolder {
    public final boolean good;
    public PositiveOrNegativeEffectHolder (boolean good) {
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
        return -1;
    }
}

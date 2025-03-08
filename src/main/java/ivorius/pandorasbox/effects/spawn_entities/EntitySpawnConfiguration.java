package ivorius.pandorasbox.effects.spawn_entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EntitySpawnConfiguration(boolean spawnsFromBox, boolean spawnDirect, double range, double shiftY, double throwStrengthSideMin, double throwStrengthSideMax, double throwStrengthYMin, double throwStrengthYMax) {
    public static final MapCodec<EntitySpawnConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.BOOL.fieldOf("comes_from_box").forGetter(EntitySpawnConfiguration::spawnsFromBox),
                            Codec.BOOL.fieldOf("spawn_direct").forGetter(EntitySpawnConfiguration::spawnDirect),
                            Codec.DOUBLE.fieldOf("range").forGetter(EntitySpawnConfiguration::range),
                            Codec.DOUBLE.fieldOf("shift_y").forGetter(EntitySpawnConfiguration::shiftY),
                            Codec.DOUBLE.fieldOf("throw_strength_side_min").forGetter(EntitySpawnConfiguration::throwStrengthSideMin),
                            Codec.DOUBLE.fieldOf("throw_strength_side_max").forGetter(EntitySpawnConfiguration::throwStrengthSideMax),
                            Codec.DOUBLE.fieldOf("throw_strength_y_min").forGetter(EntitySpawnConfiguration::throwStrengthYMin),
                            Codec.DOUBLE.fieldOf("throw_strength_y_max").forGetter(EntitySpawnConfiguration::throwStrengthYMax))
                    .apply(instance, EntitySpawnConfiguration::new));

    public static EntitySpawnConfiguration.Builder builder(boolean spawnsFromBox) {
        return new Builder(spawnsFromBox);
    }

    public static class Builder {
        boolean spawnsFromBox;
        boolean spawnDirect;
        double range;
        double shiftY;
        double throwStrengthSideMin;
        double throwStrengthSideMax;
        double throwStrengthYMin;
        double throwStrengthYMax;
        public Builder(boolean spawnsFromBox) {
            this.spawnsFromBox = spawnsFromBox;
        }
        public EntitySpawnConfiguration.Builder doesNotSpawnDirect(double range, double shiftY) {
            this.spawnDirect = false;
            this.range = range;
            this.shiftY = shiftY;
            return this;
        }

        public EntitySpawnConfiguration.Builder doesSpawnDirect(double throwStrengthSideMin, double throwStrengthSideMax, double throwStrengthYMin, double throwStrengthYMax) {
            this.spawnDirect = true;
            this.throwStrengthSideMin = throwStrengthSideMin;
            this.throwStrengthSideMax = throwStrengthSideMax;
            this.throwStrengthYMin = throwStrengthYMin;
            this.throwStrengthYMax = throwStrengthYMax;
            return this;
        }
        public EntitySpawnConfiguration build() {
            return new EntitySpawnConfiguration(spawnsFromBox, spawnDirect, range, shiftY, throwStrengthSideMin, throwStrengthSideMax, throwStrengthYMin, throwStrengthYMax);
        }
    }
}

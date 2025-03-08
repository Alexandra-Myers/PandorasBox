package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record NetherConvertEffect(NetherBiome netherBiome, double discardNetherrackChance) implements GenerateEffect {
    public static final MapCodec<NetherConvertEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(NetherBiome.CODEC.fieldOf("biome").forGetter(NetherConvertEffect::netherBiome),
                            Codec.DOUBLE.fieldOf("discard_netherrack_chance").forGetter(NetherConvertEffect::discardNetherrackChance))
                    .apply(instance, NetherConvertEffect::new));

    @Override
    public void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, double ratio, int unifiedSeed) {
        if (level instanceof ServerLevel serverLevel) {
            netherBiome.create(serverLevel, entity, random, pass, (float) ratio, pos, discardNetherrackChance);
        }
    }

    @Override
    public @Nullable ResourceKey<Biome> biome() {
        return netherBiome.biomeResourceKey;
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffect> codec() {
        return CODEC;
    }
}

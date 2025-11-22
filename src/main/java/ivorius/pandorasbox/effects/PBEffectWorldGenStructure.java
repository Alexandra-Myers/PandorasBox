package ivorius.pandorasbox.effects;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBECStructure;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PBEffectWorldGenStructure extends PBEffectNormal {
    public static final MapCodec<PBEffectWorldGenStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.INT.fieldOf("blocks_per_tick").forGetter(pbEffectWorldGenStructure -> pbEffectWorldGenStructure.blocksPerTick),
                            BlockState.CODEC.listOf().listOf().fieldOf("palette").forGetter(pbEffectWorldGenStructure -> pbEffectWorldGenStructure.palettes),
                            PBECStructure.BlockUpdateData.CODEC.listOf().fieldOf("block_updates").forGetter(pbEffectWorldGenStructure -> pbEffectWorldGenStructure.blockUpdates))
                    .apply(instance, PBEffectWorldGenStructure::new));
    private final int blocksPerTick;
    private final List<List<BlockState>> palettes;
    private final List<PBECStructure.BlockUpdateData> blockUpdates;

    public static PBEffectWorldGenStructure from(int maxTicksAlive, int blocksPerTick, List<List<String>> palettes, List<PBECStructure.BlockUpdateData> blockUpdates) {
        return new PBEffectWorldGenStructure(maxTicksAlive, blocksPerTick, palettes.stream().map(palette -> palette.stream().map(state -> {
            try {
                return BlockState.CODEC.parse(NbtOps.INSTANCE, TagParser.parseTag(state)).getOrThrow(false, s -> {});
            } catch (CommandSyntaxException e) {
                return Blocks.AIR.defaultBlockState();
            }
        }).toList()).toList(), blockUpdates);
    }

    public PBEffectWorldGenStructure(int maxTicksAlive, int blocksPerTick, List<List<BlockState>> palettes, List<PBECStructure.BlockUpdateData> blockUpdates) {
        super(maxTicksAlive);
        this.blocksPerTick = blocksPerTick;
        this.palettes = palettes;
        this.blockUpdates = blockUpdates;
        this.blockUpdates.forEach(blockUpdateData -> {
            if (palettes.size() > blockUpdateData.palette()) blockUpdateData.setState(palettes.get(blockUpdateData.palette()).get(blockUpdateData.id()));
        });
    }


    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (level.isClientSide()) return;
        int index = 0;
        for (; index < blocksPerTick && index < blockUpdates.size(); index++) {
            PBECStructure.BlockUpdateData data = blockUpdates.get(index);
            setBlockSafe(level, data.pos(), data.state());
            data.tag()
                    .map(entityTag -> BlockEntity.loadStatic(data.pos(), data.state(), entityTag))
                    .ifPresent(level::setBlockEntity);
        }
        while (index > 0) {
            blockUpdates.remove(0);
            index--;
        }
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}

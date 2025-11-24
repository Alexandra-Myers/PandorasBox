package ivorius.pandorasbox.effects;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.PBECStructure;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PBEffectWorldGenStructure extends PBEffectNormal {
    public static final MapCodec<PBEffectWorldGenStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.INT.fieldOf("blocks_per_tick").forGetter(pbEffectWorldGenStructure -> pbEffectWorldGenStructure.blocksPerTick),
                            BlockState.CODEC.listOf().listOf().fieldOf("palette").forGetter(pbEffectWorldGenStructure -> pbEffectWorldGenStructure.palettes),
                            PBECStructure.BlockUpdateData.CODEC.listOf().fieldOf("block_updates").forGetter(pbEffectWorldGenStructure -> pbEffectWorldGenStructure.blockUpdates),
                            CompoundTag.CODEC.listOf().fieldOf("entities").forGetter(PBEffectWorldGenStructure::entitiesIntoTags))
                    .apply(instance, PBEffectWorldGenStructure::new));
    private final int blocksPerTick;
    private final List<List<BlockState>> palettes;
    private final List<PBECStructure.BlockUpdateData> blockUpdates;
    private final List<CompoundTag> entityTags;
    private List<Entity> entities;

    public PBEffectWorldGenStructure(int maxTicksAlive, int blocksPerTick, List<List<BlockState>> palettes, List<PBECStructure.BlockUpdateData> blockUpdates, List<CompoundTag> entityTags, List<Entity> entities) {
        super(maxTicksAlive);
        this.blocksPerTick = blocksPerTick;
        this.palettes = palettes;
        this.blockUpdates = new ArrayList<>(blockUpdates);
        this.blockUpdates.forEach(blockUpdateData -> {
            if (palettes.size() > blockUpdateData.palette()) blockUpdateData.setState(palettes.get(blockUpdateData.palette()).get(blockUpdateData.id()));
        });
        this.entityTags = entityTags;
        this.entities = entities;
    }

    public PBEffectWorldGenStructure(int maxTicksAlive, int blocksPerTick, List<List<BlockState>> palettes, List<PBECStructure.BlockUpdateData> blockUpdates, List<CompoundTag> entityTags) {
        this(maxTicksAlive, blocksPerTick, palettes, blockUpdates, entityTags, null);
    }

    public List<Entity> entities(Level level) {
        if (this.entities == null) this.entities = EntityType.loadEntitiesRecursive(entityTags, level).toList();
        return this.entities;
    }

    public List<CompoundTag> entitiesIntoTags() {
        return this.entities == null ? this.entityTags : this.entities.stream().map(entity -> {
            CompoundTag tag = new CompoundTag();
            entity.save(tag);
            return tag;
        }).toList();
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (level.isClientSide()) return;
        int index = 0;
        for (; index < blocksPerTick && index < blockUpdates.size(); index++) {
            PBECStructure.BlockUpdateData data = blockUpdates.get(index);
            level.setBlockAndUpdate(data.pos(), data.state());
            data.tag()
                    .map(entityTag -> BlockEntity.loadStatic(data.pos(), data.state(), entityTag, level.registryAccess()))
                    .ifPresent(level::setBlockEntity);
        }
        while (index > 0) {
            blockUpdates.removeFirst();
            index--;
        }
    }

    @Override
    public void finalizeEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {
        super.finalizeEffect(level, entity, effectCenter, random);
        this.entities(level).forEach(entity1 -> entity1.getSelfAndPassengers().forEach(level::addFreshEntity));
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
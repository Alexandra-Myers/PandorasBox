/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.structure.CreativeTowerConfiguration;
import ivorius.pandorasbox.effects.structure.StructureCreativeTower;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.PBEffectInit;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenCreativeTowers extends PBEffectGenerateByStructure<StructureCreativeTower> {
    public static final MapCodec<PBEffectGenCreativeTowers> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            PBNBTHelper.arrayCodec(StructureCreativeTower.CODEC, () -> new StructureCreativeTower[0]).fieldOf("structures").forGetter(PBEffectGenCreativeTowers::getStructures))
                    .apply(instance, PBEffectGenCreativeTowers::new));
    public PBEffectGenCreativeTowers(int maxTicksAlive) {
        this(maxTicksAlive, new StructureCreativeTower[0]);
    }
    public PBEffectGenCreativeTowers(int maxTicksAlive, StructureCreativeTower[] structures) {
        super(maxTicksAlive);
        this.structures = structures;
    }

    public void createRandomStructures(RandomSource random, int number, double range, Collection<WeightedBlock> blocks) {
        this.structures = new StructureCreativeTower[number];
        for (int i = 0; i < number; i++) {
            structures[i] = createStructure();
            applyRandomProperties(structures[i], range, random);
            structures[i].configuration = new CreativeTowerConfiguration(PandorasBoxHelper.getRandomBlockList(random, blocks));
        }
    }

    @Override
    public void generateStructure(Level level, PandorasBoxEntity entity, RandomSource random, StructureCreativeTower structure, BlockPos pos, float newRatio, float prevRatio) {
        int towerHeight = level.getHeight();
        int newY = Mth.floor(towerHeight * newRatio);
        int prevY = Mth.floor(towerHeight * prevRatio);

        for (int towerY = prevY; towerY < newY; towerY++) {
            Block block = structure.getBlocks()[random.nextInt(structure.getBlocks().length)];

            setBlockVarying(level, new BlockPos(pos.getX() + structure.pos.getX(), towerY, pos.getZ() + structure.pos.getZ()), block, structure.unifiedSeed);
        }
    }

    @Override
    public StructureCreativeTower createStructure() {
        return new StructureCreativeTower();
    }

    @Override
    public @NotNull PBEffectType<? extends PBEffect> type() {
        return PBEffectInit.GEN_CREATIVE_TOWERS;
    }


}

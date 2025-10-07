/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.two_dimensional.GenDome;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenWorldSnake extends PBEffectNormal {
    public static final MapCodec<PBEffectGenWorldSnake> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(),  () -> new Block[0]).fieldOf("blocks").forGetter(PBEffectGenWorldSnake::getBlocks),
                            Codec.INT.fieldOf("unified_seed").forGetter(PBEffectGenWorldSnake::getUnifiedSeed),
                            Vec3.CODEC.fieldOf("current_position").forGetter(PBEffectGenWorldSnake::getCurrent),
                            Codec.DOUBLE.fieldOf("size").forGetter(PBEffectGenWorldSnake::getSize),
                            Codec.DOUBLE.fieldOf("speed").forGetter(PBEffectGenWorldSnake::getSpeed),
                            Codec.FLOAT.fieldOf("direction_yaw").forGetter(PBEffectGenWorldSnake::getDirYaw),
                            Codec.FLOAT.fieldOf("direction_pitch").forGetter(PBEffectGenWorldSnake::getDirPitch),
                            Codec.FLOAT.fieldOf("direction_yaw_accel").forGetter(PBEffectGenWorldSnake::getDirYawAcc),
                            Codec.FLOAT.fieldOf("direction_pitch_accel").forGetter(PBEffectGenWorldSnake::getDirPitchAcc))
                    .apply(instance, PBEffectGenWorldSnake::new));
    public Block[] blocks;
    public int unifiedSeed;

    public Vec3 current;

    public double size;

    public double speed;
    public float dirYaw;
    public float dirPitch;
    public float dirYawAcc;
    public float dirPitchAcc;
    public PBEffectGenWorldSnake(int maxTicksAlive, Block[] blocks, int unifiedSeed, Vec3 current, double size, double speed, float dirYaw, float dirPitch, float dirYawAcc, float dirPitchAcc) {
        super(maxTicksAlive);
        this.blocks = blocks;
        this.unifiedSeed = unifiedSeed;
        this.current = current;
        this.size = size;
        this.speed = speed;
        this.dirYaw = dirYaw;
        this.dirPitch = dirPitch;
        this.dirYawAcc = dirYawAcc;
        this.dirPitchAcc = dirPitchAcc;
    }

    public PBEffectGenWorldSnake(int maxTicksAlive, Block[] blocks, int unifiedSeed, double currentX, double currentY, double currentZ, double size, double speed, float dirYaw, float dirPitch) {
        this(maxTicksAlive, blocks, unifiedSeed, new Vec3(currentX, currentY, currentZ), size, speed, dirYaw, dirPitch, 0, 0);
    }

    public Block[] getBlocks() {
        return blocks;
    }

    public int getUnifiedSeed() {
        return unifiedSeed;
    }

    public Vec3 getCurrent() {
        return current;
    }

    public double getSize() {
        return size;
    }

    public double getSpeed() {
        return speed;
    }

    public float getDirYaw() {
        return dirYaw;
    }

    public float getDirPitch() {
        return dirPitch;
    }

    public float getDirYawAcc() {
        return dirYawAcc;
    }

    public float getDirPitchAcc() {
        return dirPitchAcc;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (!level.isClientSide()) {
            int requiredRange = Mth.ceil(size);

            float f1 = Mth.cos(-dirYaw * 0.017453292F - (float) Math.PI);
            float f2 = Mth.sin(-dirYaw * 0.017453292F - (float) Math.PI);
            float f3 = -Mth.cos(-dirPitch * 0.017453292F);
            float f4 = Mth.sin(-dirPitch * 0.017453292F);

            double dirX = f2 * f3 * speed;
            double dirY = f4 * speed;
            double dirZ = f1 * f3 * speed;

            double newX = current.x + dirX;
            double newY = current.y + dirY;
            double newZ = current.z + dirZ;

            int baseX = Mth.floor(newX);
            int baseY = Mth.floor(newY);
            int baseZ = Mth.floor(newZ);

            for (int x = -requiredRange; x <= requiredRange; x++) {
                for (int y = -requiredRange; y <= requiredRange; y++) {
                    for (int z = -requiredRange; z <= requiredRange; z++) {
                        if (GenDome.isSpherePart(baseX + x + 0.5, baseY + y + 0.5, baseZ + z + 0.5, newX, newY, newZ, 0.0, size)) {
                            if (!GenDome.isSpherePart(baseX + x + 0.5, baseY + y + 0.5, baseZ + z + 0.5, current.x, current.y, current.z, 0.0, size)) {
                                setBlockVarying(level, new BlockPos(x + baseX, y + baseY, z + baseZ), blocks[random.nextInt(blocks.length)], unifiedSeed);
                            }
                        }
                    }
                }
            }

            current = new Vec3(newX, newY, newZ);

            dirYaw += dirYawAcc;
            dirPitch += dirPitchAcc;

            dirYawAcc += Mth.clamp((random.nextFloat() - random.nextFloat()) * 0.5f, -10.0f, 10.0f);
            dirPitchAcc += Mth.clamp((random.nextFloat() - random.nextFloat()) * 0.5f, -10.0f, 10.0f);
        }
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}

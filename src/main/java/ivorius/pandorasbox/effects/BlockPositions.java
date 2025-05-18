package ivorius.pandorasbox.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

/**
 * Created by lukas on 21.07.15.
 */

public final class BlockPositions {
    public static AABB expandToAABB(BlockPos pos, double x, double y, double z) {
        return new AABB(pos).expandTowards(x, y, z);
    }
}
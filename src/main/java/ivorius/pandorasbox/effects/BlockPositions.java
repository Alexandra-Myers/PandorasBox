package ivorius.pandorasbox.effects;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;

import java.util.Collection;

/**
 * Created by lukas on 21.07.15.
 */
public final class BlockPositions
{
    private BlockPositions()
    {
    }

    public static BlockPos fromIntArray(int[] array)
    {
        if (array.length != 3)
            throw new IllegalArgumentException();

        return new BlockPos(array[0], array[1], array[2]);
    }

    public static int[] toIntArray(BlockPos pos)
    {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static BlockPos readWithBase(CompoundTag compound, String keyBase)
    {
        return new BlockPos(compound.getInt(keyBase + "_x"), compound.getInt(keyBase + "_y"), compound.getInt(keyBase + "_z"));
    }

    public static void writeToNBT(String keyBase, BlockPos coord, CompoundTag compound)
    {
        if (coord != null)
        {
            compound.putInt(keyBase + "_x", coord.getX());
            compound.putInt(keyBase + "_y", coord.getY());
            compound.putInt(keyBase + "_z", coord.getZ());
        }
    }

    public static BlockPos readFromNBT(String keyBase, CompoundTag compound)
    {
        return compound.contains(keyBase + "_x") && compound.contains(keyBase + "_y") && compound.contains(keyBase + "_z")
                ? new BlockPos(compound.getInt(keyBase + "_x"), compound.getInt(keyBase + "_y"), compound.getInt(keyBase + "_z"))
                : null;

    }

    public static void maybeWriteToBuffer(BlockPos coord, ByteBuf buffer)
    {
        buffer.writeBoolean(coord != null);

        if (coord != null)
            writeToBuffer(coord, buffer);
    }

    public static BlockPos maybeReadFromBuffer(ByteBuf buffer)
    {
        return buffer.readBoolean() ? readFromBuffer(buffer) : null;
    }

    public static void writeToBuffer(BlockPos coord, ByteBuf buffer)
    {
        buffer.writeInt(coord.getX());
        buffer.writeInt(coord.getY());
        buffer.writeInt(coord.getZ());
    }

    public static BlockPos readFromBuffer(ByteBuf buffer)
    {
        return new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static BlockPos getLowerCorner(Collection<BlockPos> positions)
    {
        int x = 0, y = 0, z = 0;
        boolean first = true;

        for (BlockPos position : positions)
        {
            if (first)
            {
                x = position.getX();
                y = position.getY();
                z = position.getZ();
                first = false;
            }

            x = Math.min(x, position.getX());
            y = Math.min(y, position.getY());
            z = Math.min(z, position.getZ());
        }

        if (first)
            throw new ArrayIndexOutOfBoundsException();

        return new BlockPos(x, y, z);
    }

    public static BlockPos getHigherCorner(Collection<BlockPos> positions)
    {
        int x = 0, y = 0, z = 0;
        boolean first = true;

        for (BlockPos position : positions)
        {
            if (first)
            {
                x = position.getX();
                y = position.getY();
                z = position.getZ();
                first = false;
            }

            x = Math.max(x, position.getX());
            y = Math.max(y, position.getY());
            z = Math.max(z, position.getZ());
        }

        if (first)
            throw new ArrayIndexOutOfBoundsException();

        return new BlockPos(x, y, z);
    }

    public static BlockPos getLowerCorner(BlockPos one, BlockPos two)
    {
        return new BlockPos(Math.min(one.getX(), two.getX()), Math.min(one.getY(), two.getY()), Math.min(one.getZ(), two.getZ()));
    }

    public static BlockPos getHigherCorner(BlockPos one, BlockPos two)
    {
        return new BlockPos(Math.max(one.getX(), two.getX()), Math.max(one.getY(), two.getY()), Math.max(one.getZ(), two.getZ()));
    }

    public static BlockPos invert(BlockPos pos)
    {
        return new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    public static BlockPos sub(BlockPos pos, BlockPos sub)
    {
        return new BlockPos(pos.getX() - sub.getX(), pos.getY() - sub.getY(), pos.getZ() - sub.getZ());
    }

    public static AABB expandToAABB(BlockPos pos, double x, double y, double z)
    {
        return new AABB(pos).expandTowards(x, y, z);
    }
}
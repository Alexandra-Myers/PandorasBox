package ivorius.pandorasbox.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by lukas on 03.02.15.
 */
public class PBNBTHelper
{
    public static byte readByte(CompoundNBT compound, String key, byte defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_BYTE)
                ? compound.getByte(key)
                : defaultValue;
    }

    public static byte[] readByteArray(CompoundNBT compound, String key, byte[] defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_BYTE_ARRAY)
                ? compound.getByteArray(key)
                : defaultValue;
    }

    public static double readDouble(CompoundNBT compound, String key, double defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_DOUBLE)
                ? compound.getDouble(key)
                : defaultValue;
    }

    public static float readFloat(CompoundNBT compound, String key, float defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_FLOAT)
                ? compound.getFloat(key)
                : defaultValue;
    }

    public static int readInt(CompoundNBT compound, String key, int defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_INT)
                ? compound.getInt(key)
                : defaultValue;
    }

    public static int[] readIntArray(CompoundNBT compound, String key, int[] defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_INT_ARRAY)
                ? compound.getIntArray(key)
                : defaultValue;
    }

    public static long readLong(CompoundNBT compound, String key, long defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_LONG)
                ? compound.getLong(key)
                : defaultValue;
    }

    public static short readShort(CompoundNBT compound, String key, short defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_SHORT)
                ? compound.getShort(key)
                : defaultValue;
    }

    public static String readString(CompoundNBT compound, String key, String defaultValue)
    {
        return compound != null && compound.contains(key, Constants.NBT.TAG_STRING)
                ? compound.getString(key)
                : defaultValue;
    }

    public static double[] readDoubleArray(String key, CompoundNBT compound)
    {
        if (compound.contains(key))
        {
            ListNBT list = compound.getList(key, Constants.NBT.TAG_DOUBLE);
            double[] array = new double[list.size()];

            for (int i = 0; i < array.length; i++)
                array[i] = list.getDouble(i);

            return array;
        }

        return null;
    }

    public static String[] readNBTStrings(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_STRING);
            String[] strings = new String[nbtTagList.size()];

            for (int i = 0; i < strings.length; i++)
                strings[i] = nbtTagList.getString(i);

            return strings;
        }

        return null;
    }

    public static void writeNBTStrings(String id, String[] strings, CompoundNBT compound)
    {
        if (strings != null)
        {
            ListNBT nbtTagList = new ListNBT();

            for (String s : strings)
                nbtTagList.add(StringNBT.valueOf(s));

            compound.put(id, nbtTagList);
        }
    }

    public static ItemStack[] readNBTStacks(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_COMPOUND);
            ItemStack[] itemStacks = new ItemStack[nbtTagList.size()];
            for (int i = 0; i < itemStacks.length; i++)
                itemStacks[i] = ItemStack.of(nbtTagList.get(i) instanceof CompoundNBT ? (CompoundNBT) nbtTagList.get(i) : new CompoundNBT());

            return itemStacks;
        }

        return null;
    }

    public static void writeNBTStacks(String id, ItemStack[] stacks, CompoundNBT compound)
    {
        if (stacks != null)
        {
            ListNBT nbtTagList = new ListNBT();

            for (ItemStack stack : stacks)
            {
                CompoundNBT tagCompound = new CompoundNBT();
                stack.save(tagCompound);
                nbtTagList.add(tagCompound);
            }

            compound.put(id, nbtTagList);
        }
    }

    public static Block[] readNBTBlocks(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_STRING);
            Block[] blocks = new Block[nbtTagList.size()];

            for (int i = 0; i < blocks.length; i++)
                blocks[i] = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbtTagList.getString(i)));

            return blocks;
        }

        return null;
    }

    public static void writeNBTBlocks(String id, Block[] blocks, CompoundNBT compound)
    {
        if (blocks != null)
        {
            ListNBT nbtTagList = new ListNBT();

            for (Block b : blocks)
                nbtTagList.add(StringNBT.valueOf(PBNBTHelper.storeBlockString(b)));

            compound.put(id, nbtTagList);
        }
    }

    public static long[] readNBTLongs(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ByteBuf bytes = Unpooled.copiedBuffer(compound.getByteArray(id));
            long[] longs = new long[bytes.capacity() / 8];
            for (int i = 0; i < longs.length; i++) longs[i] = bytes.readLong();
            return longs;
        }

        return null;
    }

    public static void writeNBTLongs(String id, long[] longs, CompoundNBT compound)
    {
        if (longs != null)
        {
            ByteBuf bytes = Unpooled.buffer(longs.length * 8);
            for (long aLong : longs) bytes.writeLong(aLong);
            compound.putByteArray(id, bytes.array());
        }
    }

    public static EffectInstance[] readNBTPotions(String id, CompoundNBT compound)
    {
        if (compound.contains(id))
        {
            ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_STRING);
            EffectInstance[] potions = new EffectInstance[nbtTagList.size()];

            for (int i = 0; i < potions.length; i++)
                potions[i] = EffectInstance.load(nbtTagList.getCompound(i));

            return potions;
        }

        return null;
    }

    public static void writeNBTPotions(String id, EffectInstance[] potions, CompoundNBT compound)
    {
        if (potions != null)
        {
            ListNBT nbtTagList = new ListNBT();

            for (EffectInstance p : potions)
                nbtTagList.add(p.save(new CompoundNBT()));

            compound.put(id, nbtTagList);
        }
    }

    public static int[] readIntArrayFixedSize(String id, int length, CompoundNBT compound)
    {
        int[] array = compound.getIntArray(id);
        return array.length != length ? new int[length] : array;
    }

    // Above, from IvToolkit
    // From Pandora's Box

    public static String[][] readNBTStrings2D(String id, CompoundNBT compound)
    {
        ListNBT nbtTagList = compound.getList(id, Constants.NBT.TAG_COMPOUND);
        String[][] strings = new String[nbtTagList.size()][];

        for (int i = 0; i < strings.length; i++)
            strings[i] = readNBTStrings("Strings", nbtTagList.getCompound(i));

        return strings;
    }

    public static void writeNBTStrings2D(String id, String[][] strings, CompoundNBT compound)
    {
        ListNBT nbtTagList = new ListNBT();

        for (String[] s : strings)
        {
            CompoundNBT compound1 = new CompoundNBT();
            writeNBTStrings("Strings", s, compound1);
            nbtTagList.add(compound1);
        }

        compound.put(id, nbtTagList);
    }

    public static String storeBlockString(Block block)
    {
        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
        return location == null ? "minecraft:air" : location.toString();
    }

    public static Block getBlock(String string)
    {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(string));
    }
}

package ivorius.pandorasbox.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Created by lukas on 03.02.15.
 */
public class PBNBTHelper
{
    public static byte readByte(CompoundTag compound, String key, byte defaultValue)
    {
        return compound != null && compound.contains(key, 1)
                ? compound.getByte(key)
                : defaultValue;
    }

    public static byte[] readByteArray(CompoundTag compound, String key, byte[] defaultValue)
    {
        return compound != null && compound.contains(key, 7)
                ? compound.getByteArray(key)
                : defaultValue;
    }

    public static double readDouble(CompoundTag compound, String key, double defaultValue)
    {
        return compound != null && compound.contains(key, 6)
                ? compound.getDouble(key)
                : defaultValue;
    }

    public static float readFloat(CompoundTag compound, String key, float defaultValue)
    {
        return compound != null && compound.contains(key, 5)
                ? compound.getFloat(key)
                : defaultValue;
    }

    public static int readInt(CompoundTag compound, String key, int defaultValue)
    {
        return compound != null && compound.contains(key, 3)
                ? compound.getInt(key)
                : defaultValue;
    }

    public static int[] readIntArray(CompoundTag compound, String key, int[] defaultValue)
    {
        return compound != null && compound.contains(key, 11)
                ? compound.getIntArray(key)
                : defaultValue;
    }

    public static long readLong(CompoundTag compound, String key, long defaultValue)
    {
        return compound != null && compound.contains(key, 4)
                ? compound.getLong(key)
                : defaultValue;
    }

    public static short readShort(CompoundTag compound, String key, short defaultValue)
    {
        return compound != null && compound.contains(key, 2)
                ? compound.getShort(key)
                : defaultValue;
    }

    public static String readString(CompoundTag compound, String key, String defaultValue)
    {
        return compound != null && compound.contains(key, 8)
                ? compound.getString(key)
                : defaultValue;
    }

    public static double[] readDoubleArray(String key, CompoundTag compound)
    {
        if (compound.contains(key))
        {
            ListTag list = compound.getList(key, 6);
            double[] array = new double[list.size()];

            for (int i = 0; i < array.length; i++)
                array[i] = list.getDouble(i);

            return array;
        }

        return null;
    }

    public static String[] readNBTStrings(String id, CompoundTag compound)
    {
        if (compound.contains(id))
        {
            ListTag nbtTagList = compound.getList(id, 8);
            String[] strings = new String[nbtTagList.size()];

            for (int i = 0; i < strings.length; i++)
                strings[i] = nbtTagList.getString(i);

            return strings;
        }

        return null;
    }

    public static void writeNBTStrings(String id, String[] strings, CompoundTag compound)
    {
        if (strings != null)
        {
            ListTag nbtTagList = new ListTag();

            for (String s : strings)
                nbtTagList.add(StringTag.valueOf(s));

            compound.put(id, nbtTagList);
        }
    }

    public static EntityType<?>[] readNBTEntities(String id, CompoundTag compound)
    {
        if (compound.contains(id))
        {
            ListTag nbtTagList = compound.getList(id, 8);
            EntityType<?>[] entities = new EntityType<?>[nbtTagList.size()];

            for (int i = 0; i < entities.length; i++)
                entities[i] = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(nbtTagList.getString(i)));

            return entities;
        }

        return null;
    }

    public static void writeNBTEntities(String id, EntityType<?>[] entities, CompoundTag compound) {
        if (entities != null) {
            ListTag nbtTagList = new ListTag();

            for (EntityType<?> e : entities)
                nbtTagList.add(StringTag.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(e).getPath()));

            compound.put(id, nbtTagList);
        }
    }

    public static ItemStack[] readNBTStacks(String id, CompoundTag compound)
    {
        if (compound.contains(id))
        {
            ListTag nbtTagList = compound.getList(id, 10);
            ItemStack[] itemStacks = new ItemStack[nbtTagList.size()];
            for (int i = 0; i < itemStacks.length; i++)
                itemStacks[i] = ItemStack.of(nbtTagList.get(i) instanceof CompoundTag ? (CompoundTag) nbtTagList.get(i) : new CompoundTag());

            return itemStacks;
        }

        return null;
    }

    public static void writeNBTStacks(String id, ItemStack[] stacks, CompoundTag compound)
    {
        if (stacks != null)
        {
            ListTag nbtTagList = new ListTag();

            for (ItemStack stack : stacks)
            {
                CompoundTag tagCompound = new CompoundTag();
                stack.save(tagCompound);
                nbtTagList.add(tagCompound);
            }

            compound.put(id, nbtTagList);
        }
    }

    public static Block[] readNBTBlocks(String id, CompoundTag compound)
    {
        if (compound.contains(id))
        {
            ListTag nbtTagList = compound.getList(id, 8);
            Block[] blocks = new Block[nbtTagList.size()];

            for (int i = 0; i < blocks.length; i++)
                blocks[i] = BuiltInRegistries.BLOCK.get(new ResourceLocation(nbtTagList.getString(i)));

            return blocks;
        }

        return null;
    }

    public static void writeNBTBlocks(String id, Block[] blocks, CompoundTag compound)
    {
        if (blocks != null)
        {
            ListTag nbtTagList = new ListTag();

            for (Block b : blocks)
                nbtTagList.add(StringTag.valueOf(PBNBTHelper.storeBlockString(b)));

            compound.put(id, nbtTagList);
        }
    }

    public static long[] readNBTLongs(String id, CompoundTag compound)
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

    public static void writeNBTLongs(String id, long[] longs, CompoundTag compound)
    {
        if (longs != null)
        {
            ByteBuf bytes = Unpooled.buffer(longs.length * 8);
            for (long aLong : longs) bytes.writeLong(aLong);
            compound.putByteArray(id, bytes.array());
        }
    }

    public static MobEffectInstance[] readNBTPotions(String id, CompoundTag compound)
    {
        if (compound.contains(id))
        {
            ListTag nbtTagList = compound.getList(id, 8);
            MobEffectInstance[] potions = new MobEffectInstance[nbtTagList.size()];

            for (int i = 0; i < potions.length; i++)
                potions[i] = MobEffectInstance.load(nbtTagList.getCompound(i));

            return potions;
        }

        return null;
    }

    public static void writeNBTPotions(String id, MobEffectInstance[] potions, CompoundTag compound)
    {
        if (potions != null)
        {
            ListTag nbtTagList = new ListTag();

            for (MobEffectInstance p : potions)
                nbtTagList.add(p.save(new CompoundTag()));

            compound.put(id, nbtTagList);
        }
    }

    public static int[] readIntArrayFixedSize(String id, int length, CompoundTag compound)
    {
        int[] array = compound.getIntArray(id);
        return array.length != length ? new int[length] : array;
    }

    // Above, from IvToolkit
    // From Pandora's Box

    public static String[][] readNBTStrings2D(String id, CompoundTag compound)
    {
        ListTag nbtTagList = compound.getList(id, 10);
        String[][] strings = new String[nbtTagList.size()][];

        for (int i = 0; i < strings.length; i++)
            strings[i] = readNBTStrings("Strings", nbtTagList.getCompound(i));

        return strings;
    }

    public static void writeNBTStrings2D(String id, String[][] strings, CompoundTag compound)
    {
        ListTag nbtTagList = new ListTag();

        for (String[] s : strings)
        {
            CompoundTag compound1 = new CompoundTag();
            writeNBTStrings("Strings", s, compound1);
            nbtTagList.add(compound1);
        }

        compound.put(id, nbtTagList);
    }

    public static String storeBlockString(Block block)
    {
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
        return location.toString();
    }

    public static Block getBlock(String string)
    {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation(string));
    }
}

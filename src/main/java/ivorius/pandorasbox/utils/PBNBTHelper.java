package ivorius.pandorasbox.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

/**
 * Created by lukas on 03.02.15.
 */
public class PBNBTHelper {
    public static byte readByte(CompoundNBT compound, String key, byte defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_BYTE)
                ? compound.getByte(key)
                : defaultValue;
    }

    public static byte[] readByteArray(CompoundNBT compound, String key, byte[] defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_BYTE_ARRAY)
                ? compound.getByteArray(key)
                : defaultValue;
    }

    public static double readDouble(CompoundNBT compound, String key, double defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_DOUBLE)
                ? compound.getDouble(key)
                : defaultValue;
    }

    public static float readFloat(CompoundNBT compound, String key, float defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_FLOAT)
                ? compound.getFloat(key)
                : defaultValue;
    }

    public static int readInt(CompoundNBT compound, String key, int defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_INT)
                ? compound.getInt(key)
                : defaultValue;
    }

    public static int[] readIntArray(CompoundNBT compound, String key, int[] defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_INT_ARRAY)
                ? compound.getIntArray(key)
                : defaultValue;
    }

    public static long readLong(CompoundNBT compound, String key, long defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_LONG)
                ? compound.getLong(key)
                : defaultValue;
    }

    public static short readShort(CompoundNBT compound, String key, short defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_SHORT)
                ? compound.getShort(key)
                : defaultValue;
    }

    public static String readString(CompoundNBT compound, String key, String defaultValue) {
        return compound != null && compound.contains(key, Constants.NBT.TAG_STRING)
                ? compound.getString(key)
                : defaultValue;
    }

    public static double[] readDoubleArray(String key, CompoundNBT compound) {
        if (compound.contains(key)) {
            ListNBT list = compound.getList(key, Constants.NBT.TAG_DOUBLE);
            double[] array = new double[list.size()];

            for (int i = 0; i < array.length; i++)
                array[i] = list.getDouble(i);

            return array;
        }

        return null;
    }

    public static String[] readNBTStrings(String id, CompoundNBT compound) {
        if (compound.contains(id)) {
            ListNBT listNBT = compound.getList(id, Constants.NBT.TAG_STRING);
            String[] strings = new String[listNBT.size()];

            for (int i = 0; i < strings.length; i++)
                strings[i] = listNBT.getString(i);

            return strings;
        }

        return null;
    }

    public static void writeNBTStrings(String id, String[] strings, CompoundNBT compound) {
        if (strings != null) {
            ListNBT listNBT = new ListNBT();

            for (String s : strings)
                listNBT.add(StringNBT.valueOf(s));

            compound.put(id, listNBT);
        }
    }

    public static EntityType<?>[] readNBTEntities(String id, CompoundNBT compound) {
        if (compound.contains(id)) {
            ListNBT listNBT = compound.getList(id, 8);
            EntityType<?>[] entities = new EntityType<?>[listNBT.size()];

            for (int i = 0; i < entities.length; i++)
                entities[i] = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(listNBT.getString(i)));

            return entities;
        }

        return null;
    }

    public static void writeNBTEntities(String id, EntityType<?>[] entities, CompoundNBT compound) {
        if (entities != null) {
            ListNBT listNBT = new ListNBT();

            for (EntityType<?> e : entities)
                listNBT.add(StringNBT.valueOf(Objects.requireNonNull(ForgeRegistries.ENTITIES.getKey(e)).getPath()));

            compound.put(id, listNBT);
        }
    }

    public static ItemStack[] readNBTStacks(String id, CompoundNBT compound) {
        if (compound.contains(id)) {
            ListNBT listNBT = compound.getList(id, Constants.NBT.TAG_COMPOUND);
            ItemStack[] itemStacks = new ItemStack[listNBT.size()];
            for (int i = 0; i < itemStacks.length; i++)
                itemStacks[i] = ItemStack.of(listNBT.get(i) instanceof CompoundNBT ? (CompoundNBT) listNBT.get(i) : new CompoundNBT());

            return itemStacks;
        }

        return null;
    }

    public static void writeNBTStacks(String id, ItemStack[] stacks, CompoundNBT compound) {
        if (stacks != null) {
            ListNBT listNBT = new ListNBT();

            for (ItemStack stack : stacks) {
                CompoundNBT tagCompound = new CompoundNBT();
                stack.save(tagCompound);
                listNBT.add(tagCompound);
            }

            compound.put(id, listNBT);
        }
    }

    public static Block[] readNBTBlocks(String id, CompoundNBT compound) {
        if (compound.contains(id)) {
            ListNBT listNBT = compound.getList(id, Constants.NBT.TAG_STRING);
            Block[] blocks = new Block[listNBT.size()];

            for (int i = 0; i < blocks.length; i++)
                blocks[i] = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(listNBT.getString(i)));

            return blocks;
        }

        return null;
    }

    public static void writeNBTBlocks(String id, Block[] blocks, CompoundNBT compound) {
        if (blocks != null) {
            ListNBT listNBT = new ListNBT();

            for (Block b : blocks)
                listNBT.add(StringNBT.valueOf(PBNBTHelper.storeBlockString(b)));

            compound.put(id, listNBT);
        }
    }

    public static long[] readNBTLongs(String id, CompoundNBT compound) {
        if (compound.contains(id)) {
            ByteBuf bytes = Unpooled.copiedBuffer(compound.getByteArray(id));
            long[] longs = new long[bytes.capacity() / 8];
            for (int i = 0; i < longs.length; i++) longs[i] = bytes.readLong();
            return longs;
        }

        return null;
    }

    public static void writeNBTLongs(String id, long[] longs, CompoundNBT compound) {
        if (longs != null) {
            ByteBuf bytes = Unpooled.buffer(longs.length * 8);
            for (long aLong : longs) bytes.writeLong(aLong);
            compound.putByteArray(id, bytes.array());
        }
    }

    public static EffectInstance[] readNBTPotions(String id, CompoundNBT compound) {
        if (compound.contains(id)) {
            ListNBT listNBT = compound.getList(id, Constants.NBT.TAG_STRING);
            EffectInstance[] potions = new EffectInstance[listNBT.size()];

            for (int i = 0; i < potions.length; i++)
                potions[i] = EffectInstance.load(listNBT.getCompound(i));

            return potions;
        }

        return null;
    }

    public static void writeNBTPotions(String id, EffectInstance[] potions, CompoundNBT compound) {
        if (potions != null) {
            ListNBT listNBT = new ListNBT();

            for (EffectInstance p : potions)
                listNBT.add(p.save(new CompoundNBT()));

            compound.put(id, listNBT);
        }
    }

    public static int[] readIntArrayFixedSize(String id, int length, CompoundNBT compound) {
        int[] array = compound.getIntArray(id);
        return array.length != length ? new int[length] : array;
    }

    // Above, from IvToolkit
    // From Pandora's Box

    public static String[][] readNBTStrings2D(String id, CompoundNBT compound) {
        ListNBT listNBT = compound.getList(id, Constants.NBT.TAG_COMPOUND);
        String[][] strings = new String[listNBT.size()][];

        for (int i = 0; i < strings.length; i++)
            strings[i] = readNBTStrings("Strings", listNBT.getCompound(i));

        return strings;
    }

    public static void writeNBTStrings2D(String id, String[][] strings, CompoundNBT compound) {
        ListNBT listNBT = new ListNBT();

        for (String[] s : strings) {
            CompoundNBT compound1 = new CompoundNBT();
            writeNBTStrings("Strings", s, compound1);
            listNBT.add(compound1);
        }

        compound.put(id, listNBT);
    }

    public static String storeBlockString(Block block) {
        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
        return location == null ? "minecraft:air" : location.toString();
    }

    public static Block getBlock(String string) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(string));
    }

    public static WeightedBlock[] readNBTWeightedBlocks(String id, CompoundNBT compound) {
        if (compound.contains(id)) {
            ListNBT listNBT = compound.getList(id, 8);
            WeightedBlock[] blocks = new WeightedBlock[listNBT.size()];

            for (int i = 0; i < blocks.length; i++) {
                CompoundNBT compoundNBT = listNBT.getCompound(i);
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compoundNBT.getString("block")));
                double weight = compoundNBT.getDouble("weight");
                blocks[i] = new WeightedBlock(weight, block);
            }

            return blocks;
        }

        return null;
    }

    public static void writeNBTWeightedBlocks(String id, WeightedBlock[] blocks, CompoundNBT compound) {
        if (blocks != null) {
            ListNBT listNBT = new ListNBT();

            for (WeightedBlock b : blocks) {
                CompoundNBT compoundNBT = new CompoundNBT();
                compoundNBT.putString("block", PBNBTHelper.storeBlockString(b.block));
                compoundNBT.putDouble("weight", b.weight);
                listNBT.add(compoundNBT);
            }

            compound.put(id, listNBT);
        }
    }

    public static void writeNBTRandomizedStacks(String id, RandomizedItemStack[] stacks, CompoundNBT compound) {
        if (stacks != null) {
            ListNBT listNBT = new ListNBT();

            for (RandomizedItemStack stack : stacks) {
                CompoundNBT compoundNBT = new CompoundNBT();
                CompoundNBT stackNBT = new CompoundNBT();
                stack.itemStack.save(stackNBT);
                compoundNBT.put("stack", stackNBT);
                compoundNBT.putInt("min", stack.min);
                compoundNBT.putInt("max", stack.max);
                compoundNBT.putDouble("weight", stack.weight);
                listNBT.add(compoundNBT);
            }

            compound.put(id, listNBT);
        }
    }

    public static RandomizedItemStack[] readNBTRandomizedStacks(String id, CompoundNBT compound) {
        if (compound.contains(id)) {
            ListNBT listNBT = compound.getList(id, 10);
            RandomizedItemStack[] itemStacks = new RandomizedItemStack[listNBT.size()];
            for (int i = 0; i < itemStacks.length; i++) {
                CompoundNBT compoundNBT = listNBT.get(i) instanceof CompoundNBT ? (CompoundNBT) listNBT.get(i) : new CompoundNBT();
                ItemStack stack = ItemStack.of(compoundNBT.getCompound("stack"));
                int min = 1, max = 64;
                double weight = 0;
                if (compoundNBT.contains("min"))
                    min = compoundNBT.getInt("min");
                if (compoundNBT.contains("max"))
                    max = compoundNBT.getInt("max");
                if (compoundNBT.contains("weight"))
                    weight = compoundNBT.getInt("weight");
                itemStacks[i] = new RandomizedItemStack(stack, min, max, weight);
            }

            return itemStacks;
        }

        return null;
    }
}

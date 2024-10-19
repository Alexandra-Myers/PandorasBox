package ivorius.pandorasbox.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.core.RegistryAccess;
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
    public static byte readByte(CompoundTag compound, String key, byte defaultValue) {
        return compound != null && compound.contains(key, 1)
                ? compound.getByte(key)
                : defaultValue;
    }

    public static byte[] readByteArray(CompoundTag compound, String key, byte[] defaultValue) {
        return compound != null && compound.contains(key, 7)
                ? compound.getByteArray(key)
                : defaultValue;
    }

    public static double readDouble(CompoundTag compound, String key, double defaultValue) {
        return compound != null && compound.contains(key, 6)
                ? compound.getDouble(key)
                : defaultValue;
    }

    public static float readFloat(CompoundTag compound, String key, float defaultValue) {
        return compound != null && compound.contains(key, 5)
                ? compound.getFloat(key)
                : defaultValue;
    }

    public static int readInt(CompoundTag compound, String key, int defaultValue) {
        return compound != null && compound.contains(key, 3)
                ? compound.getInt(key)
                : defaultValue;
    }

    public static int[] readIntArray(CompoundTag compound, String key, int[] defaultValue) {
        return compound != null && compound.contains(key, 11)
                ? compound.getIntArray(key)
                : defaultValue;
    }

    public static long readLong(CompoundTag compound, String key, long defaultValue) {
        return compound != null && compound.contains(key, 4)
                ? compound.getLong(key)
                : defaultValue;
    }

    public static short readShort(CompoundTag compound, String key, short defaultValue) {
        return compound != null && compound.contains(key, 2)
                ? compound.getShort(key)
                : defaultValue;
    }

    public static String readString(CompoundTag compound, String key, String defaultValue) {
        return compound != null && compound.contains(key, 8)
                ? compound.getString(key)
                : defaultValue;
    }

    public static double[] readDoubleArray(String key, CompoundTag compound) {
        if (compound.contains(key)) {
            ListTag list = compound.getList(key, 6);
            double[] array = new double[list.size()];

            for (int i = 0; i < array.length; i++)
                array[i] = list.getDouble(i);

            return array;
        }

        return null;
    }

    public static String[] readNBTStrings(String id, CompoundTag compound) {
        if (compound.contains(id)) {
            ListTag listTag = compound.getList(id, 8);
            String[] strings = new String[listTag.size()];

            for (int i = 0; i < strings.length; i++)
                strings[i] = listTag.getString(i);

            return strings;
        }

        return null;
    }

    public static void writeNBTStrings(String id, String[] strings, CompoundTag compound) {
        if (strings != null) {
            ListTag listTag = new ListTag();

            for (String s : strings)
                listTag.add(StringTag.valueOf(s));

            compound.put(id, listTag);
        }
    }

    public static EntityType<?>[] readNBTEntities(String id, CompoundTag compound) {
        if (compound.contains(id)) {
            ListTag listTag = compound.getList(id, 8);
            EntityType<?>[] entities = new EntityType<?>[listTag.size()];

            for (int i = 0; i < entities.length; i++)
                entities[i] = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(listTag.getString(i)));

            return entities;
        }

        return null;
    }

    public static void writeNBTEntities(String id, EntityType<?>[] entities, CompoundTag compound) {
        if (entities != null) {
            ListTag listTag = new ListTag();

            for (EntityType<?> e : entities)
                listTag.add(StringTag.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(e).getPath()));

            compound.put(id, listTag);
        }
    }

    public static ItemStack[] readNBTStacks(String id, CompoundTag compound, RegistryAccess registryAccess) {
        if (compound.contains(id)) {
            ListTag listTag = compound.getList(id, 10);
            ItemStack[] itemStacks = new ItemStack[listTag.size()];
            for (int i = 0; i < itemStacks.length; i++)
                itemStacks[i] = ItemStack.parseOptional(registryAccess, listTag.get(i) instanceof CompoundTag ? (CompoundTag) listTag.get(i) : new CompoundTag());

            return itemStacks;
        }

        return null;
    }

    public static void writeNBTStacks(String id, ItemStack[] stacks, CompoundTag compound, RegistryAccess registryAccess) {
        if (stacks != null) {
            ListTag listTag = new ListTag();

            for (ItemStack stack : stacks) {
                if (stack.isEmpty()) continue;
                CompoundTag compoundTag = new CompoundTag();
                stack.save(registryAccess, compoundTag);
                listTag.add(compoundTag);
            }

            compound.put(id, listTag);
        }
    }

    public static Block[] readNBTBlocks(String id, CompoundTag compound) {
        if (compound.contains(id)) {
            ListTag listTag = compound.getList(id, 8);
            Block[] blocks = new Block[listTag.size()];

            for (int i = 0; i < blocks.length; i++)
                blocks[i] = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(listTag.getString(i)));

            return blocks;
        }

        return null;
    }

    public static void writeNBTBlocks(String id, Block[] blocks, CompoundTag compound) {
        if (blocks != null) {
            ListTag listTag = new ListTag();

            for (Block b : blocks)
                listTag.add(StringTag.valueOf(PBNBTHelper.storeBlockString(b)));

            compound.put(id, listTag);
        }
    }

    public static long[] readNBTLongs(String id, CompoundTag compound) {
        if (compound.contains(id)) {
            ByteBuf bytes = Unpooled.copiedBuffer(compound.getByteArray(id));
            long[] longs = new long[bytes.capacity() / 8];
            for (int i = 0; i < longs.length; i++) longs[i] = bytes.readLong();
            return longs;
        }

        return null;
    }

    public static void writeNBTLongs(String id, long[] longs, CompoundTag compound) {
        if (longs != null) {
            ByteBuf bytes = Unpooled.buffer(longs.length * 8);
            for (long aLong : longs) bytes.writeLong(aLong);
            compound.putByteArray(id, bytes.array());
        }
    }

    public static MobEffectInstance[] readNBTPotions(String id, CompoundTag compound) {
        if (compound.contains(id)) {
            ListTag listTag = compound.getList(id, 8);
            MobEffectInstance[] potions = new MobEffectInstance[listTag.size()];

            for (int i = 0; i < potions.length; i++)
                potions[i] = MobEffectInstance.load(listTag.getCompound(i));

            return potions;
        }

        return null;
    }

    public static void writeNBTPotions(String id, MobEffectInstance[] potions, CompoundTag compound) {
        if (potions != null) {
            ListTag listTag = new ListTag();

            for (MobEffectInstance p : potions)
                listTag.add(p.save());

            compound.put(id, listTag);
        }
    }

    public static int[] readIntArrayFixedSize(String id, int length, CompoundTag compound) {
        int[] array = compound.getIntArray(id);
        return array.length != length ? new int[length] : array;
    }

    // Above, from IvToolkit
    // From Pandora's Box

    public static String[][] readNBTStrings2D(String id, CompoundTag compound) {
        ListTag listTag = compound.getList(id, 10);
        String[][] strings = new String[listTag.size()][];

        for (int i = 0; i < strings.length; i++)
            strings[i] = readNBTStrings("Strings", listTag.getCompound(i));

        return strings;
    }

    public static void writeNBTStrings2D(String id, String[][] strings, CompoundTag compound) {
        ListTag listTag = new ListTag();

        for (String[] s : strings) {
            CompoundTag compound1 = new CompoundTag();
            writeNBTStrings("Strings", s, compound1);
            listTag.add(compound1);
        }

        compound.put(id, listTag);
    }

    public static String storeBlockString(Block block) {
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
        return location.toString();
    }

    public static Block getBlock(String string) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(string));
    }

    public static WeightedBlock[] readNBTWeightedBlocks(String id, CompoundTag compound) {
        if (compound.contains(id)) {
            ListTag listTag = compound.getList(id, 8);
            WeightedBlock[] blocks = new WeightedBlock[listTag.size()];

            for (int i = 0; i < blocks.length; i++) {
                CompoundTag compoundNBT = listTag.getCompound(i);
                Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(compoundNBT.getString("block")));
                double weight = compoundNBT.getDouble("weight");
                blocks[i] = new WeightedBlock(weight, block);
            }

            return blocks;
        }

        return null;
    }

    public static void writeNBTWeightedBlocks(String id, WeightedBlock[] blocks, CompoundTag compound) {
        if (blocks != null) {
            ListTag listTag = new ListTag();

            for (WeightedBlock b : blocks) {
                CompoundTag compoundNBT = new CompoundTag();
                compoundNBT.putString("block", PBNBTHelper.storeBlockString(b.block));
                compoundNBT.putDouble("weight", b.weight);
                listTag.add(compoundNBT);
            }

            compound.put(id, listTag);
        }
    }

    public static void writeNBTRandomizedStacks(String id, RandomizedItemStack[] stacks, CompoundTag compound, RegistryAccess registryAccess) {
        if (stacks != null) {
            ListTag listTag = new ListTag();

            for (RandomizedItemStack stack : stacks) {
                CompoundTag compoundTag = new CompoundTag();
                CompoundTag stackTag = new CompoundTag();
                if (stack.itemStack.isEmpty()) continue;
                stack.itemStack.save(registryAccess, stackTag);
                compoundTag.put("stack", stackTag);
                compoundTag.putInt("min", stack.min);
                compoundTag.putInt("max", stack.max);
                compoundTag.putDouble("weight", stack.weight);
                listTag.add(compoundTag);
            }

            compound.put(id, listTag);
        }
    }

    public static RandomizedItemStack[] readNBTRandomizedStacks(String id, CompoundTag compound, RegistryAccess registryAccess) {
        if (compound.contains(id)) {
            ListTag listTag = compound.getList(id, 10);
            RandomizedItemStack[] itemStacks = new RandomizedItemStack[listTag.size()];
            for (int i = 0; i < itemStacks.length; i++) {
                CompoundTag compoundTag = listTag.get(i) instanceof CompoundTag ? (CompoundTag) listTag.get(i) : new CompoundTag();
                ItemStack stack = ItemStack.parseOptional(registryAccess, compoundTag.getCompound("stack"));
                int min = 1, max = 64;
                double weight = 0;
                if (compoundTag.contains("min"))
                    min = compoundTag.getInt("min");
                if (compoundTag.contains("max"))
                    max = compoundTag.getInt("max");
                if (compoundTag.contains("weight"))
                    weight = compoundTag.getInt("weight");
                itemStacks[i] = new RandomizedItemStack(stack, min, max, weight);
            }

            return itemStacks;
        }

        return null;
    }
}

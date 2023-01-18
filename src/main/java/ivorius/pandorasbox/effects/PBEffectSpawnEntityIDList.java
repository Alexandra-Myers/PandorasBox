/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.random.PandorasBoxEntityNamer;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.utils.StringConverter;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectSpawnEntityIDList extends PBEffectSpawnEntities
{
    public String[][] entityIDs;

    public int nameEntities;
    public int equipLevel;
    public int buffLevel;

    public PBEffectSpawnEntityIDList(int time, String[] entityIDs, int nameEntities, int equipLevel, int buffLevel)
    {
        super(time, entityIDs.length);
        this.entityIDs = get2DStringArray(entityIDs);

        this.nameEntities = nameEntities;
        this.equipLevel = equipLevel;
        this.buffLevel = buffLevel;
    }

    public PBEffectSpawnEntityIDList(int time, String[][] entityIDs, int nameEntities, int equipLevel, int buffLevel)
    {
        super(time, entityIDs.length);
        this.entityIDs = entityIDs;

        this.nameEntities = nameEntities;
        this.equipLevel = equipLevel;
        this.buffLevel = buffLevel;
    }

    private String[][] get2DStringArray(String[] strings)
    {
        String[][] result = new String[strings.length][1];
        for (int i = 0; i < strings.length; i++)
        {
            result[i][0] = strings[i];
        }
        return result;
    }

    @Override
    public Entity spawnEntity(World world, PandorasBoxEntity pbEntity, Random random, int number, double x, double y, double z)
    {
        if(world.isClientSide()) return null;
        String[] entityTower = entityIDs[number];
        Entity previousEntity = null;

        for (String entityID : entityTower)
        {
            Entity newEntity = createEntity(world, pbEntity, random, entityID, x, y, z);

            if (newEntity instanceof LivingEntity)
            {
                randomizeEntity(random, pbEntity.getId(), (LivingEntity) newEntity, nameEntities, equipLevel, buffLevel);
            }

            if (previousEntity != null)
            {
                world.addFreshEntity(previousEntity);
                assert newEntity != null;
                previousEntity.startRiding(newEntity, true);
            }

            previousEntity = newEntity;
        }

        if (previousEntity != null)
            world.addFreshEntity(previousEntity);

        return previousEntity;
    }

    public static void randomizeEntity(Random random, long namingSeed, LivingEntity entityLiving, int nameEntities, int equipLevel, int buffLevel)
    {
        if (nameEntities == 1)
        {
            entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomName(random));
            entityLiving.setCustomNameVisible(true);
        }
        else if (nameEntities == 2)
        {
            entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(random));
        }
        else if (nameEntities == 3)
        {
            entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(new Random(namingSeed)));
        }

        if (equipLevel > 0)
        {
            float itemChancePerSlot = 1.0f - (0.5f / equipLevel);
            float upgradeChancePerSlot = 1.0f - (1.0f / equipLevel);

            for (int i = 0; i < 5; i++)
            {
                if (random.nextFloat() < itemChancePerSlot)
                {
                    int itemLevel = 0;
                    while (random.nextFloat() < upgradeChancePerSlot && itemLevel < equipLevel)
                    {
                        itemLevel++;
                    }

                    if (i == 0)
                    {
                        ItemStack itemStack = PandorasBoxHelper.getRandomWeaponItemForLevel(random, itemLevel);
                        if(itemStack == null) itemStack = ItemStack.EMPTY;

                        entityLiving.setItemSlot(EquipmentSlotType.MAINHAND, itemStack);
                    }
                    else
                    {
                        if (i == 4 && random.nextFloat() < 0.2f / equipLevel)
                        {
                            entityLiving.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
                        }
                        else
                        {
                            EquipmentSlotType slot = i == 1 ? EquipmentSlotType.LEGS : i == 2 ? EquipmentSlotType.FEET : EquipmentSlotType.CHEST;
                            Item item = MobEntity.getEquipmentForSlot(slot, Math.min(itemLevel, 4));

                            if (item != null)
                            {
                                entityLiving.setItemSlot(slot, new ItemStack(item));
                            }
                            else
                            {
                                System.err.println("Pandora's Box: Item not found for slot '" + slot + "', level '" + itemLevel + "'");
                            }
                        }
                    }
                }
            }
        }

        if (buffLevel > 0)
        {
            ModifiableAttributeInstance health = entityLiving.getAttribute(Attributes.MAX_HEALTH);
            if (health != null)
            {
                double healthMultiplierP = random.nextDouble() * buffLevel * 0.25;
                health.addPermanentModifier(new AttributeModifier("Zeus's magic", healthMultiplierP, AttributeModifier.Operation.fromValue(1)));
            }

            ModifiableAttributeInstance knockbackResistance = entityLiving.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (knockbackResistance != null)
            {
                double knockbackResistanceP = random.nextDouble() * buffLevel * 0.25;
                knockbackResistance.addPermanentModifier(new AttributeModifier("Zeus's magic", knockbackResistanceP, AttributeModifier.Operation.fromValue(1)));
            }

            ModifiableAttributeInstance movementSpeed = entityLiving.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null)
            {
                double movementSpeedP = random.nextDouble() * buffLevel * 0.08;
                movementSpeed.addPermanentModifier(new AttributeModifier("Zeus's magic", movementSpeedP, AttributeModifier.Operation.fromValue(1)));
            }

            ModifiableAttributeInstance attackDamage = entityLiving.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null)
            {
                double attackDamageP = random.nextDouble() * buffLevel * 0.25;
                attackDamage.addPermanentModifier(new AttributeModifier("Zeus's magic", attackDamageP, AttributeModifier.Operation.fromValue(1)));
            }
        }
    }

    public static Entity createEntity(World world, PandorasBoxEntity pbEntity, Random random, String entityID, double x, double y, double z)
    {
        try
        {
            if ("pbspecial_XP".equals(entityID))
            {
                ExperienceOrbEntity entity = EntityType.EXPERIENCE_ORB.create(world);
                assert entity != null;
                entity.value = 10;
                return entity;
            }
            else if ("pbspecial_wolfTamed".equals(entityID))
            {
                PlayerEntity nearest = getPlayer(world, pbEntity);
                WolfEntity wolf = EntityType.WOLF.create(world);

                assert wolf != null;
                wolf.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

                if (nearest != null)
                {
                    wolf.setTame(true);
                    wolf.getNavigation().stop();
                    wolf.setTarget(null);
                    wolf.setOwnerUUID(nearest.getUUID());
                    wolf.level.broadcastEntityEvent(wolf, (byte) 7);
                }

                return wolf;
            }
            else if ("pbspecial_ocelotTamed".equals(entityID))
            {
                PlayerEntity nearest = getPlayer(world, pbEntity);

                CatEntity ocelot = EntityType.CAT.create(world);

                assert ocelot != null;
                ocelot.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

                if (nearest != null)
                {
                    ocelot.setTame(true);
                    ocelot.setCatType(1 + ocelot.level.random.nextInt(10));
                    ocelot.setOwnerUUID(nearest.getUUID());
                    ocelot.level.broadcastEntityEvent(ocelot, (byte) 7);
                }

                return ocelot;
            }
            else if (entityID.startsWith("pbspecial_tnt"))
            {
                TNTEntity entitytntprimed = EntityType.TNT.create(world);
                assert entitytntprimed != null;
                entitytntprimed.setPos(x, y, z);
                entitytntprimed.setFuse(Integer.parseInt(entityID.substring(13)));

                return entitytntprimed;
            }
            else if ("pbspecial_invisibleTnt".startsWith(entityID))
            {
                TNTEntity entitytntprimed = EntityType.TNT.create(world);
                assert entitytntprimed != null;
                entitytntprimed.setPos(x, y, z);
                entitytntprimed.setFuse(Integer.parseInt(entityID.substring(22)));
                entitytntprimed.setInvisible(true);

                return entitytntprimed;
            }
            else if ("pbspecial_firework".equals(entityID))
            {
                ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
                stack.addTagElement("Fireworks", createRandomFirework(random));

                FireworkRocketEntity fireworkRocket = EntityType.FIREWORK_ROCKET.create(world);
                assert fireworkRocket != null;
                fireworkRocket.setPos(x, y, z);
                fireworkRocket.getEntityData().set(fireworkStackParameter(), stack);
                return fireworkRocket;
            }
            else if ("pbspecial_angryWolf".equals(entityID))
            {
                WolfEntity wolf = EntityType.WOLF.create(world);
                assert wolf != null;
                wolf.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                wolf.setTarget(world.getNearestPlayer(x, y, z, 40.0, false));

                return wolf;
            }
            else if ("pbspecial_superchargedCreeper".equals(entityID))
            {
                CreeperEntity creeper = EntityType.CREEPER.create(world);
                assert creeper != null;
                creeper.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                creeper.getEntityData().set(creeperPoweredParameter(), true);
                return creeper;
            }
            else if ("pbspecial_skeletonWither".equals(entityID))
            {
                WitherSkeletonEntity skeleton = EntityType.WITHER_SKELETON.create(world);
                assert skeleton != null;
                skeleton.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

                skeleton.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.STONE_SWORD));
                skeleton.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);

                return skeleton;
            }
            else if ("pbspecial_elderGuardian".equals(entityID))
            {
                ElderGuardianEntity entity = EntityType.ELDER_GUARDIAN.create(world);
                assert entity != null;
                entity.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

                return entity;
            }
            entityID = StringConverter.convertCamelCase(entityID);

            EntityType<?> entity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityID));
            Entity entity1 = entity.create(world);
            entity1.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

            return entity1;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    private static DataParameter<Boolean> creeperPoweredParameter() throws IllegalAccessException
    {
        return (DataParameter<Boolean>) ObfuscationReflectionHelper.findField(CreeperEntity.class, "field_184714_b").get(null);
    }
    private static DataParameter<ItemStack> fireworkStackParameter() throws IllegalAccessException
    {
        return (DataParameter<ItemStack>) ObfuscationReflectionHelper.findField(FireworkRocketEntity.class, "field_184566_a").get(null);
    }

    public static CompoundNBT createRandomFirework(Random random)
    {
        CompoundNBT compound = new CompoundNBT();
        compound.put("Explosions", createRandomFireworkExplosions(random, (random.nextInt(20)) != 0 ? 1 : (1 + random.nextInt(2))));
        compound.putByte("Flight", (byte) ((random.nextInt(15) != 0) ? 1 : (2 + random.nextInt(2))));
        return compound;
    }

    public static ListNBT createRandomFireworkExplosions(Random random, int number)
    {
        ListNBT list = new ListNBT();

        for (int i = 0; i < number; i++)
        {
            list.add(createRandomFireworkExplosion(random));
        }

        return list;
    }

    public static CompoundNBT createRandomFireworkExplosion(Random random)
    {
        CompoundNBT fireworkCompound = new CompoundNBT();

        fireworkCompound.putBoolean("Flicker", random.nextInt(20) == 0);
        fireworkCompound.putBoolean("Trail", random.nextInt(30) == 0);
        fireworkCompound.putByte("Type", (byte) ((random.nextInt(10) != 0) ? 0 : (random.nextInt(4) + 1)));

        int[] colors = new int[(random.nextInt(15) != 0) ? 1 : (random.nextInt(2) + 2)];
        for (int i = 0; i < colors.length; i++)
        {
            colors[i] = DyeColor.byId(random.nextInt(16)).ordinal();
        }
        fireworkCompound.putIntArray("Colors", colors);

        if (random.nextInt(25) == 0)
        {
            int[] fadeColors = new int[random.nextInt(2) + 1];
            for (int i = 0; i < fadeColors.length; i++)
            {
                fadeColors[i] = DyeColor.byId(random.nextInt(16)).ordinal();
            }
            fireworkCompound.putIntArray("FadeColors", fadeColors);
        }

        return fireworkCompound;
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTStrings2D("entityIDs", entityIDs, compound);

        compound.putInt("nameEntities", nameEntities);
        compound.putInt("equipLevel", equipLevel);
        compound.putInt("buffLevel", buffLevel);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        entityIDs = PBNBTHelper.readNBTStrings2D("entityIDs", compound);

        nameEntities = compound.getInt("nameEntities");
        equipLevel = compound.getInt("equipLevel");
        buffLevel = compound.getInt("buffLevel");
    }
}

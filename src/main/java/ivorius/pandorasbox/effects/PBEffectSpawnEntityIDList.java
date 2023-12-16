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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.event.EventHooks;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectSpawnEntityIDList extends PBEffectSpawnEntities
{
    public String[][] entityIDs;

    public int nameEntities;
    public int equipLevel;
    public int buffLevel;
    public PBEffectSpawnEntityIDList() {}

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
    public Entity spawnEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, int number, double x, double y, double z)
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

        if (previousEntity != null) {
            world.addFreshEntity(previousEntity);
        }

        return previousEntity;
    }

    public static void randomizeEntity(RandomSource random, long namingSeed, LivingEntity entityLiving, int nameEntities, int equipLevel, int buffLevel)
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
            entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(RandomSource.create(namingSeed)));
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

                        entityLiving.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
                    }
                    else
                    {
                        if (i == 4 && random.nextFloat() < 0.2f / equipLevel)
                        {
                            entityLiving.setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
                        }
                        else
                        {
                            EquipmentSlot slot = i == 1 ? EquipmentSlot.LEGS : i == 2 ? EquipmentSlot.FEET : EquipmentSlot.CHEST;
                            Item item = Mob.getEquipmentForSlot(slot, Math.min(itemLevel, 4));

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
            AttributeInstance health = entityLiving.getAttribute(Attributes.MAX_HEALTH);
            if (health != null)
            {
                double healthMultiplierP = random.nextDouble() * buffLevel * 0.25;
                health.addPermanentModifier(new AttributeModifier("Zeus's magic", healthMultiplierP, AttributeModifier.Operation.fromValue(1)));
            }

            AttributeInstance knockbackResistance = entityLiving.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (knockbackResistance != null)
            {
                double knockbackResistanceP = random.nextDouble() * buffLevel * 0.25;
                knockbackResistance.addPermanentModifier(new AttributeModifier("Zeus's magic", knockbackResistanceP, AttributeModifier.Operation.fromValue(1)));
            }

            AttributeInstance movementSpeed = entityLiving.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null)
            {
                double movementSpeedP = random.nextDouble() * buffLevel * 0.08;
                movementSpeed.addPermanentModifier(new AttributeModifier("Zeus's magic", movementSpeedP, AttributeModifier.Operation.fromValue(1)));
            }

            AttributeInstance attackDamage = entityLiving.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null)
            {
                double attackDamageP = random.nextDouble() * buffLevel * 0.25;
                attackDamage.addPermanentModifier(new AttributeModifier("Zeus's magic", attackDamageP, AttributeModifier.Operation.fromValue(1)));
            }
        }
    }

    public static Entity createEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, String entityID, double x, double y, double z)
    {
        try
        {
            if ("pbspecial_XP".equals(entityID))
            {
                ExperienceOrb entity = EntityType.EXPERIENCE_ORB.create(world);
                assert entity != null;
                entity.value = 10;
                return entity;
            }
            else if ("pbspecial_wolfTamed".equals(entityID))
            {
                Player nearest = getPlayer(world, pbEntity);
                Wolf wolf = EntityType.WOLF.create(world);

                assert wolf != null;
                wolf.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

                if (nearest != null)
                {
                    EventHooks.onFinalizeSpawn(wolf, (ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), MobSpawnType.NATURAL, null, null);
                    wolf.getNavigation().stop();
                    wolf.setTarget(null);
                    wolf.tame(nearest);
                    wolf.level().broadcastEntityEvent(wolf, (byte) 7);
                }

                return wolf;
            }
            else if ("pbspecial_ocelotTamed".equals(entityID))
            {
                Player nearest = getPlayer(world, pbEntity);

                Cat ocelot = EntityType.CAT.create(world);

                assert ocelot != null;
                ocelot.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

                if (nearest != null)
                {
                    EventHooks.onFinalizeSpawn(ocelot, (ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), MobSpawnType.NATURAL, null, null);
                    ocelot.tame(nearest);
                    world.broadcastEntityEvent(ocelot, (byte) 7);
                }

                return ocelot;
            }
            else if (entityID.startsWith("pbspecial_tnt"))
            {
                PrimedTnt entitytntprimed = EntityType.TNT.create(world);
                assert entitytntprimed != null;
                entitytntprimed.setPos(x, y, z);
                entitytntprimed.setFuse(Integer.parseInt(entityID.substring(13)));

                return entitytntprimed;
            }
            else if ("pbspecial_invisibleTnt".startsWith(entityID))
            {
                PrimedTnt entitytntprimed = EntityType.TNT.create(world);
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
                fireworkRocket.getEntityData().set(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM, stack);
                return fireworkRocket;
            }
            else if ("pbspecial_angryWolf".equals(entityID))
            {
                Wolf wolf = EntityType.WOLF.create(world);
                assert wolf != null;
                EventHooks.onFinalizeSpawn(wolf, (ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), MobSpawnType.NATURAL, null, null);
                wolf.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                wolf.setTarget(world.getNearestPlayer(x, y, z, 40.0, false));

                return wolf;
            }
            else if ("pbspecial_superchargedCreeper".equals(entityID))
            {
                Creeper creeper = EntityType.CREEPER.create(world);
                assert creeper != null;
                EventHooks.onFinalizeSpawn(creeper, (ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), MobSpawnType.NATURAL, null, null);
                creeper.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
                return creeper;
            }
            else if ("pbspecial_skeletonWither".equals(entityID))
            {
                WitherSkeleton skeleton = EntityType.WITHER_SKELETON.create(world);
                assert skeleton != null;
                EventHooks.onFinalizeSpawn(skeleton, (ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), MobSpawnType.NATURAL, null, null);
                skeleton.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

                skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                skeleton.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);

                return skeleton;
            }
            else if ("pbspecial_elderGuardian".equals(entityID))
            {
                ElderGuardian entity = EntityType.ELDER_GUARDIAN.create(world);
                assert entity != null;
                EventHooks.onFinalizeSpawn(entity, (ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), MobSpawnType.NATURAL, null, null);
                entity.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);

                return entity;
            }
            entityID = StringConverter.convertCamelCase(entityID);

            EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(entityID));
            Entity entity1 = entity.create(world);
            assert entity1 != null;
            entity1.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
            if(entity1 instanceof AbstractPiglin piglin) {
                piglin.setImmuneToZombification(true);
            }
            if(entity1 instanceof Hoglin hoglin)
                hoglin.setImmuneToZombification(true);
            if (entity1 instanceof Mob mob)
                EventHooks.onFinalizeSpawn(mob, (ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), MobSpawnType.NATURAL, null, null);

            return entity1;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static CompoundTag createRandomFirework(RandomSource random)
    {
        CompoundTag compound = new CompoundTag();
        compound.put("Explosions", createRandomFireworkExplosions(random, (random.nextInt(20)) != 0 ? 1 : (1 + random.nextInt(2))));
        compound.putByte("Flight", (byte) ((random.nextInt(15) != 0) ? 1 : (2 + random.nextInt(2))));
        return compound;
    }

    public static ListTag createRandomFireworkExplosions(RandomSource random, int number)
    {
        ListTag list = new ListTag();

        for (int i = 0; i < number; i++)
        {
            list.add(createRandomFireworkExplosion(random));
        }

        return list;
    }

    public static CompoundTag createRandomFireworkExplosion(RandomSource random)
    {
        CompoundTag fireworkCompound = new CompoundTag();

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
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTStrings2D("entityIDs", entityIDs, compound);

        compound.putInt("nameEntities", nameEntities);
        compound.putInt("equipLevel", equipLevel);
        compound.putInt("buffLevel", buffLevel);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        entityIDs = PBNBTHelper.readNBTStrings2D("entityIDs", compound);

        nameEntities = compound.getInt("nameEntities");
        equipLevel = compound.getInt("equipLevel");
        buffLevel = compound.getInt("buffLevel");
    }
}

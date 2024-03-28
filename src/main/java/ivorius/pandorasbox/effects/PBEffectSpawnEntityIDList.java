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
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectSpawnEntityIDList extends PBEffectSpawnEntities {
    public String[][] entityIDs;

    public int nameEntities;
    public int equipLevel;
    public int buffLevel;
    public PBEffectSpawnEntityIDList() {}

    public PBEffectSpawnEntityIDList(int time, String[] entityIDs, int nameEntities, int equipLevel, int buffLevel) {
        super(time, entityIDs.length);
        this.entityIDs = get2DStringArray(entityIDs);

        this.nameEntities = nameEntities;
        this.equipLevel = equipLevel;
        this.buffLevel = buffLevel;
    }

    public PBEffectSpawnEntityIDList(int time, String[][] entityIDs, int nameEntities, int equipLevel, int buffLevel) {
        super(time, entityIDs.length);
        this.entityIDs = entityIDs;

        this.nameEntities = nameEntities;
        this.equipLevel = equipLevel;
        this.buffLevel = buffLevel;
    }

    private String[][] get2DStringArray(String[] strings) {
        String[][] result = new String[strings.length][1];
        for (int i = 0; i < strings.length; i++) {
            result[i][0] = strings[i];
        }
        return result;
    }

    @Override
    public Entity spawnEntity(World world, PandorasBoxEntity pbEntity, Random random, int number, double x, double y, double z) {
        if(world.isClientSide()) return null;
        String[] entityTower = entityIDs[number];
        Entity previousEntity = null;

        for (String entityID : entityTower) {
            Entity newEntity = createEntity(world, pbEntity, random, entityID, x, y, z);

            if (newEntity instanceof LivingEntity)
                randomizeEntity(random, pbEntity.getId(), (LivingEntity) newEntity, nameEntities, equipLevel, buffLevel);

            if (previousEntity != null) {
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

    public static void randomizeEntity(Random random, long namingSeed, LivingEntity entityLiving, int nameEntities, int equipLevel, int buffLevel) {
        if (nameEntities == 1) {
            entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomName(random));
            entityLiving.setCustomNameVisible(true);
        } else if (nameEntities == 2)
            entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(random));
        else if (nameEntities == 3)
            entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(new Random(namingSeed)));

        if (equipLevel > 0) {
            float itemChancePerSlot = 1.0f - (0.5f / equipLevel);
            float upgradeChancePerSlot = 1.0f - (1.0f / equipLevel);

            for (int i = 0; i < 5; i++) {
                if (random.nextFloat() < itemChancePerSlot) {
                    int itemLevel = 0;
                    while (random.nextFloat() < upgradeChancePerSlot && itemLevel < equipLevel) {
                        itemLevel++;
                    }

                    if (i == 0) {
                        ItemStack itemStack = PandorasBoxHelper.getRandomWeaponItemForLevel(random, itemLevel);
                        if(itemStack == null) itemStack = ItemStack.EMPTY;

                        entityLiving.setItemSlot(EquipmentSlotType.MAINHAND, itemStack);
                    } else {
                        if (i == 4 && random.nextFloat() < 0.2f / equipLevel) {
                            entityLiving.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                        } else {
                            EquipmentSlotType slot = i == 1 ? EquipmentSlotType.LEGS : i == 2 ? EquipmentSlotType.FEET : EquipmentSlotType.CHEST;
                            Item item = MobEntity.getEquipmentForSlot(slot, Math.min(itemLevel, 4));

                            if (item != null) entityLiving.setItemSlot(slot, new ItemStack(item));
                            else System.err.println("Pandora's Box: Item not found for slot '" + slot + "', level '" + itemLevel + "'");
                        }
                    }
                }
            }
        }

        if (buffLevel > 0) {
            ModifiableAttributeInstance health = entityLiving.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
                double healthMultiplierP = random.nextDouble() * buffLevel * 0.25;
                health.addPermanentModifier(new AttributeModifier("Zeus's magic", healthMultiplierP, AttributeModifier.Operation.fromValue(1)));
            }

            ModifiableAttributeInstance knockbackResistance = entityLiving.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (knockbackResistance != null) {
                double knockbackResistanceP = random.nextDouble() * buffLevel * 0.25;
                knockbackResistance.addPermanentModifier(new AttributeModifier("Zeus's magic", knockbackResistanceP, AttributeModifier.Operation.fromValue(1)));
            }

            ModifiableAttributeInstance movementSpeed = entityLiving.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                double movementSpeedP = random.nextDouble() * buffLevel * 0.08;
                movementSpeed.addPermanentModifier(new AttributeModifier("Zeus's magic", movementSpeedP, AttributeModifier.Operation.fromValue(1)));
            }

            ModifiableAttributeInstance attackDamage = entityLiving.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null) {
                double attackDamageP = random.nextDouble() * buffLevel * 0.25;
                attackDamage.addPermanentModifier(new AttributeModifier("Zeus's magic", attackDamageP, AttributeModifier.Operation.fromValue(1)));
            }
        }
    }

    public static Entity createEntity(World world, PandorasBoxEntity pbEntity, Random random, String entityID, double x, double y, double z) {
        try {
            if ("pbspecial_experience".equals(entityID)) {
                return new ExperienceOrbEntity(world, x, y, z, 10);
            } else if ("pbspecial_wolf_tamed".equals(entityID)) {
                PlayerEntity owner = getPlayer(world, pbEntity);
                WolfEntity wolf = EntityType.WOLF.create(world);

                assert wolf != null;
                wolf.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                wolf.finalizeSpawn((ServerWorld)world, world.getCurrentDifficultyAt(new BlockPos(x,y, z)), SpawnReason.COMMAND, null, null);

                if (owner != null) {
                    wolf.tame(owner);
                    wolf.getNavigation().stop();
                    wolf.setTarget(null);
                    wolf.level.broadcastEntityEvent(wolf, (byte) 7);
                }

                return wolf;
            } else if ("pbspecial_cat_tamed".equals(entityID)) {
                PlayerEntity owner = getPlayer(world, pbEntity);

                CatEntity cat = EntityType.CAT.create(world);

                assert cat != null;
                cat.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                cat.finalizeSpawn((ServerWorld)world, world.getCurrentDifficultyAt(new BlockPos(x,y, z)), SpawnReason.COMMAND, null, null);

                if (owner != null) {
                    cat.tame(owner);
                    cat.level.broadcastEntityEvent(cat, (byte) 7);
                }

                return cat;
            } else if ("pbspecial_parrot_tamed".equals(entityID)) {
                PlayerEntity owner = getPlayer(world, pbEntity);

                ParrotEntity parrot = EntityType.PARROT.create(world);

                assert parrot != null;
                parrot.setVariant(random.nextInt(5));
                parrot.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                parrot.finalizeSpawn((ServerWorld)world, world.getCurrentDifficultyAt(new BlockPos(x,y, z)), SpawnReason.COMMAND, null, null);

                if (owner != null) {
                    parrot.tame(owner);
                    world.broadcastEntityEvent(parrot, (byte) 7);
                }

                return parrot;
            } else if (entityID.startsWith("pbspecial_tnt")) {
                TNTEntity primedTnt = new TNTEntity(world, x, y, z, getPlayer(world, pbEntity));
                primedTnt.setFuse(Integer.parseInt(entityID.substring(13)));

                return primedTnt;
            } else if (entityID.startsWith("pbspecial_invisible_tnt")) {
                TNTEntity primedTnt = new TNTEntity(world, x, y, z, getPlayer(world, pbEntity));
                primedTnt.setFuse(Integer.parseInt(entityID.substring(22)));
                primedTnt.setInvisible(true);

                return primedTnt;
            } else if ("pbspecial_fireworks".equals(entityID)) {
                ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
                stack.addTagElement("Fireworks", createRandomFirework(random));

                return new FireworkRocketEntity(world, x, y, z, stack);
            } else if ("pbspecial_angry_wolf".equals(entityID)) {
                WolfEntity wolf = EntityType.WOLF.create(world);
                assert wolf != null;
                wolf.finalizeSpawn((ServerWorld)world, world.getCurrentDifficultyAt(new BlockPos(x,y, z)), SpawnReason.COMMAND, null, null);
                wolf.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                wolf.setTarget(world.getNearestPlayer(x, y, z, 40.0, false));

                return wolf;
            } else if ("pbspecial_charged_creeper".equals(entityID)) {
                CreeperEntity creeper = EntityType.CREEPER.create(world);
                assert creeper != null;
                creeper.finalizeSpawn((ServerWorld)world, world.getCurrentDifficultyAt(new BlockPos(x,y, z)), SpawnReason.COMMAND, null, null);
                creeper.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
                creeper.getEntityData().set(CreeperEntity.DATA_IS_POWERED, true);
                return creeper;
            }
            entityID = StringConverter.convertCamelCase(entityID);

            EntityType<?> entity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityID));
            assert entity != null;
            Entity entity1 = entity.create(world);
            assert entity1 != null;
            entity1.moveTo(x, y, z, random.nextFloat() * 360.0f, 0.0f);
            PlayerEntity owner = getPlayer(world, pbEntity);
            if (owner != null && entity1.getY() - owner.getY() > entity.clientTrackingRange() * 16)
                entity1.setPos(entity1.getX(), owner.getY() + entity.clientTrackingRange() * 16 - 1, entity1.getZ());
            if (entity1 instanceof AbstractPiglinEntity)
                ((AbstractPiglinEntity)entity1).setImmuneToZombification(true);
            if (entity1 instanceof HoglinEntity)
                ((HoglinEntity)entity1).setImmuneToZombification(true);
            if (entity1 instanceof MobEntity)
                ((MobEntity) entity1).finalizeSpawn((ServerWorld)world, world.getCurrentDifficultyAt(new BlockPos(x,y, z)), SpawnReason.COMMAND, null, null);

            return entity1;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static CompoundNBT createRandomFirework(Random random) {
        CompoundNBT compound = new CompoundNBT();
        compound.put("Explosions", createRandomFireworkExplosions(random, (random.nextInt(20)) != 0 ? 1 : (1 + random.nextInt(2))));
        compound.putByte("Flight", (byte) ((random.nextInt(15) != 0) ? 1 : (2 + random.nextInt(2))));
        return compound;
    }

    public static ListNBT createRandomFireworkExplosions(Random random, int number) {
        ListNBT list = new ListNBT();

        for (int i = 0; i < number; i++) {
            list.add(createRandomFireworkExplosion(random));
        }

        return list;
    }

    public static CompoundNBT createRandomFireworkExplosion(Random random) {
        CompoundNBT fireworkCompound = new CompoundNBT();

        fireworkCompound.putBoolean("Flicker", random.nextInt(20) == 0);
        fireworkCompound.putBoolean("Trail", random.nextInt(30) == 0);
        fireworkCompound.putByte("Type", (byte) ((random.nextInt(10) != 0) ? 0 : (random.nextInt(4) + 1)));

        int[] colors = new int[(random.nextInt(15) != 0) ? 1 : (random.nextInt(2) + 2)];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = DyeColor.byId(random.nextInt(16)).ordinal();
        }
        fireworkCompound.putIntArray("Colors", colors);

        if (random.nextInt(25) == 0) {
            int[] fadeColors = new int[random.nextInt(2) + 1];
            for (int i = 0; i < fadeColors.length; i++) {
                fadeColors[i] = DyeColor.byId(random.nextInt(16)).ordinal();
            }
            fireworkCompound.putIntArray("FadeColors", fadeColors);
        }

        return fireworkCompound;
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTStrings2D("entityIDs", entityIDs, compound);

        compound.putInt("nameEntities", nameEntities);
        compound.putInt("equipLevel", equipLevel);
        compound.putInt("buffLevel", buffLevel);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        entityIDs = PBNBTHelper.readNBTStrings2D("entityIDs", compound);

        nameEntities = compound.getInt("nameEntities");
        equipLevel = compound.getInt("equipLevel");
        buffLevel = compound.getInt("buffLevel");
    }
}

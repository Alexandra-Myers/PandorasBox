package ivorius.pandorasbox.effects.spawn_entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.random.PandorasBoxEntityNamer;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static ivorius.pandorasbox.effects.PBEffect.getPlayer;

public record SpawnEntityIDListEffect(String[][] entityIDs, int nameEntities, int equipLevel, int buffLevel, EntitySpawnConfiguration entitySpawnConfiguration) implements SpawnEntitiesEffect {
    public static final MapCodec<SpawnEntityIDListEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(PBNBTHelper.arrayCodec(Codec.STRING, () -> new String[0]), () -> new String[0][]).fieldOf("entities").forGetter(SpawnEntityIDListEffect::entityIDs),
                            Codec.INT.fieldOf("named_entities").forGetter(SpawnEntityIDListEffect::nameEntities),
                            Codec.INT.fieldOf("equipment_level").forGetter(SpawnEntityIDListEffect::equipLevel),
                            Codec.INT.fieldOf("buff_level").forGetter(SpawnEntityIDListEffect::buffLevel),
                            EntitySpawnConfiguration.MAP_CODEC.forGetter(SpawnEntityIDListEffect::entitySpawnConfiguration))
                    .apply(instance, SpawnEntityIDListEffect::new));
    @Override
    public Entity spawnEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, int number, double x, double y, double z) {
        if(world.isClientSide()) return null;
        String[] entityTower = entityIDs[number];
        Entity previousEntity = null;

        for (String entityID : entityTower) {
            Entity newEntity = createEntity(world, pbEntity, random, entityID, x, y, z);

            if (newEntity instanceof LivingEntity) {
                randomizeEntity(random, pbEntity.getId(), (LivingEntity) newEntity, nameEntities, equipLevel, buffLevel);
            }

            if (previousEntity != null) {
                world.addFreshEntity(previousEntity);
                assert newEntity != null;
                previousEntity.startRiding(newEntity, true, true);
            }

            previousEntity = newEntity;
        }

        if (previousEntity != null) {
            world.addFreshEntity(previousEntity);
        }

        return previousEntity;
    }

    public static void randomizeEntity(RandomSource random, long namingSeed, LivingEntity entityLiving, int nameEntities, int equipLevel, int buffLevel) {
        if (!entityLiving.hasCustomName()) {
            if (nameEntities == 1) {
                entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomName(random));
                entityLiving.setCustomNameVisible(true);
            } else if (nameEntities == 2) {
                entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(random));
            } else if (nameEntities == 3) {
                entityLiving.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(RandomSource.create(namingSeed)));
            }
        }

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

                        entityLiving.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
                    } else {
                        if (i == 4 && random.nextFloat() < 0.2f / equipLevel)
                            entityLiving.setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                        else {
                            EquipmentSlot slot = i == 1 ? EquipmentSlot.LEGS : i == 2 ? EquipmentSlot.FEET : EquipmentSlot.CHEST;
                            Item item = Mob.getEquipmentForSlot(slot, Math.min(itemLevel, 4));

                            if (item != null) entityLiving.setItemSlot(slot, new ItemStack(item));
                            else System.err.println("Pandora's Box: Item not found for slot '" + slot + "', level '" + itemLevel + "'");
                        }
                    }
                }
            }
        }

        if (buffLevel > 0) {
            AttributeInstance health = entityLiving.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
                double healthMultiplierP = random.nextDouble() * buffLevel * 0.25;
                health.addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "zeus_magic_health"), healthMultiplierP, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }

            AttributeInstance knockbackResistance = entityLiving.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (knockbackResistance != null) {
                double knockbackResistanceP = random.nextDouble() * buffLevel * 0.25;
                knockbackResistance.addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "zeus_magic_knockback_resistance"), knockbackResistanceP, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }

            AttributeInstance movementSpeed = entityLiving.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                double movementSpeedP = random.nextDouble() * buffLevel * 0.08;
                movementSpeed.addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "zeus_magic_speed"), movementSpeedP, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }

            AttributeInstance attackDamage = entityLiving.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null) {
                double attackDamageP = random.nextDouble() * buffLevel * 0.25;
                attackDamage.addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "zeus_magic_damage"), attackDamageP, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    public static Entity createEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, String entityID, double x, double y, double z) {
        try {
            String trunkEntityID = ResourceLocation.parse(entityID).getPath();
            if ("pbspecial_colorful_sheep".equals(trunkEntityID)){
                Sheep sheep = EntityType.SHEEP.create(world, EntitySpawnReason.COMMAND);

                assert sheep != null;
                if (random.nextInt(32 * 32) == 0) sheep.setCustomName(Component.literal("jeb_"));
                moveTo(sheep, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                sheep.finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(BlockPos.containing(x, y, z)), null, null);
                sheep.setColor(DyeColor.byId(random.nextInt(16)));

                return sheep;
            } else if ("pbspecial_hogfather".equals(trunkEntityID)){
                Zombie santa = EntityType.ZOMBIE.create(world, EntitySpawnReason.COMMAND);
                ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
                helmet.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff0000));
                ItemStack chestPlate = new ItemStack(Items.LEATHER_CHESTPLATE);
                chestPlate.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff0000));
                ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
                leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff0000));
                ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
                boots.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff0000));

                assert santa != null;
                moveTo(santa, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                santa.finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(BlockPos.containing(x, y, z)), null, null);
                santa.setItemSlot(EquipmentSlot.HEAD, helmet);
                santa.setItemSlot(EquipmentSlot.CHEST, chestPlate);
                santa.setItemSlot(EquipmentSlot.LEGS, leggings);
                santa.setItemSlot(EquipmentSlot.FEET, boots);
                santa.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK));

                santa.setCustomName(Component.literal("Hogfather"));

                return santa;
            } else if ("pbspecial_experience".equals(trunkEntityID)) {
                return new ExperienceOrb(world, x, y, z, 10);
            } else if ("pbspecial_wolf_tamed".equals(trunkEntityID)) {
                Player owner = getPlayer(world, pbEntity);
                Wolf wolf = EntityType.WOLF.create(world, EntitySpawnReason.COMMAND);

                assert wolf != null;
                moveTo(wolf, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                wolf.finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(BlockPos.containing(x, y, z)), null, null);


                if (owner != null) {
                    wolf.getNavigation().stop();
                    wolf.setTarget(null);
                    wolf.tame(owner);
                    wolf.level().broadcastEntityEvent(wolf, (byte) 7);
                }

                return wolf;
            } else if ("pbspecial_cat_tamed".equals(trunkEntityID)) {
                Player owner = getPlayer(world, pbEntity);

                Cat cat = EntityType.CAT.create(world, EntitySpawnReason.COMMAND);

                assert cat != null;
                moveTo(cat, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                cat.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);

                if (owner != null) {
                    cat.tame(owner);
                    world.broadcastEntityEvent(cat, (byte) 7);
                }

                return cat;
            } else if ("pbspecial_parrot_tamed".equals(trunkEntityID)) {
                Player owner = getPlayer(world, pbEntity);

                Parrot parrot = EntityType.PARROT.create(world, EntitySpawnReason.COMMAND);

                assert parrot != null;
                moveTo(parrot, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                parrot.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);

                if (owner != null) {
                    parrot.tame(owner);
                    world.broadcastEntityEvent(parrot, (byte) 7);
                }

                return parrot;
            } else if (trunkEntityID.startsWith("pbspecial_tnt")) {
                PrimedTnt primedTnt = new PrimedTnt(world, x, y, z, getPlayer(world, pbEntity));
                primedTnt.setFuse(Integer.parseInt(trunkEntityID.substring(13)));

                return primedTnt;
            } else if (trunkEntityID.startsWith("pbspecial_invisible_tnt")) {
                PrimedTnt primedTnt = new PrimedTnt(world, x, y, z, getPlayer(world, pbEntity));
                primedTnt.setFuse(Integer.parseInt(trunkEntityID.substring(23)));
                primedTnt.setInvisible(true);

                return primedTnt;
            } else if ("pbspecial_fireworks".equals(trunkEntityID)) {
                ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
                stack.set(DataComponents.FIREWORKS, createRandomFirework(random));

                return new FireworkRocketEntity(world, x, y, z,stack);
            } else if ("pbspecial_angry_wolf".equals(trunkEntityID)) {
                Wolf wolf = EntityType.WOLF.create(world, EntitySpawnReason.COMMAND);
                assert wolf != null;
                wolf.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);
                moveTo(wolf, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                wolf.setTarget(world.getNearestPlayer(x, y, z, 40.0, false));

                return wolf;
            } else if ("pbspecial_charged_creeper".equals(trunkEntityID)) {
                Creeper creeper = EntityType.CREEPER.create(world, EntitySpawnReason.COMMAND);
                assert creeper != null;
                creeper.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);
                moveTo(creeper, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
                return creeper;
            }
            EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.tryParse(entityID));
            Entity entity1 = entity.create(world, EntitySpawnReason.COMMAND);
            assert entity1 != null;
            moveTo(entity1, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
            Player owner = getPlayer(world, pbEntity);
            if (owner != null && entity1.getY() - owner.getY() > entity.clientTrackingRange() * 16)
                entity1.setPos(entity1.getX(), owner.getY() + entity.clientTrackingRange() * 16 - 1, entity1.getZ());
            if(entity1 instanceof AbstractPiglin piglin)
                piglin.setImmuneToZombification(true);
            if(entity1 instanceof Hoglin hoglin)
                hoglin.setImmuneToZombification(true);
            if (entity1 instanceof Mob mob)
                mob.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);

            return entity1;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void moveTo(Entity entity, Vec3 pos, float yRot, float xRot) {
        entity.setPos(pos);
        entity.forceSetRotation(yRot, true, xRot, true);
    }

    public static Fireworks createRandomFirework(RandomSource random) {
        return new Fireworks((random.nextInt(15) != 0) ? 1 : (2 + random.nextInt(2)), createRandomFireworkExplosions(random, (random.nextInt(20)) != 0 ? 1 : (1 + random.nextInt(2))));
    }

    public static List<FireworkExplosion> createRandomFireworkExplosions(RandomSource random, int number) {
        List<FireworkExplosion> list = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            list.add(createRandomFireworkExplosion(random));
        }

        return list;
    }

    public static FireworkExplosion createRandomFireworkExplosion(RandomSource random) {
        FireworkExplosion.Shape fireworkShape = FireworkExplosion.Shape.byId((random.nextInt(10) != 0) ? 0 : (random.nextInt(4) + 1));

        int size = (random.nextInt(15) != 0) ? 1 : (random.nextInt(2) + 2);
        IntList colors = new IntArrayList(size);
        for (int i = 0; i < size; i++) {
            colors.add(i, DyeColor.byId(random.nextInt(16)).getFireworkColor());
        }

        IntList fadeColors = IntLists.emptyList();
        if (random.nextInt(25) == 0) {
            size = random.nextInt(2) + 1;
            fadeColors = new IntArrayList(size);
            for (int i = 0; i < size; i++) {
                fadeColors.add(i, DyeColor.byId(random.nextInt(16)).getFireworkColor());
            }
        }

        return new FireworkExplosion(fireworkShape, colors, fadeColors, random.nextInt(30) == 0, random.nextInt(20) == 0);
    }

    @Override
    public @NotNull MapCodec<? extends SpawnEntitiesEffect> codec() {
        return CODEC;
    }
}

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
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
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
    public static final EquipmentSlot[] VALID_ITEM_SLOTS = new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND};
    @Override
    public Entity spawnEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, int number, double x, double y, double z) {
        if(world.isClientSide()) return null;
        String[] entityTower = entityIDs[number];
        Entity previousEntity = null;

        for (String entityID : entityTower) {
            Entity[] addedEntities = createEntity(world, pbEntity, random, entityID, x, y, z);

            for (Entity newEntity : addedEntities) {
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
        }

        if (previousEntity != null) {
            world.addFreshEntity(previousEntity);
        }

        return previousEntity;
    }

    public static void randomizeEntity(RandomSource random, long namingSeed, LivingEntity livingEntity, int nameEntities, int equipLevel, int buffLevel) {
        if (!livingEntity.hasCustomName()) {
            if (nameEntities == 1) {
                livingEntity.setCustomName(PandorasBoxEntityNamer.getRandomName(random));
                livingEntity.setCustomNameVisible(true);
            } else if (nameEntities == 2) {
                livingEntity.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(random));
            } else if (nameEntities == 3) {
                livingEntity.setCustomName(PandorasBoxEntityNamer.getRandomCasualName(RandomSource.create(namingSeed)));
            }
        }

        if (livingEntity.level() instanceof ServerLevel serverLevel && equipLevel > 0) {
            float itemChancePerSlot = 1.0f - (0.5f / equipLevel);
            float upgradeChancePerSlot = 1.0f - (1.0f / equipLevel);

            for (EquipmentSlot slot : VALID_ITEM_SLOTS) {
                if (random.nextFloat() < itemChancePerSlot) {
                    int itemLevel = 0;
                    while (random.nextFloat() < upgradeChancePerSlot && itemLevel < equipLevel) {
                        itemLevel++;
                    }

                    ItemStack stack = ItemStack.EMPTY;

                    if (slot.equals(EquipmentSlot.MAINHAND)) {
                        stack = PandorasBoxHelper.getRandomWeaponItemForLevel(random, itemLevel);
                        if (stack == null) stack = ItemStack.EMPTY;
                    } else {
                        if (slot.equals(EquipmentSlot.HEAD) && random.nextFloat() < 0.2f / equipLevel)
                            stack = new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN);
                        else {
                            Item item = Mob.getEquipmentForSlot(slot, Math.min(itemLevel, 5));

                            if (item != null) stack = new ItemStack(item);
                            else System.err.println("Pandora's Box: Item not found for slot '" + slot + "', level '" + itemLevel + "'");
                        }
                    }
                    if (!stack.isEmpty()) {
                        if (livingEntity instanceof Mob mob) {
                            mob.equipItemIfPossible(serverLevel, stack);
                            mob.setDropChance(slot, 0.085F);
                        } else livingEntity.setItemSlot(slot, stack);
                    }
                }
            }
        }

        if (buffLevel > 0) {
            AttributeInstance health = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
                double healthMultiplierP = random.nextDouble() * buffLevel * 0.25;
                health.addPermanentModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "zeus_magic_health"), healthMultiplierP, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                livingEntity.setHealth((float) (livingEntity.getHealth() + livingEntity.getHealth() * healthMultiplierP));
            }

            AttributeInstance knockbackResistance = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (knockbackResistance != null) {
                double knockbackResistanceP = random.nextDouble() * buffLevel * 0.25;
                knockbackResistance.addPermanentModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "zeus_magic_knockback_resistance"), knockbackResistanceP, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }

            AttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                double movementSpeedP = random.nextDouble() * buffLevel * 0.08;
                movementSpeed.addPermanentModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "zeus_magic_speed"), movementSpeedP, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }

            AttributeInstance attackDamage = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null) {
                double attackDamageP = random.nextDouble() * buffLevel * 0.25;
                attackDamage.addPermanentModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "zeus_magic_damage"), attackDamageP, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    public static Entity[] createEntity(Level world, PandorasBoxEntity pbEntity, RandomSource random, String entityID, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) return null;
        Identifier asID = Identifier.parse(entityID);
        String trunkEntityID = asID.getPath();
        if ("pbspecial_zombie_horseman".equals(trunkEntityID)) {
            Zombie horseman = EntityType.ZOMBIE.create(serverLevel, EntitySpawnReason.COMMAND);
            assert horseman != null;
            moveTo(horseman, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
            horseman.finalizeSpawn(serverLevel, (serverLevel).getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);
            horseman.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            horseman.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
            horseman.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
            horseman.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            horseman.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SPEAR));

            ZombieHorse horse = EntityType.ZOMBIE_HORSE.create(serverLevel, EntitySpawnReason.COMMAND);
            assert horse != null;
            moveTo(horse, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
            horse.finalizeSpawn(serverLevel, (serverLevel).getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);
            horse.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.NETHERITE_HORSE_ARMOR));
            return new Entity[] {horseman, horse};
        } else if ("pbspecial_nautilus_jockey".equals(trunkEntityID)) {
            Drowned jockey = EntityType.DROWNED.create(serverLevel, EntitySpawnReason.COMMAND);
            assert jockey != null;
            moveTo(jockey, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
            jockey.finalizeSpawn(serverLevel, (serverLevel).getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);
            jockey.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            jockey.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
            jockey.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
            jockey.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            jockey.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));

            ZombieNautilus nautilus = EntityType.ZOMBIE_NAUTILUS.create(serverLevel, EntitySpawnReason.COMMAND);
            assert nautilus != null;
            moveTo(nautilus, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
            nautilus.finalizeSpawn(serverLevel, (serverLevel).getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);
            nautilus.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.NETHERITE_NAUTILUS_ARMOR));
            return new Entity[] {jockey, nautilus};
        } else return new Entity[] {createEntity(serverLevel, pbEntity, random, asID, trunkEntityID, x, y, z)};
    }

    public static Entity createEntity(ServerLevel serverLevel, PandorasBoxEntity pbEntity, RandomSource random, Identifier asID, String trunkEntityID, double x, double y, double z) {
        try {
            if ("pbspecial_colorful_sheep".equals(trunkEntityID)){
                Sheep sheep = EntityType.SHEEP.create(serverLevel, EntitySpawnReason.COMMAND);

                assert sheep != null;
                if (random.nextInt(32 * 32) == 0) sheep.setCustomName(Component.literal("jeb_"));
                moveTo(sheep, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                sheep.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(x, y, z)), null, null);
                sheep.setColor(DyeColor.byId(random.nextInt(16)));

                return sheep;
            } else if ("pbspecial_hogfather".equals(trunkEntityID)){
                Zombie santa = EntityType.ZOMBIE.create(serverLevel, EntitySpawnReason.COMMAND);
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
                santa.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(x, y, z)), null, null);
                santa.setItemSlot(EquipmentSlot.HEAD, helmet);
                santa.setItemSlot(EquipmentSlot.CHEST, chestPlate);
                santa.setItemSlot(EquipmentSlot.LEGS, leggings);
                santa.setItemSlot(EquipmentSlot.FEET, boots);
                santa.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK));

                santa.setCustomName(Component.literal("Hogfather"));

                return santa;
            } else if ("pbspecial_experience".equals(trunkEntityID)) {
                return new ExperienceOrb(serverLevel, x, y, z, 10);
            } else if ("pbspecial_wolf_tamed".equals(trunkEntityID)) {
                Player owner = getPlayer(serverLevel, pbEntity);
                Wolf wolf = EntityType.WOLF.create(serverLevel, EntitySpawnReason.COMMAND);

                assert wolf != null;
                moveTo(wolf, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                wolf.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(x, y, z)), null, null);


                if (owner != null) {
                    wolf.getNavigation().stop();
                    wolf.setTarget(null);
                    wolf.tame(owner);
                    wolf.level().broadcastEntityEvent(wolf, (byte) 7);
                }

                return wolf;
            } else if ("pbspecial_cat_tamed".equals(trunkEntityID)) {
                Player owner = getPlayer(serverLevel, pbEntity);

                Cat cat = EntityType.CAT.create(serverLevel, EntitySpawnReason.COMMAND);

                assert cat != null;
                moveTo(cat, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                cat.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);

                if (owner != null) {
                    cat.tame(owner);
                    serverLevel.broadcastEntityEvent(cat, (byte) 7);
                }

                return cat;
            } else if ("pbspecial_parrot_tamed".equals(trunkEntityID)) {
                Player owner = getPlayer(serverLevel, pbEntity);

                Parrot parrot = EntityType.PARROT.create(serverLevel, EntitySpawnReason.COMMAND);

                assert parrot != null;
                moveTo(parrot, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                parrot.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);

                if (owner != null) {
                    parrot.tame(owner);
                    serverLevel.broadcastEntityEvent(parrot, (byte) 7);
                }

                return parrot;
            } else if (trunkEntityID.startsWith("pbspecial_tnt")) {
                PrimedTnt primedTnt = new PrimedTnt(serverLevel, x, y, z, getPlayer(serverLevel, pbEntity));
                primedTnt.setFuse(Integer.parseInt(trunkEntityID.substring(13)));

                return primedTnt;
            } else if (trunkEntityID.startsWith("pbspecial_invisible_tnt")) {
                PrimedTnt primedTnt = new PrimedTnt(serverLevel, x, y, z, getPlayer(serverLevel, pbEntity));
                primedTnt.setFuse(Integer.parseInt(trunkEntityID.substring(23)));
                primedTnt.setInvisible(true);

                return primedTnt;
            } else if ("pbspecial_fireworks".equals(trunkEntityID)) {
                ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
                stack.set(DataComponents.FIREWORKS, createRandomFirework(random));

                return new FireworkRocketEntity(serverLevel, x, y, z,stack);
            } else if ("pbspecial_angry_wolf".equals(trunkEntityID)) {
                Wolf wolf = EntityType.WOLF.create(serverLevel, EntitySpawnReason.COMMAND);
                assert wolf != null;
                wolf.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);
                moveTo(wolf, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                wolf.setTarget(serverLevel.getNearestPlayer(x, y, z, 40.0, false));

                return wolf;
            } else if ("pbspecial_charged_creeper".equals(trunkEntityID)) {
                Creeper creeper = EntityType.CREEPER.create(serverLevel, EntitySpawnReason.COMMAND);
                assert creeper != null;
                creeper.finalizeSpawn(serverLevel, (serverLevel).getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);
                moveTo(creeper, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
                creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
                return creeper;
            }
            EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.getValue(asID);
            Entity entity1 = entity.create(serverLevel, EntitySpawnReason.COMMAND);
            assert entity1 != null;
            moveTo(entity1, new Vec3(x, y, z), random.nextFloat() * 360.0f, 0.0f);
            Player owner = getPlayer(serverLevel, pbEntity);
            if (owner != null && entity1.getY() - owner.getY() > entity.clientTrackingRange() * 16)
                entity1.setPos(entity1.getX(), owner.getY() + entity.clientTrackingRange() * 16 - 1, entity1.getZ());
            if(entity1 instanceof AbstractPiglin piglin)
                piglin.setImmuneToZombification(true);
            if(entity1 instanceof Hoglin hoglin)
                hoglin.setImmuneToZombification(true);
            if (entity1 instanceof Mob mob)
                mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(x,y,z)), null, null);

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

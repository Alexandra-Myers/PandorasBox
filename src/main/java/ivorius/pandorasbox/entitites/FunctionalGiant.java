package ivorius.pandorasbox.entitites;

import ivorius.pandorasbox.entitites.goals.GiantAttackGoal;
import ivorius.pandorasbox.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.UUID;

public class FunctionalGiant extends Giant implements NeutralMob {
    private static final double DEFAULT_ATTACK_REACH = Math.sqrt(2.04) - 0.6;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private static final UniformInt RAMPAGE_UPDATE_TIME = TimeUtil.rangeOfSeconds(10, 15);
    private boolean isOnRampage = false;
    private int rampageTime = 0;
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;
    public FunctionalGiant(EntityType<? extends Giant> entityType, Level level) {
        super(entityType, level);
        setMaxUpStep(2.5F);
        this.xpReward = 20;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new GiantAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(2, new FleeSunGoal(this, 1.0));
        this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 1.0, true, 4, () -> false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.addPersistentAngerSaveData(compound);
        compound.putBoolean("isOnRampage", isOnRampage);
        compound.putInt("xpReward", xpReward);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.readPersistentAngerSaveData(this.level(), compound);
        this.isOnRampage = compound.getBoolean("isOnRampage");
        this.xpReward = compound.getInt("xpReward");
    }

    public static AttributeSupplier.Builder createGiantAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    @Override
    public boolean isAngryAt(LivingEntity livingEntity) {
        if (this.isOnRampage && (livingEntity instanceof AbstractVillager || livingEntity instanceof Player || livingEntity instanceof IronGolem)) return true;
        return NeutralMob.super.isAngryAt(livingEntity);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity livingEntity) {
        return this.getAttackBoundingBox().intersects(livingEntity.getBoundingBox().inflate(livingEntity.getPickRadius()));
    }

    protected AABB getAttackBoundingBox() {
        Entity entity = this.getVehicle();
        AABB finalBoundingBox;
        if (entity != null) {
            AABB vehicleBoundingBox = entity.getBoundingBox();
            AABB selfBoundingBox = this.getBoundingBox();
            finalBoundingBox = new AABB(Math.min(selfBoundingBox.minX, vehicleBoundingBox.minX), selfBoundingBox.minY, Math.min(selfBoundingBox.minZ, vehicleBoundingBox.minZ), Math.max(selfBoundingBox.maxX, vehicleBoundingBox.maxX), selfBoundingBox.maxY, Math.max(selfBoundingBox.maxZ, vehicleBoundingBox.maxZ));
        } else finalBoundingBox = this.getBoundingBox();

        return finalBoundingBox.inflate(DEFAULT_ATTACK_REACH, 0.0, DEFAULT_ATTACK_REACH);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (!super.hurt(damageSource, amount)) {
            return false;
        } else if (!(this.level() instanceof ServerLevel serverLevel)) {
            return false;
        } else {
            LivingEntity livingEntity = this.getTarget();
            if (livingEntity == null && damageSource.getEntity() instanceof LivingEntity) {
                livingEntity = (LivingEntity)damageSource.getEntity();
            }

            if (livingEntity != null
                    && this.level().getDifficulty() == Difficulty.HARD
                    && (double)this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                    && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                int xPos = Mth.floor(this.getX());
                int yPos = Mth.floor(this.getY());
                int zPos = Mth.floor(this.getZ());
                FunctionalGiant giant = new FunctionalGiant(EntityInit.GIANT.get(), this.level());

                for (int tries = 0; tries < 50; tries++) {
                    int offsetXPos = xPos + Mth.nextInt(this.random, 7, 50) * Mth.nextInt(this.random, -1, 1);
                    int offsetYPos = yPos + Mth.nextInt(this.random, 30, 60) * Mth.nextInt(this.random, -1, 1);
                    int offsetZPos = zPos + Mth.nextInt(this.random, 7, 50) * Mth.nextInt(this.random, -1, 1);
                    BlockPos blockPos = new BlockPos(offsetXPos, offsetYPos, offsetZPos);
                    EntityType<?> entityType = giant.getType();
                    SpawnPlacements.Type type = SpawnPlacements.getPlacementType(entityType);
                    if (NaturalSpawner.isSpawnPositionOk(type, this.level(), blockPos, entityType)
                            && SpawnPlacements.checkSpawnRules(entityType, serverLevel, MobSpawnType.REINFORCEMENT, blockPos, this.level().random)) {
                        giant.setPos(offsetXPos, offsetYPos, offsetZPos);
                        if (!this.level().hasNearbyAlivePlayer(offsetXPos, offsetYPos, offsetZPos, 7.0)
                                && this.level().isUnobstructed(giant)
                                && this.level().noCollision(giant)
                                && !this.level().containsAnyLiquid(giant.getBoundingBox())) {
                            giant.setTarget(livingEntity);
                            giant.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(giant.blockPosition()), MobSpawnType.REINFORCEMENT, null, null);
                            serverLevel.addFreshEntityWithPassengers(giant);
                            this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                                    .addPermanentModifier(new AttributeModifier("Giant reinforcement caller charge", -0.1F, AttributeModifier.Operation.ADDITION));
                            giant.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                                    .addPermanentModifier(new AttributeModifier("Giant reinforcement callee charge", -0.1F, AttributeModifier.Operation.ADDITION));
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean shouldBurnThisTick = this.isSunBurnTick();
            if (shouldBurnThisTick) {
                ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageableItem()) {
                        itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));
                        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    shouldBurnThisTick = false;
                }

                if (shouldBurnThisTick) {
                    this.setSecondsOnFire(8);
                }
            }
            if (this.rampageTime == 0) {
                startRampageRecheckTimer();
                if (random.nextFloat() > 0.65) this.isOnRampage = !this.isOnRampage;
            }
            this.rampageTime--;
        }

        super.aiStep();
    }
    @Override
    protected void customServerAiStep() {
        this.updatePersistentAnger((ServerLevel) this.level(), true);
        super.customServerAiStep();
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean didHurtTarget = super.doHurtTarget(entity);
        if (didHurtTarget) {
            float effectiveDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < effectiveDifficulty * 0.3F) {
                entity.setSecondsOnFire(2 * (int)effectiveDifficulty);
            }
        }

        return didHurtTarget;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
        super.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
        if (randomSource.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.1F : 0.05F)) {
            int equipment = randomSource.nextInt(3);
            if (equipment == 0) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }

    @Override
    public boolean killedEntity(ServerLevel serverLevel, LivingEntity livingEntity) {
        boolean success = super.killedEntity(serverLevel, livingEntity);
        if ((serverLevel.getDifficulty() == Difficulty.NORMAL || serverLevel.getDifficulty() == Difficulty.HARD) && livingEntity instanceof Villager villager) {
            if (serverLevel.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return success;
            }

            ZombieVillager zombieVillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
            if (zombieVillager != null) {
                zombieVillager.finalizeSpawn(
                        serverLevel, serverLevel.getCurrentDifficultyAt(zombieVillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), null
                );
                zombieVillager.setVillagerData(villager.getVillagerData());
                zombieVillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
                zombieVillager.setTradeOffers(villager.getOffers().createTag());
                zombieVillager.setVillagerXp(villager.getVillagerXp());
                if (!this.isSilent()) {
                    serverLevel.levelEvent(null, 1026, this.blockPosition(), 0);
                }

                success = false;
            }
        }

        return success;
    }

    @Override
    public boolean canHoldItem(ItemStack itemStack) {
        return (!itemStack.is(Items.EGG) || !this.isBaby() || !this.isPassenger()) && super.canHoldItem(itemStack);
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        return !itemStack.is(Items.GLOW_INK_SAC) && super.wantsToPickUp(itemStack);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag
    ) {
        RandomSource randomSource = serverLevelAccessor.getRandom();
        spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
        float difficultySpecialMult = difficultyInstance.getSpecialMultiplier();
        this.setCanPickUpLoot(randomSource.nextFloat() < 0.55F * difficultySpecialMult);

        this.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
        this.populateDefaultEquipmentEnchantments(randomSource, difficultyInstance);

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && randomSource.nextFloat() < 0.25F) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(randomSource.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.setDropChance(EquipmentSlot.HEAD, 0.0F);
            }
        }

        this.handleAttributes(difficultySpecialMult);
        return spawnGroupData;
    }

    protected void handleAttributes(float difficultySpecialMult) {
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)
                .addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05F, AttributeModifier.Operation.ADDITION));
        double followRangeBonus = this.random.nextDouble() * 1.5 * (double)difficultySpecialMult;
        if (followRangeBonus > 1.0) {
            this.getAttribute(Attributes.FOLLOW_RANGE)
                    .addPermanentModifier(new AttributeModifier("Random spawn bonus", followRangeBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        if (this.random.nextFloat() < difficultySpecialMult * 0.1F) {
            this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                    .addPermanentModifier(new AttributeModifier("Leader giant bonus", this.random.nextDouble() * 0.25 + 0.25, AttributeModifier.Operation.ADDITION));
            double healthBonus = this.random.nextDouble() * 3.0 + 1.0;
            this.getAttribute(Attributes.MAX_HEALTH)
                    .addPermanentModifier(new AttributeModifier("Leader giant bonus", healthBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
            this.xpReward += (int) (healthBonus * this.xpReward);
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int i, boolean bl) {
        super.dropCustomDeathLoot(damageSource, i, bl);
        if (damageSource.getEntity() instanceof Creeper creeper && creeper.canDropMobsSkull()) {
            ItemStack itemStack = this.getSkull();
            if (!itemStack.isEmpty()) {
                creeper.increaseDroppedSkulls();
                this.spawnAtLocation(itemStack);
            }
        }
    }

    protected ItemStack getSkull() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int remainingPersistentAngerTime) {
        this.remainingPersistentAngerTime = remainingPersistentAngerTime;
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID persistentAngerTarget) {
        this.persistentAngerTarget = persistentAngerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }


    public void startRampageRecheckTimer() {
        this.rampageTime = RAMPAGE_UPDATE_TIME.sample(this.random);
    }

    @Override
    public boolean isPreventingPlayerRest(Player player) {
        return this.isAngryAt(player);
    }

    @Override
    public float getWalkTargetValue(BlockPos arg, LevelReader arg2) {
        return -super.getWalkTargetValue(arg, arg2);
    }

    public static boolean checkGiantSpawnRules(
            EntityType<? extends Monster> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource
    ) {
        return serverLevelAccessor.getDifficulty() != Difficulty.PEACEFUL
                && isDarkEnoughToSpawn(serverLevelAccessor, blockPos, randomSource)
                && checkMobSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
    }

    public static boolean isDarkEnoughToSpawn(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, RandomSource randomSource) {
        if (serverLevelAccessor.getBrightness(LightLayer.SKY, blockPos) > randomSource.nextIntBetweenInclusive(8, 20)) {
            return false;
        } else {
            DimensionType dimensionType = serverLevelAccessor.dimensionType();
            if (serverLevelAccessor.getBrightness(LightLayer.BLOCK, blockPos) > 11) {
                return false;
            } else {
                int brightness = serverLevelAccessor.getLevel().isThundering()
                        ? serverLevelAccessor.getMaxLocalRawBrightness(blockPos, 15)
                        : serverLevelAccessor.getMaxLocalRawBrightness(blockPos, 8);
                return brightness <= dimensionType.monsterSpawnLightTest().sample(randomSource);
            }
        }
    }
}

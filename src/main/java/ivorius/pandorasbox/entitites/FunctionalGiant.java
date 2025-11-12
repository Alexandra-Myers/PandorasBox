package ivorius.pandorasbox.entitites;

import com.google.common.annotations.VisibleForTesting;
import ivorius.pandorasbox.entitites.goals.GiantAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.UUID;

public class FunctionalGiant extends Giant implements NeutralMob {
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private static final UniformInt RAMPAGE_UPDATE_TIME = TimeUtil.rangeOfSeconds(10, 15);
    private boolean isOnRampage = false;
    private int rampageTime = 0;
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;
    public FunctionalGiant(EntityType<? extends Giant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
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
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        this.addPersistentAngerSaveData(valueOutput);
        valueOutput.putBoolean("isOnRampage", isOnRampage);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.readPersistentAngerSaveData(this.level(), valueInput);
        this.isOnRampage = valueInput.getBooleanOr("isOnRampage", false);
    }

    public static AttributeSupplier.Builder createGiantAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.CAMERA_DISTANCE, 16.0)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.ARMOR, 8.0);
    }

    @Override
    public boolean isAngryAt(LivingEntity livingEntity, ServerLevel serverLevel) {
        if (this.isOnRampage && (livingEntity instanceof AbstractVillager || livingEntity instanceof Player || livingEntity instanceof IronGolem)) return true;
        return NeutralMob.super.isAngryAt(livingEntity, serverLevel);
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean shouldBurnThisTick = this.isSunBurnTick();
            if (shouldBurnThisTick) {
                ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageableItem()) {
                        Item item = itemStack.getItem();
                        itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));
                        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                            this.onEquippedItemBroken(item, EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    shouldBurnThisTick = false;
                }

                if (shouldBurnThisTick) {
                    this.igniteForSeconds(8.0F);
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
    protected void customServerAiStep(ServerLevel serverLevel) {
        this.updatePersistentAnger(serverLevel, true);
        super.customServerAiStep(serverLevel);
    }

    @VisibleForTesting
    public boolean convertVillagerToZombieVillager(ServerLevel serverLevel, Villager villager) {
        ZombieVillager zombieVillager = villager.convertTo(
                EntityType.ZOMBIE_VILLAGER,
                ConversionParams.single(villager, true, true),
                zombieVillagerx -> {
                    zombieVillagerx.finalizeSpawn(
                            serverLevel, serverLevel.getCurrentDifficultyAt(zombieVillagerx.blockPosition()), EntitySpawnReason.CONVERSION, new Zombie.ZombieGroupData(false, true)
                    );
                    zombieVillagerx.setVillagerData(villager.getVillagerData());
                    zombieVillagerx.setGossips(villager.getGossips().copy());
                    zombieVillagerx.setTradeOffers(villager.getOffers().copy());
                    zombieVillagerx.setVillagerXp(villager.getVillagerXp());
                    if (!this.isSilent()) {
                        serverLevel.levelEvent(null, 1026, this.blockPosition(), 0);
                    }
                }
        );
        return zombieVillager != null;
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        boolean didHurtTarget = super.doHurtTarget(serverLevel, entity);
        if (didHurtTarget) {
            float effectiveDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < effectiveDifficulty * 0.3F) {
                entity.igniteForSeconds(2 * (int)effectiveDifficulty);
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
        if (randomSource.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            int i = randomSource.nextInt(3);
            if (i == 0) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }

    @Override
    public boolean killedEntity(ServerLevel serverLevel, LivingEntity livingEntity, DamageSource damageSource) {
        boolean success = super.killedEntity(serverLevel, livingEntity, damageSource);
        if ((serverLevel.getDifficulty() == Difficulty.NORMAL || serverLevel.getDifficulty() == Difficulty.HARD) && livingEntity instanceof Villager villager) {
            if (serverLevel.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return success;
            }

            if (this.convertVillagerToZombieVillager(serverLevel, villager)) {
                success = false;
            }
        }

        return success;
    }

    @Override
    public boolean canHoldItem(ItemStack itemStack) {
        return (!itemStack.is(ItemTags.EGGS) || !this.isBaby() || !this.isPassenger()) && super.canHoldItem(itemStack);
    }

    @Override
    public boolean wantsToPickUp(ServerLevel serverLevel, ItemStack itemStack) {
        return !itemStack.is(Items.GLOW_INK_SAC) && super.wantsToPickUp(serverLevel, itemStack);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData
    ) {
        RandomSource randomSource = serverLevelAccessor.getRandom();
        spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);
        float difficultySpecialMult = difficultyInstance.getSpecialMultiplier();
        this.setCanPickUpLoot(randomSource.nextFloat() < 0.55F * difficultySpecialMult);

        this.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
        this.populateDefaultEquipmentEnchantments(serverLevelAccessor, randomSource, difficultyInstance);

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && randomSource.nextFloat() < 0.25F) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(randomSource.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.setDropChance(EquipmentSlot.HEAD, 0.0F);
            }
        }

        this.handleAttributes();
        return spawnGroupData;
    }

    protected void handleAttributes() {
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)
                .addOrReplacePermanentModifier(new AttributeModifier(RANDOM_SPAWN_BONUS_ID, this.random.nextDouble() * 0.05F, AttributeModifier.Operation.ADD_VALUE));
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
    public boolean isPreventingPlayerRest(ServerLevel serverLevel, Player player) {
        return this.isAngryAt(player, serverLevel);
    }
}
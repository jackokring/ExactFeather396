package uk.co.kring.ef396.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EntityType;
import uk.co.kring.ef396.Loaded;

import java.util.Random;

public class HogEntity extends Animal {

    private boolean stealing = false;

    public HogEntity(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

    }

    @Override
    public boolean save(CompoundTag tag) {

        return super.save(tag);
    }

    public String getFashion() {
        return null;
    }

    public static int spawnWeight() {
        return 10;
    }

// Only needed for entities that are not LivingEntity:
//    @Override
//    public Packet<?> getAddEntityPacket() {
//        return NetworkHooks.getEntitySpawningPacket(this);
//    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    public static boolean canSpawn(EntityType<HogEntity> he, LevelAccessor levelAccess,
                                   MobSpawnType mobSpawnType, BlockPos pos, Random random) {
        return Animal.checkAnimalSpawnRules(he, levelAccess,
                mobSpawnType, pos, random);

    }

    // sounds

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return Loaded.wipple.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return Loaded.error.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return Loaded.boom.get();
    }
}

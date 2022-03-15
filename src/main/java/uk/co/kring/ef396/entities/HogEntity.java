package uk.co.kring.ef396.entities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import uk.co.kring.ef396.ExactFeather;
import net.minecraft.world.entity.EntityType;
import uk.co.kring.ef396.Loaded;

import java.util.UUID;

public class HogEntity extends Husk implements NeutralMob {

    private static final Ingredient TEMPTATION_ITEMS
            = Ingredient.of((ItemLike) Potions.THICK);
    // At last a booze for an Entity
    private static final ResourceLocation LOOT_TABLE
            = new ResourceLocation(ExactFeather.MOD_ID, "entities/hog");

    public HogEntity(EntityType<HogEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier makeAttributes() {
        //return the list of potion-able effected attributes
        return AttributeSupplier.builder()
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.ATTACK_DAMAGE, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 2.0f)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.25f)
                .add(Attributes.MAX_HEALTH, 10.0f)
                .add(Attributes.FOLLOW_RANGE, 8.0f)
                .add(Attributes.ATTACK_KNOCKBACK, 3.0f)
                .add(Attributes.JUMP_STRENGTH, 2.0f)
                .build();
        //TODO
    }

    public static int spawnWeight() { return 100; }

    public static Biome spawnBiome() {
        return null;//null equals all biomes
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));

        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, TEMPTATION_ITEMS, false));

        this.goalSelector.addGoal(8, new EatBlockGoal(this));

        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        // targets
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));

    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return LOOT_TABLE;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 1 + this.getRandom().nextInt(4);
    }

    // sound events
    @Override
    protected SoundEvent getAmbientSound() { return Loaded.wipple.get(); }

    @Override
    protected SoundEvent getDeathSound() { return Loaded.boom.get(); }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return Loaded.error.get();
    }

    // static collision box
    public static float getSizeXZ() {
        return 0.6f;
    }

    public static float getSizeY() {
        return 1.95f;
    }

    // fashion
    public String getFashion() {
        return null;
    }

    // The neutral mob interface
    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {

    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }
}

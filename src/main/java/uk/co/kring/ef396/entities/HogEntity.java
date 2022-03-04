package uk.co.kring.ef396.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import uk.co.kring.ef396.ExactFeather;
import net.minecraft.world.entity.EntityType;
import uk.co.kring.ef396.initializers.SoundInit;

public class HogEntity extends Zombie {

    private static final Ingredient TEMPTATION_ITEMS
            = Ingredient.of((ItemLike) Potions.THICK);
    // At last a booze for an Entity
    private static final ResourceLocation LOOT_TABLE
            = new ResourceLocation(ExactFeather.MOD_ID, "entities/hog");

    public HogEntity(EntityType<? extends Zombie> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        //breed and baby goals
        //this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        //this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));


        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));

        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, TEMPTATION_ITEMS, false));

        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new EatBlockGoal(this));
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return LOOT_TABLE;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 1 + this.getRandom().nextInt(4);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.PIG_AMBIENT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.PIG_DEATH; }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundInit.ERROR.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
    }

    public static float getSizeXZ() {
        return 1.0f;
    }

    public static float getSizeY() {
        return 1.975f;
    }
}

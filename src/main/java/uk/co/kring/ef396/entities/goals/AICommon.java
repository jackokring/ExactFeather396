package uk.co.kring.ef396.entities.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import uk.co.kring.ef396.entities.HogEntity;

import java.util.EnumSet;

public class AICommon extends Goal {

    private HogEntity entity;

    public AICommon(HogEntity entity) {
        this.entity = entity;
    }

    public final HogEntity getEntity() {
        return entity;
    }

    @Override
    public boolean canUse() {
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    @Override
    public boolean isInterruptable() {
        return super.isInterruptable();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return super.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void setFlags(EnumSet<Flag> p_25328_) {
        super.setFlags(p_25328_);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public EnumSet<Flag> getFlags() {
        return super.getFlags();
    }

    @Override
    protected int adjustedTickDelay(int p_186072_) {
        return super.adjustedTickDelay(p_186072_);
    }
}

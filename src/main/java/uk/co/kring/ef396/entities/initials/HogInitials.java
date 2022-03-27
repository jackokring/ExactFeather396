package uk.co.kring.ef396.entities.initials;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.LevelAccessor;
import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.recipes.BrewingCommon;

import java.util.Random;

public class HogInitials {

    public int spawnWeight() {
        return 10;
    }

    public AttributeSupplier.Builder prepareAttributes() {
         var builder = LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
        var attributes = BrewingCommon.attributes();
        for (Attribute ua: attributes) {
            builder.add(ua);// attributes for AI "circuit bending"
        }
        return builder;
    }

    public boolean canSpawn(EntityType<HogEntity> he, LevelAccessor levelAccess,
                                   MobSpawnType mobSpawnType, BlockPos pos, Random random) {
        return Animal.checkAnimalSpawnRules(he, levelAccess,
                mobSpawnType, pos, random);

    }
}

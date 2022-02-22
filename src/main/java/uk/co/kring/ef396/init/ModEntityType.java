package uk.co.kring.ef396.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.entities.HogEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityType {

    public static class HogEntityFactory implements EntityType.EntityFactory<HogEntity> {

        @Override
        public HogEntity create(EntityType<HogEntity> type, Level world) {
            return new HogEntity(type, world);
        }
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ExactFeather.MOD_ID);

    // Entity Types
    public static final RegistryObject<EntityType<HogEntity>> HOG = ENTITY_TYPES.register("hog",
            () -> EntityType.Builder.of(new HogEntityFactory(), MobCategory.CREATURE)
                    .sized(1.0f, 1.0f) // Hitbox Size
                    .build(new ResourceLocation(ExactFeather.MOD_ID, "hog").toString()));
}

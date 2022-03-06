package uk.co.kring.ef396.initializers;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.entities.HogEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import uk.co.kring.ef396.entities.renderers.HogRenderer;
import uk.co.kring.ef396.utilities.RegistryMap;

public class EntityInit {

    public static final RegistryMap<EntityType<?>> ENTITIES
            = new RegistryMap<EntityType<?>>(DeferredRegister.create(ForgeRegistries.ENTITIES, ExactFeather.MOD_ID));

    // Entity Types
    public static final RegistryObject<EntityType<HogEntity>> HOG = ENTITIES.register("hog",
            () -> EntityType.Builder.of(HogEntity::new, MobCategory.CREATURE)
                    .sized(HogEntity.getSizeXZ(), HogEntity.getSizeY()) // Hit box Size
                    .build(new ResourceLocation(ExactFeather.MOD_ID, "hog").toString()));

    static {
        ExactFeather.registerRender((event) -> {
            EntityRenderers.register(HOG.get(), HogRenderer::new);
        });
    }
}

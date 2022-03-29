package uk.co.kring.ef396.entities.initials;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.recipes.BrewingCommon;
import uk.co.kring.ef396.utilities.RegistryMap;

import java.util.Random;
import java.util.function.Supplier;

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

    public Supplier<EntityType<HogEntity>> register(String name,
                                                    EntityType.EntityFactory<HogEntity> entity) {
        EntityType.Builder<HogEntity> builder = EntityType.Builder.of(
                        entity, MobCategory.CREATURE)
                .sized(0.6f, 1.95f) // Hit box Size
                .clientTrackingRange(8)
                .setShouldReceiveVelocityUpdates(false);
        // entity supplier
        Supplier<EntityType<HogEntity>> he =
                () -> builder.build(new ResourceLocation(ExactFeather.MOD_ID, "hog").toString());
        // model control of layers
        ExactFeather.registerLayers((event) -> {
            event.registerLayerDefinition(RegistryMap.HogModel.HOG_LAYER, RegistryMap.HogModel::createBodyLayer);
        });
        // renderer for skin
        ExactFeather.registerRender((event) -> EntityRenderers.register(he.get(),
                (context) -> new HumanoidMobRenderer<HogEntity, RegistryMap.HogModel>(context,
                        new RegistryMap.HogModel(context.bakeLayer(RegistryMap.HogModel.HOG_LAYER)), 1f) {
                    @Override
                    @NotNull
                    public ResourceLocation getTextureLocation(@NotNull HogEntity fashion) {
                        String f = fashion.getFashion();
                        if(f == null) {
                            f = "";
                        } else {
                            f = "/" + f;
                        }
                        return new ResourceLocation(ExactFeather.MOD_ID,
                                "textures/entity/" + name + f + ".png");
                    }
                }));
        // actionable attributes registration
        ExactFeather.registerAttrib((event)
                -> event.put(he.get(), prepareAttributes().build()));//dynamic
        // biome specific spawn assignments
        ExactFeather.registerSpawn((event) -> {
            if(event.getName() == null) return;
            event.getSpawns().addSpawn(MobCategory.CREATURE,
                    new MobSpawnSettings.SpawnerData(he.get(),
                            spawnWeight(),1,3));
        });
        // register spawning placements
        ExactFeather.registerCommon((event) -> {
            SpawnPlacements.register(he.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.WORLD_SURFACE, this::canSpawn);
        });
        return he;
    }
}

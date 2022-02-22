package uk.co.kring.ef396.gen;

import net.minecraft.world.level.biome.Biome;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.init.ModEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntitySpawns {

    @SubscribeEvent
    public static void generateOres(FMLLoadCompleteEvent event) {
        for (Biome biome : ForgeRegistries.BIOMES) {

            // Nether Mobs
            if (biome.getBiomeCategory() == Biome.BiomeCategory.NETHER) { }

            // End Mobs
            else if (biome.getBiomeCategory() == Biome.BiomeCategory.THEEND) { }

            // Overworld Mobs
            else {
                if (biome.getBiomeCategory() != Biome.BiomeCategory.OCEAN) {
                    biome.getSpawns(EntityClassification.CREATURE)
                            .add(new Biome.SpawnListEntry(ModEntityType.HOG.get(), 10, 3, 5));
                }
            }
        }
    }
}

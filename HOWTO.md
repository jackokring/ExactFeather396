# How to Add to and Extend `ExactFeather`

### *Don't Forget `runData` to Make Some of the Files Automatically*

## Adding a Loaded Class
In `src/main/java/uk/co/kring/ef396/loaded` is a class `BlankLoaded.java` which should be extended and implement any methods relating to the following instructions. The class name should be entered in `src/main/resources/assets/ef396` inside the file `loaded_load.txt` as `loaded.<className>` if you placed the class in the same folder as the class `BlankLoaded`.

This has the advantage that any changes in the extends `BlankLoaded` class can be submitted as a pull request without merge collisions, as it would be the only file changed, or rely on independent packages.

## Adding a Simple Item
Inside the method `items(RegistryMap<Item> reg)` include the following code
```
<name> = reg.register("<name>",
    () -> new Item(new Item.Properties().tab(tab)));
```
and add a field to hold the name reference
```
public static RegistryObject<Item> <name>;
```
replacing `<name>` with the actual name you wish to name it internally.

Auto `runData`: Or create a file in `src/main/resources/assets/ef396/models/item` called `<name>.json` with the content
```
{
  "parent": "item/generated",
  "textures": {
    "layer0": "ef396:items/<name>"
  }
}
```
which then references a texture `<name>.png` file (of size 16 by 16 px) in `src/main/resources/assets/ef396/textures/items` and that completes adding a simple item.

You can then add a translation for a display name in `src/main/resources/assets/ef396/lang/en_us.json` like, although this would lead to a merge collision, but a simple one.
```
"item.ef396.<name>": "<displayed name>",
```

## Adding a Simple Potion
Inside the method `potions(RegistryMap<Item> reg)` include the following code
```
MobEffectCommon <effect> = (new MobEffectCommon(MobEffects.<option>))
                .corrupt(false, false, false);
        <name> = reg.regPotionImmediate("<name>",
                <item to add to water bottle>, me);
```
and add a field to hold the name reference
```
public static RegistryObject<Potion> <name>;
```
replacing `<name>` with the actual name you wish to name it internally. There are other `regPotion<X>` methods for different circumstances.

You can then add a translation for a display name in `src/main/resources/assets/ef396/lang/en_us.json` like, although this would lead to a merge collision, but a simple one.
```
  "item.minecraft.potion.effect.<name>": "Potion of <name>",
  "item.minecraft.splash_potion.effect.<name>": "Splash Potion of <name>",
  "item.minecraft.lingering_potion.effect.<name>": "Lingering Potion of <name>",
```

## Adding a Simple Block
Create a class
```
package uk.co.kring.ef396.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class <name> extends Block {

    public <name>() {
        super(Block.Properties.of(Material.METAL)
                .requiresCorrectToolForDrops()
                .strength(5.0f, 6.0f)
                .sound(SoundType.METAL));
    }
}
```
Inside the method `blocks(RegistryMap<Item> reg)` include the following code
```
reg.regBlockItem(<name> = reg.register("<name>", <name>::new), tab);
```
and add a field to hold the name reference
```
public static RegistryObject<Block> <name>;
```
replacing `<name>` with the actual name you wish to name it internally. You can modify the properties in the class file which extends `Block`.

Auto `runData`: In addition to items a `Block` needs a blockstate in `src/main/resources/assets/ef396/blockstates/<name>.json` which contains for simple blocks
```
{
  "variants": {
    "": { "model": "ef396:block/<name>" }
  }
}
```
as it allows selection of the model based on the state of the block.

Auto `runData`: Then create a file in `src/main/resources/assets/ef396/models/block` called `<name>.json` with the content
```
{
  "parent": "block/cube_all",
  "textures": {
    "all": "ef396:blocks/<name>"
  }
}
```
which then references a texture `<name>.png` file (of size 16 by 16 px) in `src/main/resources/assets/ef396/textures/blocks` and that completes adding a simple block.

The `runData` generation can be skipped for a block by `implements ComplexBlock` and then you make your own blockstate and model files. 

You can then add a translation for a display name in `src/main/resources/assets/ef396/lang/en_us.json` like, although this would lead to a merge collision, but a simple one.
```
"block.ef396.<name>": "<displayed name>",
```

Here's where I changed `src/main/resources/data/minecraft/worldgen/configured_feature/ore_diamond_small.json` to intercept small diamond ore production to make ruby ore.

## Adding a Simple Mob
Create a class
```
package uk.co.kring.ef396.entities;

public class <name> extends HogEntity {

}
```

You may also want to override the class `HogInitials` to provide initializations for the entity, and perhaps perform configuration by the `Configurator`.

Inside the method `entities(RegistryMap<EntityType<?>> reg)` include the following code
```
<name>SpawnEgg = reg.regEggItem(<name> = reg.regMob("<name>", <name>::new, <name-initials>::new), tab);
```
and add a field to hold the name reference
```
public static RegistryObject<EntityType<HogEntity>> <name>;
```
replacing `<name>` with the actual name you wish to name it internally.

A texture `<name>.png` file (a `Humanoid` mob skin) in `src/main/resources/assets/ef396/textures/entity` called `<name>.png` completes adding a simple mob. The mob can also have fashion by using a folder and returning the fashion name with `getFashion`.

You can then add a translation for a display name in `src/main/resources/assets/ef396/lang/en_us.json` like, although this would lead to a merge collision, but a simple one.
```
"item.ef396.<name>_spawn_egg": "<name> Spawn Egg",

"entity.ef396.<name>": "<name>",
```

## Adding a Sound Effect
Inside the method `items(RegistryMap<SoundEvent> reg)` include the following code
```
<name> = reg.regSound("<name>");
```
and add a field to hold the name reference
```
public static RegistryObject<SoundEvent> <name>;
```
replacing `<name>` with the actual name you wish to name it internally.

Then in `src/main/resources/assets/ef396/sounds.json` add
```
  "<name>": {
    "sounds": [
      "ef396:<name-file>"
    ],
    "subtitle": "subtitles.ef396.<name>"
  }
```
and finally add the `.ogg` file in `src/main/resources/assets/ef396/sounds` so it can be included in the mod. Multiple sounds can be made into a pool for one <name> sound.

You can then add a translation for a display subtitle name in `src/main/resources/assets/ef396/lang/en_us.json` like, although this would lead to a merge collision, but a simple one.
```
"subtitles.ef396.<name>": "<display name>",
```

## Adding a Recipe
In `src/main/resources/data/ef396/recipes` add a file based on the `<name>.json` and include something like
```
{
  "type": "minecraft:crafting_shaped",
  "pattern":
  [
    "   ",
    "x x",
    "x x"
  ],
  "key":
  {
    "x":
    {
      "tag": "ef396:ruby"
    }
  },
  "result":
  {
    "item": "ef396:ruby_boots",
    "count": 1
  }
}
```
A useful tool for recipes and drops is found [Here](https://misode.github.io/loot-table/) with loot tables being located `src/main/resources/data/ef396/loot_tables/blocks` for blocks.
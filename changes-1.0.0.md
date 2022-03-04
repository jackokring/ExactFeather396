# Change Log 1.0.0
### The TechnoVision TurtyWurty Remix

[Release Page](https://github.com/jackokring/ExactFeather396/releases)

* Fixed basic faults with `1.18.1` changes in the symbol set.
* Removed `Ore` generation as `1.18.1` has `data` tags for doing this kind of thing like generating biomes. I assume this is to stop failure of vanilla biomes by block pollution. A `Biome` maybe added later to test this idea.
* Internal `ForgeSpawnEggItem` automation.
* Class refactoring when necessary or logical.
* Removed `Oven` so need new container start. I'm likely to do some `Potion` thing.
* Added `RegistryMap` for index hashing. The class is synchronized for read only with an entries cache. Can return a `Optional<T>` via `getOptional()` for better coding style.
* Added `PriorityHashMap` for first entry priority. Includes `overwrite()` for `super.put()` behaviour. Also includes `getViolations()` for a `List` of the pretenders given a key.
* `PoisonAppleItem` replaces unmapped items. Uses configuration. Stacks to 33 items.
* `RubyBlock` replaces unmapped blocks.
* `HogEntity` replaces unmapped mobs. Temptation via `(ItemLike) Potions.THICK`. Basic texture replace on a `Zombie` as this was easier to engineer as the `Animal` renderer seems more complex. Perhaps this is for the best as the mechanic of reproduction is then open for differentiation.
* `error.ogg` replaces unmapped sounds.
* Added tree view of data directory.
* `Configurator` class easy implements a lot of the boilerplate for configuration files in a reduced number of classes.
* Added `Registries` for global access to all without having to use specific class name too.
* `Potion` added.
* Started on a `BaseCodeException` system for AI processing.
* Added a `BedtimeBook` idea.

### TODO
* Implement some AI.
* Name potion. Decide effects.
* Maybe a custom effect.
* Tags for classification, and generable in world?
* Does `Potions.MUNDANE` have a field from whence it was made?
* An `Exporter` class loading a chain from a config string?
* 
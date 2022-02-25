# Change Log 1.0.0
### The TechnoVision TurtyWurty Remix

[Release Page](https://github.com/jackokring/ExactFeather396/releases)

* Fixed basic faults with `1.18.1` changes in the symbol set.
* Removed `Ore` generation as `1.18.1` has `data` tags for doing this kind of thing like generating biomes. I assume this is to stop failure of vanilla biomes by block pollution. A `Biome` maybe added later to test this idea.
* Internal `ForgeSpawnEggItem` automation.
* Class refactoring.
* Removed `Oven` so need new container start.
* Added `RegistryMap` for index hashing.
* Added `PriorityHashMap` for first entry priority. Includes `overwrite()` for `super.put()` behaviour.
* `PoisonAppleItem` replaces unmapped items.
* `RubyBlock` replaces unmapped blocks.
* `HogEntity` replaces unmapped mobs.
* `error` replaces unmapped sounds.
* Added tree view of data directory.

### TODO

* Created workarounds when current code not possible due to version bump.
* Fixed the `Hog` to the latest code examples.
* Implement some AI.
# Registrate Refabricated [![License](https://img.shields.io/github/license/tterrag1098/Registrate?cacheSeconds=36000)](https://www.tldrlegal.com/l/mpl-2.0) ![Minecraft Version](https://img.shields.io/badge/minecraft-1.18.2-blue)

A powerful wrapper for creating and registering objects in your mod. 
Now ported to Fabric/Quilt.

This is an unofficial port. Please do not report issues to the Forge version.

Having trouble? Open an issue, or join us in the [Create Discord](https://discord.gg/hmaD7Se)'s #devchat.

## Why Registrate?

- Allows you to organize your mod content however you like, rather than having pieces of each object defined in scattered places
- Simple fluent API
- Open to extension, build and register custom objects and data
- Automatic data generation with sane defaults
- Shadeable, contains no mod, only code

## How to Use

First, create a `Registrate` object which will be used across your entire project.

```java
public static final Registrate REGISTRATE = Registrate.create(MOD_ID);
```

Using a constant field is not necessary, it can be passed around and thrown away after registration is setup.

Next, begin adding objects.

If you have a block class such as

```java
public class MyBlock extends Block {

    public MyBlock(Block.Properties properties) {
        super(properties);
    }
    
    ...
}
```

then register it like so,

```java
public static final RegistryEntry<MyBlock> MY_BLOCK = REGISTRATE.block("my_block", MyBlock::new).register();
```

Registrate will create a block, with a default simple blockstate, model, loot table, and lang entry. However, all of these facets can be configured easily to use whatever custom data you may want. Example:

```java
public static final RegistryEntry<MyStairsBlock> MY_STAIRS = REGISTRATE.block("my_block", MyStairsBlock::new)
            .defaultItem()
            .tag(BlockTags.STAIRS)
            .blockstate(ctx -> ctx.getProvider()
                .stairsBlock(ctx.getEntry(), ctx.getProvider().modLoc(ctx.getName())))
            .lang("Special Stairs")
            .register();
```

This customized version will create a BlockItem (with its own default model and lang entry), add the block to a tag, configure the blockstate for stair properties, and add a custom localization.

Finally, when you're done with registration, you must call `REGISTRATE.register();` to finalize everything.

```java
public class MyMod implements ModInitializer {
	public static final Registrate REGISTRATE = Registrate.create(MOD_ID);
	
	public void onInitialize() {
        MyModBlocks.init();
        ...
        REGISTRATE.register();
    }
}
```

For data generation, Your mod must have a `DataGeneratorEntrypoint`. From here, create an `ExistingFileHelper`
and call `REGISTRATE.setupDatagen(generator, helper);`.

```java
public class MyModDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		ExistingFileHelper helper = ExistingFileHelper.standard();
		MyMod.REGISTRATE.setupDatagen(generator, helper);
	}
}
```

To get an overview of the different APIs and methods, check out the [Javadocs](https://ci.tterrag.com/job/Registrate/job/1.16/javadoc/). For more advanced usage, read the [wiki](https://github.com/tterrag1098/Registrate/wiki) (WIP).

## Project Setup

Registrate can easily be depended on like any other Fabric mod.
You should include Registrate in your mod, so it doesn't need to be downloaded manually.

```groovy
repositories {
    // Registrate Refabricated is hosted on this maven.
    maven { url = "https://mvn.devos.one/snapshots/" }
}

...

dependencies {
    // depend on and include Registrate.
    modImplementation(include("com.tterrag.registrate_fabric:Registrate:${project.registrate_version}"))
}
```
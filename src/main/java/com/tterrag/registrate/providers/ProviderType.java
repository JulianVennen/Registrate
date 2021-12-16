package com.tterrag.registrate.providers;

import java.util.Map;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;
import com.tterrag.registrate.util.nullness.FieldsAreNonnullByDefault;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a type of data that can be generated, and specifies a factory for the provider.
 * <p>
 * Used as a key for data generator callbacks.
 * <p>
 * This file also defines the built-in provider types, but third-party types can be created with {@link #register(String, ProviderType)}.
 *
 * @param <T>
 *            The type of the provider
 */
@FunctionalInterface
@SuppressWarnings("deprecation")
@FieldsAreNonnullByDefault
//@ParametersAreNonnullByDefault
public interface ProviderType<T extends RegistrateProvider> {

    // SERVER DATA
    public static final ProviderType<RegistrateRecipeProvider> RECIPE = register("recipe", (p, i) -> new RegistrateRecipeProvider(p, i.generator()));
    public static final ProviderType<RegistrateAdvancementProvider> ADVANCEMENT = register("advancement", (p, i) -> new RegistrateAdvancementProvider(p, i.generator()));
    public static final ProviderType<RegistrateLootTableProvider> LOOT = register("loot", ((p, i) -> new RegistrateLootTableProvider(p, i.generator())));
    public static final ProviderType<RegistrateTagsProvider<Block>> BLOCK_TAGS = register("tags/block", type -> (p, g) -> new RegistrateTagsProvider<Block>(p, type, "blocks", g.generator(), Registry.BLOCK));
    public static final ProviderType<RegistrateItemTagsProvider> ITEM_TAGS = registerDelegate("tags/item", type -> (p, g, existing) -> new RegistrateItemTagsProvider(p, type, "items", g.generator(), (RegistrateTagsProvider<Block>)existing.get(BLOCK_TAGS)));
    public static final ProviderType<RegistrateTagsProvider<Fluid>> FLUID_TAGS = register("tags/fluid", type -> (p, g) -> new RegistrateTagsProvider<Fluid>(p, type, "fluids", g.generator(), Registry.FLUID));
    public static final ProviderType<RegistrateTagsProvider<EntityType<?>>> ENTITY_TAGS = register("tags/entity", type -> (p, g) -> new RegistrateTagsProvider<EntityType<?>>(p, type, "entity_types", g.generator(), Registry.ENTITY_TYPE));

    // CLIENT DATA
    public static final ProviderType<RegistrateBlockstateProvider> BLOCKSTATE = register("blockstate", (p, i) -> new RegistrateBlockstateProvider(p, i.generator(), i.helper()));
    public static final ProviderType<RegistrateItemModelProvider> ITEM_MODEL = register("item_model", (p, i, existing) -> new RegistrateItemModelProvider(p, i.generator(), ((RegistrateBlockstateProvider)existing.get(BLOCKSTATE)).getExistingFileHelper()));
    public static final ProviderType<RegistrateLangProvider> LANG = register("lang", (p, i) -> new RegistrateLangProvider(p, i.generator()));

    T create(AbstractRegistrate<?> parent, RegistrateDataProvider.DataInfo info, Map<ProviderType<?>, RegistrateProvider> existing);

    // TODO this is clunky af
    @NotNull
    static <T extends RegistrateProvider> ProviderType<T> registerDelegate(String name, NonNullUnaryOperator<ProviderType<T>> type) {
        ProviderType<T> ret = new ProviderType<T>() {

            @Override
            public T create(@NotNull AbstractRegistrate<?> parent, RegistrateDataProvider.DataInfo info, Map<ProviderType<?>, RegistrateProvider> existing) {
                return type.apply(this).create(parent, info, existing);
            }
        };
        return register(name, ret);
    }

    @NotNull
    static <T extends RegistrateProvider> ProviderType<T> register(String name, NonNullFunction<ProviderType<T>, NonNullBiFunction<AbstractRegistrate<?>, RegistrateDataProvider.DataInfo, T>> type) {
        ProviderType<T> ret = new ProviderType<T>() {
            
            @Override
            public T create(@NotNull AbstractRegistrate<?> parent, RegistrateDataProvider.DataInfo info, Map<ProviderType<?>, RegistrateProvider> existing) {
                return type.apply(this).apply(parent, info);
            }
        };
        return register(name, ret);
    }
    
    @NotNull
    static <T extends RegistrateProvider> ProviderType<T> register(String name, NonNullBiFunction<AbstractRegistrate<?>, RegistrateDataProvider.DataInfo, T> type) {
        ProviderType<T> ret = new ProviderType<T>() {
            
            @Override
            public T create(AbstractRegistrate<?> parent, RegistrateDataProvider.DataInfo info, Map<ProviderType<?>, RegistrateProvider> existing) {
                return type.apply(parent, info);
            }
        };
        return register(name, ret);
    }

    @NotNull
    static <T extends RegistrateProvider> ProviderType<T> register(String name, ProviderType<T> type) {
        RegistrateDataProvider.TYPES.put(name, type);
        return type;
    }
}

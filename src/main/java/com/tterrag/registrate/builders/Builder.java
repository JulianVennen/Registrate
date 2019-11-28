package com.tterrag.registrate.builders;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.minecraft.tags.Tag;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * A Builder creates registry entries. A Builder instance has a constant name which will be used for the resultant object, they cannot be reused for different names. It holds a parent object that will
 * be returned from some final methods.
 * <p>
 * When a builder is completed via {@link #register()} or {@link #build()}, the object will be lazily registered (through the owning {@link Registrate} object).
 * 
 * @param <R>
 *            Type of the registry for the current object. This is the concrete base class that all registry entries must extend, and the type used for the forge registry itself.
 * @param <T>
 *            Actual type of the object being built.
 * @param <P>
 *            Type of the parent object, this is returned from {@link #build()} and {@link #getParent()}.
 * @param <S>
 *            Self type
 */
public interface Builder<R extends IForgeRegistryEntry<R>, T extends R, P, S extends Builder<R, T, P, S>> {

    /**
     * Complete the current entry, and return the {@link RegistryObject} that will supply the built entry once it is available. The builder can be used afterwards, and changes made will reflect the
     * output, as long as it is before registration takes place (before forge registry events).
     * 
     * @return The {@link RegistryObject} supplying the built entry.
     */
    RegistryObject<T> register();

    /**
     * The owning {@link Registrate} that created this builder.
     * 
     * @return the owner {@link Registrate}
     */
    Registrate getOwner();

    /**
     * The parent object.
     * 
     * @return the parent object of this builder
     */
    P getParent();

    /**
     * The name of the entry being created, and combined with the mod ID of the parent {@link Registrate}, the registry name.
     * 
     * @return the name of the current entry
     */
    String getName();

    /**
     * Allows retrieval of the built entry. Mostly used internally by builder classes.
     *
     * @param registryType
     *            a {@link Class} representing the type of the registry for this builder
     * @return a {@link Supplier} to the created object, which will return null if not registered yet, and throw an exception if no such entry exists.
     * @see Registrate#get(Class)
     */
    default Supplier<T> get(Class<? super R> registryType) {
        return () -> getOwner().<R, T> get(getName(), registryType).get();
    }

    /**
     * Add a data provider callback for this entry, which will be invoked when the provider of the given type executes.
     * <p>
     * This is mostly unneeded, and instead helper methods for specific data types should be used when possible.
     * 
     * @param <D>
     *            The type of provider
     * @param type
     *            The {@link ProviderType} for the desired provider
     * @param registryType
     *            A {@link Class} representing the type of the registry for this builder
     * @param cons
     *            The callback to execute when the provider is run
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    default <D extends RegistrateProvider> S addData(ProviderType<D> type, Class<? super R> registryType, Consumer<DataGenContext<D, R, T>> cons) {
        getOwner().addDataGenerator(getName(), type, prov -> cons.accept(DataGenContext.from(prov, this, registryType)));
        return (S) this;
    }

    /**
     * Tag this entry with a tag of the correct type.
     * 
     * @param type
     *            The provider type (which must be a tag provider)
     * @param registryType
     *            A {@link Class} representing the type of the registry for this builder
     * @param tag
     *            The tag to use
     * @return this {@link Builder}
     */
    default S tag(ProviderType<RegistrateTagsProvider<R>> type, Class<? super R> registryType, Tag<R> tag) {
        return addData(type, registryType, ctx -> ctx.getProvider().getBuilder(tag).add(get(registryType).get()));
    }

    /**
     * Apply a transformation to this {@link Builder}. Useful to apply helper methods within a fluent chain, e.g.
     * 
     * <pre>
     * {@code
     * public static final RegistryObject<MyBlock> MY_BLOCK = REGISTRATE.object("my_block")
     *         .block(MyBlock::new)
     *         .transform(Utils::defaultBlockProperties)
     *         .register();
     * }
     * </pre>
     * 
     * @param <R2>
     * @param <T2>
     * @param <P2>
     * @param <S2>
     * @param func
     *            The {@link Function function} to apply
     * @return the {@link Builder} returned by the given function
     */
    @SuppressWarnings("unchecked")
    default <R2 extends IForgeRegistryEntry<R2>, T2 extends R2, P2, S2 extends Builder<R2, T2, P2, S2>> S2 transform(Function<S, S2> func) {
        return func.apply((S) this);
    }

    /**
     * Register the entry and return the parent object. The {@link RegistryObject} will be created but not returned. It can be retrieved later with {@link Registrate#get(Class)} or
     * {@link Registrate#get(String, Class)}.
     * 
     * @return the parent object
     */
    default P build() {
        register(); // Ignore return value
        return getParent();
    }
}

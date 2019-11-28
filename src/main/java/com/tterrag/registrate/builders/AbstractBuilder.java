package com.tterrag.registrate.builders;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.tags.Tag;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Base class which most builders should extend, instead of implementing [@link {@link Builder} directly.
 * <p>
 * Provides the most basic functionality, and some utility methods that remove the need to pass the registry class.
 *
 * @param <R>
 * @param <T>
 * @param <P>
 * @param <S>
 * @see Builder
 */
@RequiredArgsConstructor
public abstract class AbstractBuilder<R extends IForgeRegistryEntry<R>, T extends R, P, S extends AbstractBuilder<R, T, P, S>> implements Builder<R, T, P, S> {

    @Getter(onMethod = @__({ @Override }))
    private final Registrate owner;
    @Getter(onMethod = @__({ @Override }))
    private final P parent;
    @Getter(onMethod = @__({ @Override }))
    private final String name;
    @Getter(AccessLevel.PROTECTED)
    private final BuilderCallback callback;
    private final Class<? super R> registryType;

    /**
     * Create the built entry. This method will be lazily resolved at registration time, so it is safe to bake in values from the builder.
     * 
     * @return The built entry
     */
    protected abstract T createEntry();

    @Override
    public RegistryObject<T> register() {
        return callback.accept(name, registryType, this::createEntry);
    }

    /**
     * Allows retrieval of the built entry. Mostly used internally by builder classes.
     *
     * @return a {@link Supplier} to the created object, which will return null if not registered yet, and throw an exception if no such entry exists.
     * @see Registrate#get(Class)
     */
    public Supplier<T> get() {
        return get(registryType);
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
     * @param cons
     *            The callback to execute when the provider is run
     * @return this {@link Builder}
     */
    public <D extends RegistrateProvider> S addData(ProviderType<D> type, Consumer<DataGenContext<D, R, T>> cons) {
        return addData(type, registryType, cons);
    }

    /**
     * Set the lang key for this entry to the default value (specified by {@link RegistrateLangProvider#getAutomaticName(Supplier)}). Generally, specific helpers from concrete builders should be used
     * instead.
     * 
     * @param langKeyProvider
     *            A function to get the translation key from the entry
     * @return this {@link Builder}
     */
    public S lang(Function<T, String> langKeyProvider) {
        return lang(langKeyProvider, RegistrateLangProvider::getAutomaticName);
    }

    /**
     * Set the lang key for this entry to the specified name. Generally, specific helpers from concrete builders should be used instead.
     * 
     * @param langKeyProvider
     *            A function to get the translation key from the entry
     * @param name
     *            The name to use
     * @return this {@link Builder}
     */
    public S lang(Function<T, String> langKeyProvider, String name) {
        return lang(langKeyProvider, (p, s) -> name);
    }

    private S lang(Function<T, String> langKeyProvider, BiFunction<RegistrateLangProvider, Supplier<T>, String> localizedNameProvider) {
        return addData(ProviderType.LANG, ctx -> ctx.getProvider().add(langKeyProvider.apply(ctx.getEntry()), localizedNameProvider.apply(ctx.getProvider(), ctx::getEntry)));
    }

    /**
     * Tag this entry with a tag of the correct type.
     * 
     * @param type
     *            The provider type (which must be a tag provider)
     * @param tag
     *            The tag to use
     * @return this {@link Builder}
     */
    public S tag(ProviderType<RegistrateTagsProvider<R>> type, Tag<R> tag) {
        return tag(type, registryType, tag);
    }
}

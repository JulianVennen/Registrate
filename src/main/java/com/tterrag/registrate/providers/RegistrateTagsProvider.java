package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.Registry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;

public class RegistrateTagsProvider<T> extends FabricTagProvider<T> implements RegistrateProvider {

    private final AbstractRegistrate<?> owner;
    private final ProviderType<? extends RegistrateTagsProvider<T>> type;

    public RegistrateTagsProvider(AbstractRegistrate<?> owner, ProviderType<? extends RegistrateTagsProvider<T>> type, String name, FabricDataGenerator generatorIn, Registry<T> registryIn) {
        super(generatorIn, registryIn, name, "Tags (" + name + ")");
        this.owner = owner;
        this.type = type;
    }

    @Override
    protected void generateTags() {
        owner.genData(type, this);
    }

    @Override
    public EnvType getSide() {
        return EnvType.SERVER;
    }

    @Override
    public TagAppender<T> tag(TagKey<T> tag) { return super.tag(tag); }

    public FabricTagBuilder<T> Tag(TagKey<T> tag) { return (FabricTagBuilder<T>) super.tag(tag); }

    @Override
    public Tag.Builder getOrCreateRawBuilder(TagKey<T> tag) { return super.getOrCreateRawBuilder(tag); }
}

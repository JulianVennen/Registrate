package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RegistrateTagsProvider<T> extends FabricTagProvider<T> implements RegistrateProvider {

    private final AbstractRegistrate<?> owner;
    private final ProviderType<? extends RegistrateTagsProvider<T>> type;
    private final String name;

    public RegistrateTagsProvider(AbstractRegistrate<?> owner, ProviderType<? extends RegistrateTagsProvider<T>> type, String name, FabricDataGenerator generatorIn, Registry<T> registryIn) {
        super(generatorIn, registryIn);
        this.owner = owner;
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return "Tags (" + name + ")";
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

    @Override
    public TagBuilder getOrCreateRawBuilder(TagKey<T> tag) { return super.getOrCreateRawBuilder(tag); }
}

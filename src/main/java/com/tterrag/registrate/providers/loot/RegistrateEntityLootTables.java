package com.tterrag.registrate.providers.loot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.tterrag.registrate.AbstractRegistrate;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class RegistrateEntityLootTables extends SimpleFabricLootTableProvider implements RegistrateLootTables {

    private final AbstractRegistrate<?> parent;
    private final Consumer<RegistrateEntityLootTables> callback;

    private final Map<ResourceLocation, Builder> entries = new HashMap<>();

    public RegistrateEntityLootTables(AbstractRegistrate<?> parent, Consumer<RegistrateEntityLootTables> callback, FabricDataGenerator dataGenerator) {
        super(dataGenerator, LootContextParamSets.ENTITY);
        this.parent = parent;
        this.callback = callback;
    }
    @Override
    public void accept(BiConsumer<ResourceLocation, Builder> consumer) {
        callback.accept(this);
        entries.forEach(consumer);
    }

    public void add(EntityType<?> type, LootTable.Builder table) {
        entries.put(type.getDefaultLootTable(), table);
    }

    public void add(ResourceLocation id, LootTable.Builder table) {
        entries.put(id, table);
    }

}

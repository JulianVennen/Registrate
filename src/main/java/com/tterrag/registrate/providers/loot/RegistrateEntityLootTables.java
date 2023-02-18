package com.tterrag.registrate.providers.loot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.tterrag.registrate.AbstractRegistrate;

import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.Registry;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
        entries.put(Registry.ENTITY_TYPE.getKey(type), table);
    }

    public void add(ResourceLocation id, LootTable.Builder table) {
        entries.put(id, table);
    }

}

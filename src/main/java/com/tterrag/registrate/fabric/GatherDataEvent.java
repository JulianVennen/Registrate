package com.tterrag.registrate.fabric;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraftforge.common.data.ExistingFileHelper;

public interface GatherDataEvent {
    Event<GatherDataEvent> EVENT = EventFactory.createArrayBacked(GatherDataEvent.class, callbacks -> ((generator, helper) -> {
        for(GatherDataEvent event : callbacks)
            event.gatherData(generator, helper);
    }));

    void gatherData(FabricDataGenerator generator, ExistingFileHelper helper);
}

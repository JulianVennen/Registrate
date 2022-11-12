package com.tterrag.registrate.test.mod;

import com.tterrag.registrate.fabric.GatherDataEvent;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class TestModDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		String[] existingData = System.getProperty("com.tterrag.registrate.existingData").split(";");
		ExistingFileHelper helper = new ExistingFileHelper(Arrays.stream(existingData).map(Paths::get).toList(), Collections.emptySet(),
				true, null, null);
		GatherDataEvent.EVENT.invoker().gatherData(generator, helper);
	}
}

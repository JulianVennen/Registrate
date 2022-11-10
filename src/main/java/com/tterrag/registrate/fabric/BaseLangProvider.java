package com.tterrag.registrate.fabric;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.HashMap;
import java.util.Map;

public class BaseLangProvider extends FabricLanguageProvider {
	private final Map<String, String> entries = new HashMap<>();

	protected BaseLangProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	protected BaseLangProvider(FabricDataGenerator dataGenerator, String languageCode) {
		super(dataGenerator, languageCode);
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		entries.forEach(translationBuilder::add);
	}

	public void add(String key, String value) {
		entries.put(key, value);
	}
}

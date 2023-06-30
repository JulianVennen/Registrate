package com.tterrag.registrate.fabric;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.HashMap;
import java.util.Map;

public class BaseLangProvider extends FabricLanguageProvider {
	private final Map<String, String> entries = new HashMap<>();

	protected BaseLangProvider(FabricDataOutput output) {
		super(output);
	}

	protected BaseLangProvider(FabricDataOutput output, String languageCode) {
		super(output, languageCode);
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		entries.forEach(translationBuilder::add);
	}

	public void add(String key, String value) {
		entries.put(key, value);
	}
}

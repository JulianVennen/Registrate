package com.tterrag.registrate.test.mod;

import net.fabricmc.api.ModInitializer;

public class TestModInit implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println("test mod loading!");
		new TestMod().init();
	}
}

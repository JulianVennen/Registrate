package com.tterrag.registrate.fabric;

import java.util.function.Consumer;

import net.minecraft.core.Registry;

public class RegistryUtil {
	public static void forAllRegistries(Consumer<Registry<?>> consumer) {
		// Fluid, Block, and Item need to run first
		consumer.accept(Registry.FLUID);
		consumer.accept(Registry.BLOCK);
		consumer.accept(Registry.ITEM);
		Registry.REGISTRY.forEach(registry -> {
			if (registry != Registry.FLUID && registry != Registry.BLOCK && registry != Registry.ITEM) {
				consumer.accept(registry);
			}
		});
	}
}

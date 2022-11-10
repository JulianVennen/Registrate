package com.tterrag.registrate.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(LootTableProvider.class)
public class LootTableProviderMixin {
	@ModifyReceiver(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", remap = false))
	private List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> registrate$runCustomGeneration(
			List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> subProviders) {
		if ((Object) this instanceof RegistrateLootTableProvider registrate) {
			return registrate.getTables();
		}
		return subProviders;
	}

	@ModifyExpressionValue(method = "run", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;difference(Ljava/util/Set;Ljava/util/Set;)Lcom/google/common/collect/Sets$SetView;", remap = false))
	private Iterator<ResourceLocation> registrate$preventVanillaValidationReporting(Iterator<ResourceLocation> difference) {
		if ((Object) this instanceof RegistrateLootTableProvider registrate) {
			return new ArrayList<ResourceLocation>().iterator(); // empty, do nothing
		}
		return difference;
	}

	@WrapWithCondition(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 0, remap = false))
	private boolean registrate$preventVanillaValidation(Map<ResourceLocation, LootTable> map, BiConsumer<ResourceLocation, LootTable> consumer) {
		// if registrate, don't validate
		return !((Object) this instanceof RegistrateLootTableProvider registrate);
	}

	@Inject(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 0, remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
	private void registrate$doCustomValidation(CachedOutput output, CallbackInfo ci, Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
		if ((Object) this instanceof RegistrateLootTableProvider registrate) {
			registrate.validate(map, validationContext);
		}
	}
}

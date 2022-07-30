package com.tterrag.registrate.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.tterrag.registrate.providers.loot.RegistrateEntityLootTables;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;

@Mixin(EntityLoot.class)
public class EntityLootMixin {
	@Unique
	private final boolean registrate$isNotRegistrate = ((Object) this instanceof RegistrateEntityLootTables) ^ true; // weird mixin thing prevents this being a simple inversion

	@WrapWithCondition(
			method = "accept(Ljava/util/function/BiConsumer;)V",
			at = {
					@At(
							value = "INVOKE",
							target = "Lnet/minecraft/data/loot/EntityLoot;add(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V"
					),
					@At(
							value = "INVOKE",
							target = "Lnet/minecraft/data/loot/EntityLoot;add(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/storage/loot/LootTable$Builder;)V"
					)
			}
	)
	private boolean registrate$preventVanillaAdditions(EntityLoot self, @Coerce Object arg, Builder builder) {
		return registrate$isNotRegistrate;
	}

	@Inject(method = "accept(Ljava/util/function/BiConsumer;)V", at = @At("HEAD"))
	private void registrate$addCustom(BiConsumer<ResourceLocation, Builder> biConsumer, CallbackInfo ci) {
		if ((Object) this instanceof RegistrateEntityLootTables registrate) {
			registrate.addTables();
		}
	}

	@ModifyVariable(
			method = "accept(Ljava/util/function/BiConsumer;)V",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;",
							remap = false
					)
			),
			at = @At("STORE")
	)
	private Iterator<EntityType<?>> registrate$modifyEntityList(Iterator<EntityType<?>> original) {
		if ((Object) this instanceof RegistrateEntityLootTables registrate) {
			return registrate.getKnownEntities().iterator();
		}
		return original;
	}

	// isNonLiving replaces the contains() check and the == MISC check on forge
	// to replicate this, we need to modify both checks to be true, since they're &&'d.
	// note that contains() is also inverted.

	private boolean registrate$isNonLiving = false;

	@Inject(
			method = "accept(Ljava/util/function/BiConsumer;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/EntityType;getDefaultLootTable()Lnet/minecraft/resources/ResourceLocation;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void registrate$setNonLivingStatus(BiConsumer<ResourceLocation, Builder> biConsumer, CallbackInfo ci,
											   Set<ResourceLocation> set, Iterator<EntityType<?>> registryItr, EntityType<?> type) {
		if ((Object) this instanceof RegistrateEntityLootTables registrate) {
			this.registrate$isNonLiving = registrate.isNonLiving(type); // we don't need to worry about resetting this at the end since it's set for every entity
		}
	}

	@ModifyExpressionValue(
			method = "accept(Ljava/util/function/BiConsumer;)V",
			at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z")
	)
	private boolean registrate$customNonLivingContains(boolean contains) {
		if (this.registrate$isNonLiving) {
			return false; // true, but this call is inverted, so we have to invert
		}
		return contains;
	}

	@ModifyExpressionValue(
			method = "accept(Ljava/util/function/BiConsumer;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;getCategory()Lnet/minecraft/world/entity/MobCategory;")
	)
	private MobCategory registrate$customNonLivingCategory(MobCategory actualCategory) {
		if (this.registrate$isNonLiving) {
			return MobCategory.MISC; // checks if == MISC, this makes it true
		}
		return actualCategory;
	}
}

package com.tterrag.registrate.builders;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.fabric.RegistryObject;
import com.tterrag.registrate.fabric.ScreenHandlerRegistryExtension;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import org.jetbrains.annotations.Nullable;

public class MenuBuilder<T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>,  P> extends AbstractBuilder<MenuType<?>, MenuType<T>, P, MenuBuilder<T, S, P>> {
    
    public interface MenuFactory<T extends AbstractContainerMenu> {
        
        T create(MenuType<T> type, int windowId, Inventory inv);
    }

    public interface ForgeMenuFactory<T extends AbstractContainerMenu> {

        T create(MenuType<T> type, int windowId, Inventory inv, @Nullable FriendlyByteBuf buffer);
    }
    
    public interface ScreenFactory<M extends AbstractContainerMenu, T extends Screen & MenuAccess<M>> {
        
        T create(M menu, Inventory inv, Component displayName);
    }

    private final MenuFactory<T> factory;
    private final ForgeMenuFactory<T> forgeFactory;
    private final NonNullSupplier<ScreenFactory<T, S>> screenFactory;

    public MenuBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, MenuFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
        this(owner, parent, name, callback, (type, windowId, inv, $) -> factory.create(type, windowId, inv), screenFactory);
    }

    public MenuBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ForgeMenuFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
        super(owner, parent, name, callback, MenuType.class);
        this.forgeFactory = factory;
        this.factory = null;
        this.screenFactory = screenFactory;
    }

    @Override
    protected @NonnullType MenuType<T> createEntry() {
        NonNullSupplier<MenuType<T>> supplier = this.asSupplier();
        MenuType<T> ret;
        ScreenHandlerRegistryExtension.createOnly = true;
        if (this.factory == null) {
            ForgeMenuFactory<T> factory = this.forgeFactory;
            ret = ScreenHandlerRegistry.registerExtended(null, (windowId, inv, buf) -> factory.create(supplier.get(), windowId, inv, buf));
        } else {
            MenuFactory<T> factory = this.factory;
            ret = ScreenHandlerRegistry.registerSimple(null, (syncId, inventory) -> factory.create(supplier.get(), syncId, inventory));
        }
        EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
            ScreenFactory<T, S> screenFactory = this.screenFactory.get();
            ScreenRegistry.<T, S>register(ret, (type, inv, displayName) -> screenFactory.create(type, inv, displayName));
        });
        return ret;
    }

    @Override
    protected RegistryEntry<MenuType<T>> createEntryWrapper(RegistryObject<MenuType<T>> delegate) {
        return new MenuEntry<>(getOwner(), delegate);
    }

    @Override
    public MenuEntry<T> register() {
        return (MenuEntry<T>) super.register();
    }
}

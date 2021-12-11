package com.tterrag.registrate.mixin;

import com.tterrag.registrate.fabric.FluidData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.level.material.Fluid;

@Mixin(Fluid.class)
public class FluidMixin implements FluidData.FluidAttributes {

    @Unique
    private FluidData data;

    @Unique
    @Override
    public FluidData getData() {
        return data;
    }

    @Override
    public void setData(FluidData data) {
        this.data = data;
    }
}

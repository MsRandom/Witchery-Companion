package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker.nonrecipes.CauldronHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 Mixins:
 [Integration] Crafttweaker integration to add new Heat Sources to the Witch's Cauldron
 */
@Mixin(value = TileEntityCauldron.class)
public abstract class TileEntityCauldronMixin {


    /** Since Witchery has hardcoded check to if the block below is FIRE, we will use this
     * as the "true" return value for the method. Anything else is considered "false"
     */
    @WrapOperation(method = "update", remap = true,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getBlock()Lnet/minecraft/block/Block;", remap = true))
    public Block WPallowOtherHeatSources(IBlockState instance, Operation<Block> original) {
        return CauldronHandler.isHeatSource(instance) ? Blocks.FIRE : Blocks.AIR;
    }

}

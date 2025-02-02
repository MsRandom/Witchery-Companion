package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.msrandom.witchery.block.entity.TileEntityCursedBlock;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.brewing.ModifiersImpact;
import net.msrandom.witchery.brewing.action.BrewActionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 Mixins:
 [Bugfix] Fix crash when adding or updating a curse to a trigger block
 */
@Mixin(value = TileEntityCursedBlock.class)
public abstract class TileEntityCursedBlockMixin extends WitcheryTileEntity {

    @Shadow(remap = false)
    public BrewActionList actionList;

    @Shadow(remap = false)
    public int count;

    @Shadow(remap = false)
    public int duration;

    @Shadow(remap = false)
    public int expansion;

    @Shadow(remap = false)
    public UUID thrower;

    @Inject(method = "writeToNBT", remap = true,
            cancellable = true, at = @At("HEAD"))
    private void WPfixNullActionList(NBTTagCompound nbtRoot, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.cursedBlock_fixNullActionListCrash){
            if (actionList == null) {
                super.writeToNBT(nbtRoot);
                cir.setReturnValue(nbtRoot);
            }
        }
    }

    @Inject(method = "updateCurse", remap = false,
            cancellable = true, at = @At("HEAD"))
    private void WPfixUpdateCurse(ModifiersImpact impactModifiers, BrewActionList actionList, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.cursedBlock_fixNullActionListCrash) {
            if (this.actionList != null && this.actionList.equals(actionList)) {
                ++this.count;
            } else {
                this.actionList = actionList;
                this.count = 1;
                this.duration = impactModifiers.lifetime.get() >= 0 ? 5 + impactModifiers.lifetime.get() * impactModifiers.lifetime.get() * 5 : 100;
                this.expansion = Math.min(4 + impactModifiers.extent.get(), 10);
                if (impactModifiers.thrower != null) {
                    this.thrower = impactModifiers.thrower.getUniqueID();
                }
            }
        }
    }

}

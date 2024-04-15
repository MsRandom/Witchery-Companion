package com.smokeythebandicoot.witcherypatcher.mixins.brewing.action.effect;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.BrewActionEffect;
import net.msrandom.witchery.brewing.action.effect.BrewEffectSerializer;
import net.msrandom.witchery.brewing.action.effect.ErosionBrewEffect;
import net.msrandom.witchery.brewing.action.effect.FrogsTongueBrewEffect;
import net.msrandom.witchery.util.EntityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fixes brew of frogs tongue crashing null entity being passed if player drinks the brew
 */
@Mixin(value = FrogsTongueBrewEffect.class, remap = false)
public class FrogsTongueBrewEffectMixin extends BrewActionEffect {
    private FrogsTongueBrewEffectMixin(BrewEffectSerializer<?> serializer, boolean invertible) {
        super(serializer, invertible);
    }

    @Inject(method = "doApplyToEntity", at = @At("HEAD"), remap = false, cancellable = true)
    private void WPdoApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack actionStack, CallbackInfo cbi) {
        if (ModConfig.MixinConfig.MixinBugfixes.BrewsFixes.fixFrongsTongueBew) {
            if (targetEntity == null) {
                cbi.cancel();
            }
        }

    }
}
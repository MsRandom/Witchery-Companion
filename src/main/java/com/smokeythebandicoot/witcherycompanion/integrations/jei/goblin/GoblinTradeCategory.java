package com.smokeythebandicoot.witcherycompanion.integrations.jei.goblin;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.api.GoblinTradeApi;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GoblinTradeCategory extends BaseRecipeCategory<GoblinTradeWrapper> {

    public static boolean enabled = true;
    public static String UID = "witchery.goblin_trade";
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/goblin_trade.png");

    public GoblinTradeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 22, 124, 22);
        localizedName = new TextComponentTranslation("witcherycompanion.gui.goblin_trade.name").getFormattedText();
    }

    public  static void register(IRecipeCategoryRegistration registry) {
        enabled = ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiGoblinTrades;
        if (!enabled) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new GoblinTradeCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {
        if (!enabled) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<GoblinTradeWrapper> getRecipes(IGuiHelper guiHelper) {
        List<GoblinTradeWrapper> recipes = new ArrayList<>();

        for (MerchantRecipe goblinTrade : GoblinTradeApi.getTrades(0)) {
            recipes.add(new GoblinTradeWrapper(guiHelper, goblinTrade));
        }

        return recipes;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    public void setRecipe(IRecipeLayout recipeLayout, GoblinTradeWrapper tradeWrapper, IIngredients ingredients) {
        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 21, 2);
        guiItemStacks.init(1, true, 45, 2);
        guiItemStacks.init(2, false, 100, 2);

        guiItemStacks.set(0, inputs.get(0));
        guiItemStacks.set(1, inputs.get(1));
        guiItemStacks.set(2, outputs.get(0));
    }

}

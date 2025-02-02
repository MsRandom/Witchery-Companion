package com.smokeythebandicoot.witcherycompanion.integrations.api;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class is responsible to Bridge between Witchery and Integrations (Crafttweaker, Groovy (eventually))
 * It keeps track of the Goblin trades and provides them to Witchery. Integrations can modify this class
 * Mixins will inject the trades contained here in the EntityGoblin class
 */
public class GoblinTradeApi {

    private static final List<GoblinProfession> professions = new ArrayList<>();

    /** =============================== TRADE GENERATION =============================== **/
    public static MerchantRecipeList generateTrades(int professionId) {
        GoblinProfession profession = professions.get(professionId);
        return profession.generateActualTrades(null);
    }

    public static MerchantRecipeList generateTrades(World world, int professionId) {
        GoblinProfession profession = professions.get(professionId);
        return profession.generateActualTrades(world);
    }

    public static MerchantRecipeList getTrades(int professionId) {
        GoblinProfession profession = professions.get(professionId);
        return profession.getAllTrades();
    }

    /** =============================== PROFESSION RETRIEVAL =============================== **/
    public static int getRandomProfessionID(Random random) {
        if (professions.isEmpty()) return -1;
        return random.nextInt(professions.size());
    }

    public static GoblinProfession getRandomProfession(Random random) {
        return professions.get(getRandomProfessionID(random));
    }

    public static GoblinProfession getProfessionByName(String professionName) {
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName)) {
                return profession;
            }
        }
        return null;
    }

    public static List<String> listProfessions() {
        List<String> list = new ArrayList<>();
        for (GoblinProfession profession : professions) {
            list.add(profession.professionName);
        }
        return list;
    }

    /** =============================== PROFESSION MANIP =============================== **/
    /** Registers a new profession with a name and no fallback trade. Returns false if a profession with that name
     already exists */
    public static boolean registerProfession(String professionName) {
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName)) return false;
        }
        GoblinProfession newProfession = new GoblinProfession(professionName, null);
        professions.add(newProfession);
        return true;
    }

    /** Un-registers a previously-registered profession. Returns false if the profession did not exist */
    public static boolean unregisterProfession(String professionName) {
        GoblinProfession toRemove = null;
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName)) {
                toRemove = profession;
            }
        }
        if (toRemove == null) return false;
        professions.remove(toRemove);
        return true;
    }

    /** =============================== TRADES MANIP =============================== **/
    /** Sets the fallback trade for this profession. Returns false if the profession does not exist */
    public static boolean setProfessionFallbackTrade(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.fallBackTrade = new GoblinTrade(buy1, buy2, sell, 1.0f);
        return true;
    }

    /** Removes the fallback trade for this profession. Returns false if the profession does not exist */
    public static boolean removeProfessionFallbackTrade(String professionName) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.fallBackTrade = null;
        return true;
    }

    /** Adds a new trade to the profession */
    public static boolean addTradeToProfession(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.addTrade(new GoblinTrade(buy1, buy2, sell, 1.0f));
        return true;
    }

    /** Adds a new trade to the profession. Specifies the chance that the Goblin has to get the recipe */
    public static boolean addTradeToProfession(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell, Float chance) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.addTrade(new GoblinTrade(buy1, buy2, sell, chance));
        return true;
    }

    /** Removes the trade from the profession. Use null as wildcards */
    public static boolean removeTrade(String professionName, Ingredient buy1, Ingredient buy2, Ingredient sell, Float chance) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.removeTradeByMatching(buy1, buy2, sell, chance);
        return true;
    }

    public static class GoblinProfession {

        // The name of the profession
        public String professionName;

        // In case no trades are available, this is the fallback one
        public GoblinTrade fallBackTrade;

        // A list of possible trades and the probability to select one of them
        private final List<GoblinTrade> possibleTrades;

        public GoblinProfession(String professionName, GoblinTrade fallBackTrade) {
            this.fallBackTrade = fallBackTrade;
            this.professionName = professionName;
            possibleTrades = new ArrayList<>();
        }

        /** Adds a new trade for this profession */
        public void addTrade(GoblinTrade trade) {
            possibleTrades.add(trade);
        }

        /** Removes a trade */
        public void removeTrade(GoblinTrade trade) {
            possibleTrades.remove(trade);
        }

        /** Removes a trade that matches the elements. Use null for wildcard, use Items.AIR for empty stack */
        public void removeTradeByMatching(Ingredient buy1, Ingredient buy2, Ingredient sell, Float chance) {
            List<GoblinTrade> toRemove = new ArrayList<GoblinTrade>();
            for (GoblinTrade trade : possibleTrades) {
                MerchantRecipe t = trade.getTrade();

                // If trade matches, add it to removal list. Null is considered a wildcard, while Empty itemstack
                // matches an empty item in the trade (Buy 1 + Empty -> Sell)
                if ((buy1 == null || buy1.apply(t.getItemToBuy())) &&
                    (buy2 == null || buy2.apply(t.getSecondItemToBuy())) &&
                    (sell == null || sell.apply(t.getItemToSell())) &&
                    (chance == null || chance == trade.getChance())
                ) {
                    toRemove.add(trade);
                }
            }

            for (GoblinTrade trade : toRemove) {
                possibleTrades.remove(trade);
            }
        }

        /** Generates a list of trades based on current random context and the list of current possible trades. Use
         Integer.MAX_VALUE to get all trades */
        public MerchantRecipeList generateActualTrades(World world) {
            // Init random generator
            Random random;
            if (world == null) random = new Random();
            else random = world.rand;

            // Init result list
            MerchantRecipeList tradeList = new MerchantRecipeList();

            for (GoblinTrade trade : possibleTrades) {
                if (random.nextFloat() < trade.getChance()) {
                    tradeList.add(trade.genNewTrade());
                }
            }

            // Check fallback
            if (tradeList.isEmpty() && fallBackTrade != null) {
                tradeList.add(fallBackTrade.genNewTrade());
            }

            return tradeList;
        }

        /** Returns all trades that are possible with this profession. Use Integer.MAX_VALUE to get all trades */
        public MerchantRecipeList getAllTrades() {
            MerchantRecipeList tradeList = new MerchantRecipeList();
            for (GoblinTrade trade : possibleTrades) {
                tradeList.add(trade.genNewTrade());
            }
            if (fallBackTrade != null) tradeList.add(fallBackTrade.genNewTrade());
            return tradeList;
        }


    }

    public static class GoblinTrade {

        private final MerchantRecipe trade;
        private final float probability;

        public GoblinTrade(ItemStack buy1, ItemStack buy2, ItemStack sell, Float probability) {

            this.probability = probability == null ? 1.0f : probability;
            if (buy1 == null) buy1 = new ItemStack(Items.AIR);
            if (buy2 == null) buy2 = new ItemStack(Items.AIR);
            if (sell == null) sell = new ItemStack(Items.AIR);
            this.trade = new MerchantRecipe(buy1, buy2, sell);

        }

        public MerchantRecipe getTrade() {
            return trade;
        }

        public MerchantRecipe genNewTrade() {
            return new MerchantRecipe(this.trade.getItemToBuy(), this.trade.getSecondItemToBuy(), this.trade.getItemToSell());
        }

        public float getChance() {
            return probability;
        }

    }


}

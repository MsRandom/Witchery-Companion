package com.smokeythebandicoot.witcherypatcher;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = WitcheryPatcher.MODID,
        version = WitcheryPatcher.MODVERSION,
        name = WitcheryPatcher.MODNAME,
        useMetadata = true
)
public class WitcheryPatcher implements ILateMixinLoader {

    public static final String MODID = "witcherypatcher";
    public static final String MODNAME = "Witchery Patcher";
    public static final String MODVERSION = "1.0";
    public static final String MODDESCRIPTION = "A mod to fix bugs and performance issues in Witchery:Resurrected";
    public static final String MODAUTHOR = "SmokeyTheBandicoot";
    public static final String MODCREDITS = "";
    public static final String MODURL = "";
    public static final String MODLOGO = "assets/witcherypatcher/logo.png";

    public static Logger logger;
    @Instance(value = MODID)
    public static WitcheryPatcher instance;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        // Set mod info
        event.getModMetadata().autogenerated = false;
        event.getModMetadata().name = MODID;
        event.getModMetadata().credits = MODCREDITS;
        event.getModMetadata().authorList.clear();
        event.getModMetadata().authorList.add(MODAUTHOR);
        event.getModMetadata().description = MODDESCRIPTION;
        event.getModMetadata().url = MODURL;
        event.getModMetadata().logoFile = MODLOGO;


    }

    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();
        configs.add("witcherypatcher.mixins.json");
        return configs;
    }

}
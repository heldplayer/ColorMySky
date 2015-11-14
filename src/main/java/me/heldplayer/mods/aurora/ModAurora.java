package me.heldplayer.mods.aurora;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.specialattack.forge.core.ModInfo;
import net.specialattack.forge.core.SpACoreMod;
import net.specialattack.forge.core.SpACoreProxy;
import net.specialattack.forge.core.config.ConfigManager;
import net.specialattack.forge.core.config.Configuration;

@Mod(name = Objects.MOD_NAME, modid = Objects.MOD_ID, dependencies = Objects.MOD_DEPENCIES, guiFactory = Objects.GUI_FACTORY)
public class ModAurora extends SpACoreMod {

    @Instance(value = Objects.MOD_ID)
    public static ModAurora instance;
    @SidedProxy(clientSide = Objects.CLIENT_PROXY, serverSide = Objects.SERVER_PROXY)
    public static CommonProxy proxy;

    public static Config config;
    public static ConfigManager configManager;

    @Configuration("colormysky.cfg")
    public static class Config {

        @Configuration.Option(category = "client", side = Configuration.CSide.CLIENT)
        @Configuration.DoubleMinMax(min = 0.0D, max = 1.0D)
        public double intensity = 1.0F;

        @Configuration.Option(category = "client.moon_phases", side = Configuration.CSide.CLIENT)
        public boolean newMoon = true;

        @Configuration.Option(category = "client.moon_phases", side = Configuration.CSide.CLIENT)
        public boolean waxingCrescent = false;

        @Configuration.Option(category = "client.moon_phases", side = Configuration.CSide.CLIENT)
        public boolean firstQuarter = true;

        @Configuration.Option(category = "client.moon_phases", side = Configuration.CSide.CLIENT)
        public boolean waxingGibbous = false;

        @Configuration.Option(category = "client.moon_phases", side = Configuration.CSide.CLIENT)
        public boolean fullMoon = true;

        @Configuration.Option(category = "client.moon_phases", side = Configuration.CSide.CLIENT)
        public boolean waningGibbous = false;

        @Configuration.Option(category = "client.moon_phases", side = Configuration.CSide.CLIENT)
        public boolean lastQuarter = true;

        @Configuration.Option(category = "client.moon_phases", side = Configuration.CSide.CLIENT)
        public boolean waningCrescent = false;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Objects.log = event.getModLog();

        ModAurora.configManager = ConfigManager.registerConfig(ModAurora.config = new Config());

        super.preInit(event);
    }

    @Override
    public ModInfo getModInfo() {
        return Objects.MOD_INFO;
    }

    @Override
    public SpACoreProxy[] getProxies() {
        return new SpACoreProxy[] { ModAurora.proxy };
    }
}

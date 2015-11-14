package me.heldplayer.mods.aurora;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.specialattack.forge.core.SpACoreProxy;

public class CommonProxy extends SpACoreProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        FMLInterModComms.sendMessage("Mystcraft", "API", "me.heldplayer.mods.aurora.integration.mystcraft.MystcraftIntegration.register");
    }
}

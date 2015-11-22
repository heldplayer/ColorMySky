package blue.heldplayer.mods.aurora;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.specialattack.forge.core.SpACoreProxy;

public class CommonProxy extends SpACoreProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        //FMLInterModComms.sendMessage("Mystcraft", "API", "me.heldplayer.mods.aurora.integration.mystcraft.MystcraftIntegration.register");
    }

    public void reloadEffects() {
    }
}

package blue.heldplayer.mods.aurora;

import net.specialattack.forge.core.ModInfo;
import org.apache.logging.log4j.Logger;

/**
 * HeldsPeripherals mod Objects
 */
public class Objects {

    public static final String MOD_ID = "colormysky";
    public static final String MOD_NAME = "Bring Color to my Skies";
    public static final ModInfo MOD_INFO = new ModInfo(Objects.MOD_ID, Objects.MOD_NAME);
    public static final String MOD_DEPENCIES = "required-after:spacore@[01.06.00,)";
    public static final String PACKAGE_ROOT = "blue.heldplayer.mods.aurora";
    public static final String CLIENT_PROXY = Objects.PACKAGE_ROOT + ".client.ClientProxy";
    public static final String SERVER_PROXY = Objects.PACKAGE_ROOT + ".CommonProxy";
    public static final String GUI_FACTORY = Objects.PACKAGE_ROOT + ".client.gui.GuiFactory";

    public static Logger log;

}

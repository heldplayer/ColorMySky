package blue.heldplayer.mods.aurora.client.gui;

import blue.heldplayer.mods.aurora.ModAurora;
import blue.heldplayer.mods.aurora.Objects;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfiguration extends GuiConfig {

    public GuiConfiguration(GuiScreen parent) {
        super(parent, new ArrayList<IConfigElement>(ModAurora.configManager.categories.values()), Objects.MOD_ID, false, false, "I'm Recording Mod Configuration");
    }
}

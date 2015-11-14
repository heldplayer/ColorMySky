package me.heldplayer.mods.aurora.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import java.util.ArrayList;
import me.heldplayer.mods.aurora.ModAurora;
import me.heldplayer.mods.aurora.Objects;
import net.minecraft.client.gui.GuiScreen;

public class GuiConfiguration extends GuiConfig {

    public GuiConfiguration(GuiScreen parent) {
        super(parent, new ArrayList<IConfigElement>(ModAurora.configManager.categories.values()), Objects.MOD_ID, false, false, "I'm Recording Mod Configuration");
    }
}

package me.heldplayer.mods.aurora.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;

public class SkyRendererAurora extends IRenderHandler {

    private IRenderHandler parent;
    private SphereRenderer sky = new SphereRenderer(200.0F, 3);

    public SkyRendererAurora(IRenderHandler parent) {
        this.parent = parent;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        this.parent.render(partialTicks, world, mc);
        Aurora.renderAurora(partialTicks, world, mc, this.sky);
    }
}

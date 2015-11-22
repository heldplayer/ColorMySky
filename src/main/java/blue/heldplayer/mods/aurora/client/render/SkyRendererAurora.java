package blue.heldplayer.mods.aurora.client.render;

import blue.heldplayer.mods.aurora.client.selection.EffectSelector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.client.IRenderHandler;
import net.specialattack.forge.core.client.MC;

public class SkyRendererAurora extends IRenderHandler {

    public IRenderHandler parent;
    public EffectSelector[] selectors;
    private SphereRenderer sky = new SphereRenderer(200.0F, 3);

    public SkyRendererAurora(IRenderHandler parent, EffectSelector[] selectors) {
        this.parent = parent;
        this.selectors = selectors;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        if (this.parent != null) {
            this.parent.render(partialTicks, world, mc);
        }
        this.renderAdds(partialTicks, world, mc);
    }

    protected void renderAdds(float partialTicks, WorldClient world, Minecraft mc) {
        if (this.selectors != null) {
            Profiler profiler = MC.getMc().mcProfiler;
            profiler.startSection("colormysky:render-sky");
            for (EffectSelector selector : this.selectors) {
                selector.resetFast();
                if (selector.canRender() && selector.isValidNowFast(world, mc.thePlayer)) {
                    float brightness = selector.getBrightness(world, mc.thePlayer);
                    selector.getEffect().render(partialTicks, world, mc, brightness);
                }
            }
            profiler.endSection();
        }
    }
}

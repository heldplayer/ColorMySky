package blue.heldplayer.mods.aurora.client.effect;

import blue.heldplayer.mods.aurora.ModAurora;
import blue.heldplayer.mods.aurora.client.ClientProxy;
import blue.heldplayer.mods.aurora.client.render.SphereRenderer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.specialattack.forge.core.client.shader.ShaderProgram;

public class FullColorSky implements Effect {

    private SphereRenderer skybox = new SphereRenderer(200.0F, 3);
    private float maxBrightness = 1.0F;

    @Override
    public boolean canRender() {
        return ClientProxy.auroraShader != null && ClientProxy.auroraShader.getShader() != null;
    }

    private float getBrightness(float partialTicks, WorldClient world) {
        float angle = world.getCelestialAngle(partialTicks);
        float brightness = 1.0F - ((float) Math.cos(angle * Math.PI * 2.0F) * 2F + 0.65F);

        if (brightness < 0.0F) {
            brightness = 0.0F;
        }

        if (brightness > 1.0F) {
            brightness = 1.0F;
        }

        return brightness * brightness * 0.5F;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc, float brightness) {
        float starBrightness = this.maxBrightness * brightness * (this.getBrightness(partialTicks, world) * (1.0F - world.getRainStrength(partialTicks)) - 0.1F);
        if (starBrightness > 0.0F) {
            ShaderProgram shader = ClientProxy.auroraShader.getShader();
            shader.bind();
            shader.getUniform("brightness").set1(starBrightness * 2.5F * (float) ModAurora.config.intensity);

            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.skybox.render();
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            GlStateManager.enableCull();
            GlStateManager.enableTexture2D();

            shader.unbind();
        }
    }

    @Override
    public void load(JsonObject object) {
        JsonElement maxBrightness = object.get("max-brightness");
        if (maxBrightness != null && maxBrightness.isJsonPrimitive() && maxBrightness.getAsJsonPrimitive().isNumber()) {
            this.maxBrightness = maxBrightness.getAsFloat();
        }
    }
}

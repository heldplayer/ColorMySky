package me.heldplayer.mods.aurora.client.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.heldplayer.mods.aurora.ModAurora;
import me.heldplayer.mods.aurora.client.ClientProxy;
import me.heldplayer.mods.aurora.client.render.SphereRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.shader.ShaderProgram;
import org.lwjgl.opengl.GL11;

public class FullColorSky implements Effect {

    private SphereRenderer skybox = new SphereRenderer(200.0F, 3);
    private float maxBrightness = 1.0F;

    @Override
    public boolean canRender() {
        return ClientProxy.auroraShader != null && ClientProxy.auroraShader.getShader() != null;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc, float brightness) {
        float starBrightness = this.maxBrightness * brightness * (world.getStarBrightness(partialTicks) * (1.0F - world.getRainStrength(partialTicks)) - 0.1F);
        if (starBrightness > 0.0F) {
            ShaderProgram shader = ClientProxy.auroraShader.getShader();
            shader.bind();
            shader.getUniform("brightness").set1(starBrightness * 2.5F * (float) ModAurora.config.intensity);

            float horizon = (float) mc.thePlayer.getPosition(partialTicks).yCoord;
            GLState.glDisable(GL11.GL_TEXTURE_2D);
            GLState.glDisable(GL11.GL_CULL_FACE);
            GLState.glDisable(GL11.GL_FOG);
            GLState.glDisable(GL11.GL_ALPHA_TEST);
            GLState.glEnable(GL11.GL_BLEND);
            GLState.glDepthMask(false);
            GLState.glPushMatrix();
            GLState.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.skybox.render();
            GLState.glPopMatrix();
            GLState.glDepthMask(true);
            GLState.glDisable(GL11.GL_BLEND);
            GLState.glEnable(GL11.GL_ALPHA_TEST);
            GLState.glEnable(GL11.GL_FOG);
            GLState.glEnable(GL11.GL_CULL_FACE);
            GLState.glEnable(GL11.GL_TEXTURE_2D);

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

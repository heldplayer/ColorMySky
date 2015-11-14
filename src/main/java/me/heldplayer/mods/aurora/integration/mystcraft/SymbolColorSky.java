package me.heldplayer.mods.aurora.integration.mystcraft;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ICelestial;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.heldplayer.mods.aurora.ModAurora;
import me.heldplayer.mods.aurora.client.ClientProxy;
import me.heldplayer.mods.aurora.client.render.SphereRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.shader.ShaderProgram;
import org.lwjgl.opengl.GL11;

public class SymbolColorSky implements IAgeSymbol {

    private String[] words;

    public SymbolColorSky() {
        this.words = new String[] { WordData.Celestial, WordData.Image, WordData.Chaos, WordData.Power };
    }

    @Override
    public void registerLogic(AgeDirector ageDirector, long seed) {
        ageDirector.registerInterface(new CelestialObject());
    }

    @Override
    public int instabilityModifier(int i) {
        return 0;
    }

    @Override
    public String identifier() {
        return "ColorfulSky";
    }

    @Override
    public String displayName() {
        return StatCollector.translateToLocal("colormysky:symbol.myst.colorfulsky");
    }

    @Override
    public String[] getPoem() {
        return this.words;
    }

    private static class CelestialObject implements ICelestial {

        @SideOnly(Side.CLIENT)
        private SphereRenderer sky;

        @Override
        public boolean providesLight() {
            return false;
        }

        @Override
        public float getAltitudeAngle(long l, float v) {
            return 0.5F;
        }

        @Override
        public Long getTimeToDawn(long l) {
            return null;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(TextureManager textureManager, World world, float partialTicks) {
            boolean hasShader = ClientProxy.auroraShader != null && ClientProxy.auroraShader.getShader() != null;
            float starBrightness = world.getStarBrightness(partialTicks) * (1.0F - world.getRainStrength(partialTicks)) - 0.1F;
            if (hasShader && starBrightness > 0.0F) {
                if (this.sky == null) {
                    this.sky = new SphereRenderer(200.0F, 3);
                }
                ShaderProgram shader = ClientProxy.auroraShader.getShader();
                shader.bind();
                shader.getUniform("brightness").set1(starBrightness * 2.5F * (float) ModAurora.config.intensity);

                GLState.glDisable(GL11.GL_TEXTURE_2D);
                //GLState.glDisable(GL11.GL_CULL_FACE);
                //GLState.glDisable(GL11.GL_FOG);
                //GLState.glDisable(GL11.GL_ALPHA_TEST);
                //GLState.glEnable(GL11.GL_BLEND);
                //GLState.glDepthMask(false);
                GLState.glPushMatrix();
                GLState.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.sky.render();
                GLState.glPopMatrix();
                //GLState.glDepthMask(true);
                //GLState.glDisable(GL11.GL_BLEND);
                //GLState.glEnable(GL11.GL_ALPHA_TEST);
                //GLState.glEnable(GL11.GL_FOG);
                //GLState.glEnable(GL11.GL_CULL_FACE);
                GLState.glEnable(GL11.GL_TEXTURE_2D);

                shader.unbind();
            }
        }
    }
}

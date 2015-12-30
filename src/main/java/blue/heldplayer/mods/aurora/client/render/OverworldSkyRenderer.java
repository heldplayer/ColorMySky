package blue.heldplayer.mods.aurora.client.render;

import blue.heldplayer.mods.aurora.client.selection.EffectSelector;
import java.lang.reflect.Method;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.specialattack.forge.core.client.MC;
import org.lwjgl.opengl.GL11;

public class OverworldSkyRenderer extends SkyRendererAurora {

    private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");

    private static boolean starsMade = false;
    private static int starGLCallList;
    private static int glSkyList;
    private static int glSkyList2;

    private static VertexFormat vertexBufferFormat;
    private static VertexBuffer starVBO;
    private static VertexBuffer skyVBO;
    private static VertexBuffer sky2VBO;
    private static boolean vboEnabled = false;

    private static void initStars() {
        if (OverworldSkyRenderer.starsMade) {
            return;
        }
        OverworldSkyRenderer.vertexBufferFormat = new VertexFormat();
        OverworldSkyRenderer.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        OverworldSkyRenderer.vboEnabled = OpenGlHelper.useVbo();
        OverworldSkyRenderer.generateStars();
        OverworldSkyRenderer.generateSky();
        OverworldSkyRenderer.generateSky2();
    }

    private static void generateSky2() {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        if (OverworldSkyRenderer.sky2VBO != null) {
            OverworldSkyRenderer.sky2VBO.deleteGlBuffers();
        }

        if (OverworldSkyRenderer.glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(OverworldSkyRenderer.glSkyList2);
            OverworldSkyRenderer.glSkyList2 = -1;
        }

        if (OverworldSkyRenderer.vboEnabled) {
            OverworldSkyRenderer.sky2VBO = new VertexBuffer(OverworldSkyRenderer.vertexBufferFormat);
            OverworldSkyRenderer.renderSky(worldrenderer, -16.0F, true);
            worldrenderer.finishDrawing();
            worldrenderer.reset();
            OverworldSkyRenderer.sky2VBO.bufferData(worldrenderer.getByteBuffer());
        } else {
            OverworldSkyRenderer.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(OverworldSkyRenderer.glSkyList2, GL11.GL_COMPILE);
            OverworldSkyRenderer.renderSky(worldrenderer, -16.0F, true);
            tessellator.draw();
            GL11.glEndList();
        }
    }


    private static void generateSky() {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        if (OverworldSkyRenderer.skyVBO != null) {
            OverworldSkyRenderer.skyVBO.deleteGlBuffers();
        }

        if (OverworldSkyRenderer.glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(OverworldSkyRenderer.glSkyList);
            OverworldSkyRenderer.glSkyList = -1;
        }

        if (OverworldSkyRenderer.vboEnabled) {
            OverworldSkyRenderer.skyVBO = new VertexBuffer(OverworldSkyRenderer.vertexBufferFormat);
            OverworldSkyRenderer.renderSky(worldrenderer, 16.0F, false);
            worldrenderer.finishDrawing();
            worldrenderer.reset();
            OverworldSkyRenderer.skyVBO.bufferData(worldrenderer.getByteBuffer());
        } else {
            OverworldSkyRenderer.glSkyList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(OverworldSkyRenderer.glSkyList, GL11.GL_COMPILE);
            OverworldSkyRenderer.renderSky(worldrenderer, 16.0F, false);
            tessellator.draw();
            GL11.glEndList();
        }
    }

    private static void renderSky(WorldRenderer worldRendererIn, float p_174968_2_, boolean p_174968_3_) {
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

        for (int i = -384; i <= 384; i += 64) {
            for (int j = -384; j <= 384; j += 64) {
                float f1 = (float) i;
                float f2 = (float) (i + 64);

                if (p_174968_3_) {
                    f2 = (float) i;
                    f1 = (float) (i + 64);
                }

                worldRendererIn.pos((double) f1, (double) p_174968_2_, (double) j).endVertex();
                worldRendererIn.pos((double) f2, (double) p_174968_2_, (double) j).endVertex();
                worldRendererIn.pos((double) f2, (double) p_174968_2_, (double) (j + 64)).endVertex();
                worldRendererIn.pos((double) f1, (double) p_174968_2_, (double) (j + 64)).endVertex();
            }
        }
    }

    private static void generateStars() {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        if (OverworldSkyRenderer.starVBO != null) {
            OverworldSkyRenderer.starVBO.deleteGlBuffers();
        }

        if (OverworldSkyRenderer.starGLCallList >= 0) {
            GLAllocation.deleteDisplayLists(OverworldSkyRenderer.starGLCallList);
            OverworldSkyRenderer.starGLCallList = -1;
        }

        if (OverworldSkyRenderer.vboEnabled) {
            OverworldSkyRenderer.starVBO = new VertexBuffer(OverworldSkyRenderer.vertexBufferFormat);
            OverworldSkyRenderer.renderStars(worldrenderer);
            worldrenderer.finishDrawing();
            worldrenderer.reset();
            OverworldSkyRenderer.starVBO.bufferData(worldrenderer.getByteBuffer());
        } else {
            OverworldSkyRenderer.starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GL11.glNewList(OverworldSkyRenderer.starGLCallList, GL11.GL_COMPILE);
            OverworldSkyRenderer.renderStars(worldrenderer);
            tessellator.draw();
            GL11.glEndList();
            GlStateManager.popMatrix();
        }
    }

    private static void renderStars(WorldRenderer worldRendererIn) {
        Random random = new Random(10842L);
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

        for (int i = 0; i < 1500; ++i) {
            double d0 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d3 = (double) (0.15F + random.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j) {
                    double d18 = (double) ((j & 2) - 1) * d3;
                    double d19 = (double) ((j + 1 & 2) - 1) * d3;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    worldRendererIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                }
            }
        }
    }

    private SphereRenderer sky = new SphereRenderer(200.0F, 3);

    public OverworldSkyRenderer(EffectSelector[] selectors) {
        super(null, selectors);
        OverworldSkyRenderer.initStars();
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        this.renderDefaultSky(partialTicks, world, mc);
        this.renderAdds(partialTicks, world, mc);
    }

    private static Method getFOVModifier;

    static {
        OverworldSkyRenderer.getFOVModifier = ReflectionHelper.findMethod(EntityRenderer.class, MC.getEntityRenderer(), new String[] { "getFOVModifier", "func_78481_a" }, float.class, boolean.class);
    }

    private void renderDefaultSky(float partialTicks, WorldClient world, Minecraft mc) {
        GlStateManager.disableTexture2D();
        Vec3 vec3 = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
        float f1 = (float) vec3.xCoord;
        float f2 = (float) vec3.yCoord;
        float f3 = (float) vec3.zCoord;

        if (mc.gameSettings.anaglyph) {
            float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        GlStateManager.color(f1, f2, f3);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(f1, f2, f3);

        if (OverworldSkyRenderer.vboEnabled) {
            OverworldSkyRenderer.skyVBO.bindBuffer();
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
            OverworldSkyRenderer.skyVBO.drawArrays(7);
            OverworldSkyRenderer.skyVBO.unbindBuffer();
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        } else {
            GlStateManager.callList(OverworldSkyRenderer.glSkyList);
        }

        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;

        if (afloat != null) {
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            f7 = afloat[0];
            f8 = afloat[1];
            f9 = afloat[2];
            float f12;

            if (mc.gameSettings.anaglyph) {
                f10 = (f7 * 30.0F + f8 * 59.0F + f9 * 11.0F) / 100.0F;
                f11 = (f7 * 30.0F + f8 * 70.0F) / 100.0F;
                f12 = (f7 * 30.0F + f9 * 70.0F) / 100.0F;
                f7 = f10;
                f8 = f11;
                f9 = f12;
            }

            worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(0.0D, 100.0D, 0.0D).color(f7, f8, f9, afloat[3]).endVertex();

            for (int j = 0; j <= 16; ++j) {
                f12 = (float) j * (float) Math.PI * 2.0F / 16.0F;
                float f13 = MathHelper.sin(f12);
                float f14 = MathHelper.cos(f12);
                worldrenderer.pos((double) (f13 * 120.0F), (double) (f14 * 120.0F), (double) (-f14 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
        }

        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        GlStateManager.pushMatrix();
        f7 = 1.0F - world.getRainStrength(partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, f7);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        f11 = 30.0F;
        mc.getTextureManager().bindTexture(OverworldSkyRenderer.locationSunPng);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double) (-f11), 100.0D, (double) (-f11)).tex(0.0D, 0.0D).endVertex();
        worldrenderer.pos((double) f11, 100.0D, (double) (-f11)).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos((double) f11, 100.0D, (double) f11).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos((double) (-f11), 100.0D, (double) f11).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
        f11 = 20.0F;
        mc.getTextureManager().bindTexture(OverworldSkyRenderer.locationMoonPhasesPng);
        int k = world.getMoonPhase();
        int l = k % 4;
        int i1 = k / 4 % 2;
        float f15 = (float) l / 4.0F;
        float f16 = (float) i1 / 2.0F;
        float f17 = (float) (l + 1) / 4.0F;
        float f18 = (float) (i1 + 1) / 2.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double) (-f11), -100.0D, (double) f11).tex((double) f17, (double) f18).endVertex();
        worldrenderer.pos((double) f11, -100.0D, (double) f11).tex((double) f15, (double) f18).endVertex();
        worldrenderer.pos((double) f11, -100.0D, (double) (-f11)).tex((double) f15, (double) f16).endVertex();
        worldrenderer.pos((double) (-f11), -100.0D, (double) (-f11)).tex((double) f17, (double) f16).endVertex();
        tessellator.draw();
        GlStateManager.disableTexture2D();
        float f19 = world.getStarBrightness(partialTicks) * f7;

        if (f19 > 0.0F) {
            GlStateManager.color(f19, f19, f19, f19);

            if (OverworldSkyRenderer.vboEnabled) {
                OverworldSkyRenderer.starVBO.bindBuffer();
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                OverworldSkyRenderer.starVBO.drawArrays(7);
                OverworldSkyRenderer.starVBO.unbindBuffer();
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            } else {
                GlStateManager.callList(OverworldSkyRenderer.starGLCallList);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F);
        double d0 = mc.thePlayer.getPositionEyes(partialTicks).yCoord - world.getHorizon();

        if (d0 < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);

            if (OverworldSkyRenderer.vboEnabled) {
                OverworldSkyRenderer.sky2VBO.bindBuffer();
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                OverworldSkyRenderer.sky2VBO.drawArrays(7);
                OverworldSkyRenderer.sky2VBO.unbindBuffer();
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            } else {
                GlStateManager.callList(OverworldSkyRenderer.glSkyList2);
            }

            GlStateManager.popMatrix();
            f10 = -((float) (d0 + 65.0D));
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(-1.0D, (double) f10, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, (double) f10, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, (double) f10, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, (double) f10, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, (double) f10, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, (double) f10, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, (double) f10, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, (double) f10, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
        }

        if (world.provider.isSkyColored()) {
            GlStateManager.color(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
        } else {
            GlStateManager.color(f1, f2, f3);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -((float) (d0 - 16.0D)), 0.0F);
        GlStateManager.callList(OverworldSkyRenderer.glSkyList2);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }
}

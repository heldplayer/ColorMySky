package me.heldplayer.mods.aurora.client;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.heldplayer.mods.aurora.CommonProxy;
import me.heldplayer.mods.aurora.client.render.OverworldSkyRenderer;
import me.heldplayer.mods.aurora.client.render.SkyRendererAurora;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.specialattack.forge.core.client.shader.ShaderCallback;
import net.specialattack.forge.core.client.shader.ShaderManager;
import net.specialattack.forge.core.client.shader.ShaderProgram;
import net.specialattack.forge.core.client.shader.ShaderUniform;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static ShaderManager.ShaderBinding auroraShader;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        ClientProxy.auroraShader = ShaderManager.getShader(new ResourceLocation("colormysky:shaders/aurora"));
        if (ClientProxy.auroraShader != null && ClientProxy.auroraShader.getShader() != null) {
            ShaderProgram shader = ClientProxy.auroraShader.getShader();
            shader.addCallback(new ShaderCallback() {

                private float time;
                private float prevPartial;

                @Override
                public void call(ShaderProgram program) {
                    ShaderUniform time = program.getUniform("time");
                    if (time != null) {
                        Timer timer = net.specialattack.forge.core.client.ClientProxy.getMinecraftTimer();
                        float partial = timer.renderPartialTicks;
                        if (partial < this.prevPartial) {
                            this.time += 1.0F - this.prevPartial + partial;
                        } else {
                            this.time += partial - this.prevPartial;
                        }
                        //while (this.time > 100.0F) {
                        //this.time -= 100.0F;
                        //}
                        this.prevPartial = partial;
                        time.set1(this.time / 300.0F);
                    }
                    ShaderUniform vertexSize = program.getUniform("vertexSize");
                    if (vertexSize != null) {
                        vertexSize.set1(600.0F);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (world instanceof WorldClient && world.provider.dimensionId == 0) {
            IRenderHandler oldRenderer = world.provider.getSkyRenderer();
            if (oldRenderer != null) {
                // Good, we can just use the previous renderer to do our dirty work
                world.provider.setSkyRenderer(new SkyRendererAurora(oldRenderer));
            } else {
                // Damn! We need to render the aurora AND the sky D:
                world.provider.setSkyRenderer(new OverworldSkyRenderer());
            }
        }
    }
}
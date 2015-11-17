package me.heldplayer.mods.aurora.client;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import me.heldplayer.mods.aurora.CommonProxy;
import me.heldplayer.mods.aurora.ModAurora;
import me.heldplayer.mods.aurora.client.effect.FullColorSky;
import me.heldplayer.mods.aurora.client.render.OverworldSkyRenderer;
import me.heldplayer.mods.aurora.client.render.SkyRendererAurora;
import me.heldplayer.mods.aurora.client.selection.BiomeSelector;
import me.heldplayer.mods.aurora.client.selection.EffectSelector;
import me.heldplayer.mods.aurora.client.selection.MoonPhaseSelector;
import me.heldplayer.mods.aurora.client.selection.logical.*;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.specialattack.forge.core.client.MC;
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

        EffectManager.registerEffectType("full-color-sky", FullColorSky.class);
        EffectManager.registerEffectSelectorType("moon-phase", MoonPhaseSelector.class);
        EffectManager.registerEffectSelectorType("biome", BiomeSelector.class);
        EffectManager.registerEffectSelectorType("or", OrConditionSelector.class);
        EffectManager.registerEffectSelectorType("and", AndConditionSelector.class);
        EffectManager.registerEffectSelectorType("nor", NorConditionSelector.class);
        EffectManager.registerEffectSelectorType("nand", NandConditionSelector.class);
        EffectManager.registerEffectSelectorType("xor", XorConditionSelector.class);
        EffectManager.registerEffectSelectorType("xnor", XnorConditionSelector.class);
        EffectManager.reloadEffects();
    }

    @Override
    public void reloadEffects() {
        super.reloadEffects();
        EffectManager.reloadEffects();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (world instanceof WorldClient) {
            ClientProxy.reworkEffect((WorldClient) world);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text event) {
        if (ModAurora.config.debug) {
            WorldClient world = MC.getWorld();
            EntityClientPlayerMP player = MC.getPlayer();
            if (world != null && world.provider != null && player != null) {
                EffectSelector[] selectors = EffectManager.getForDimension(world.provider.dimensionId);
                if (selectors != null) {
                    for (EffectSelector selector : selectors) {
                        this.debugOverlayRecursive(world, player, event.left, selector, "");
                    }
                }
            }
        }
    }

    private void debugOverlayRecursive(WorldClient world, EntityClientPlayerMP player, ArrayList<String> list, EffectSelector selector, String prefix) {
        boolean canRender = selector.isValidNowFast(world, player);
        float brightness = selector.getBrightness(world, player);
        String display = prefix + String.format("%s: %s @ %.3f", selector.getClass().getSimpleName(), canRender, brightness);
        list.add(display);
        if (selector instanceof ConditionSelector) {
            for (EffectSelector condition : ((ConditionSelector) selector).conditionals) {
                this.debugOverlayRecursive(world, player, list, condition, prefix + "  | ");
            }
        } else if (selector instanceof EffectManager.Alias) {
            this.debugOverlayRecursive(world, player, list, ((EffectManager.Alias) selector).getReference(), prefix + "->");
        }
    }

    public static void reworkEffect(WorldClient world) {
        if (world != null && world.provider != null) {
            IRenderHandler oldRenderer = world.provider.getSkyRenderer();
            EffectSelector[] selectors = EffectManager.getForDimension(world.provider.dimensionId);
            if (oldRenderer instanceof SkyRendererAurora) { // Great! This world already has an old renderer!
                if (selectors == null) { // Remove the renderer if there is nothing to render
                    world.provider.setSkyRenderer(((SkyRendererAurora) oldRenderer).parent);
                } else {
                    ((SkyRendererAurora) oldRenderer).selectors = selectors;
                }
            } else if (selectors != null) { // Only replace if there is something to render
                if (oldRenderer != null) {
                    // Good, we can just use the previous renderer to do our dirty work
                    world.provider.setSkyRenderer(new SkyRendererAurora(oldRenderer, selectors));
                } else {
                    // Damn! We need to render the aurora AND the sky D:
                    world.provider.setSkyRenderer(new OverworldSkyRenderer(selectors));
                }
            }
        }
    }
}

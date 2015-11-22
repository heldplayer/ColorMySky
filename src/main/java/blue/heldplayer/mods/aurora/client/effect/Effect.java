package blue.heldplayer.mods.aurora.client.effect;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public interface Effect {

    boolean canRender();

    void render(float partialTicks, WorldClient world, Minecraft mc, float brightness);

    void load(JsonObject object);
}

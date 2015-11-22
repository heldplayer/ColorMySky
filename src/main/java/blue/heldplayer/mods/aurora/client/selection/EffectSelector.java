package blue.heldplayer.mods.aurora.client.selection;

import com.google.gson.JsonObject;
import blue.heldplayer.mods.aurora.client.effect.Effect;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public abstract class EffectSelector {

    public abstract void load(JsonObject object);

    public Effect getEffect() {
        return null;
    }

    public boolean canRender() {
        Effect effect = this.getEffect();
        return effect != null && effect.canRender();
    }

    private Boolean fast = null;

    // Especiall useful for biomes
    public boolean isValidNowFast(WorldClient world, EntityPlayerSP player) {
        if (this.fast == null) {
            this.fast = this.isValidNow(world, player);
        }
        return this.fast;
    }

    public void resetFast() {
        this.fast = null;
    }

    public boolean isValidNow(WorldClient world, EntityPlayerSP player) {
        return false;
    }

    public float getBrightness(WorldClient world, EntityPlayerSP player) {
        return this.isValidNowFast(world, player) ? 1.0F : 0.0F;
    }
}

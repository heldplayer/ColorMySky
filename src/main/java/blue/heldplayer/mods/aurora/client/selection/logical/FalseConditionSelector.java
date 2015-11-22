package blue.heldplayer.mods.aurora.client.selection.logical;

import blue.heldplayer.mods.aurora.client.effect.Effect;
import blue.heldplayer.mods.aurora.client.selection.EffectSelector;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import blue.heldplayer.mods.aurora.client.EffectManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public class FalseConditionSelector extends EffectSelector {

    private Effect effect;

    @Override
    public void load(JsonObject object) {
        JsonElement effectElement = object.get("effect");
        if (effectElement != null && effectElement.isJsonObject()) {
            Effect effect = EffectManager.readEffect(effectElement.getAsJsonObject());
            if (effect != null) {
                this.effect = effect;
            }
        }
    }

    @Override
    public Effect getEffect() {
        return this.effect;
    }

    @Override
    public boolean isValidNow(WorldClient world, EntityPlayerSP player) {
        return false;
    }

    @Override
    public boolean isValidNowFast(WorldClient world, EntityPlayerSP playerMP) {
        return false;
    }

    @Override
    public void resetFast() {
    }

    @Override
    public float getBrightness(WorldClient world, EntityPlayerSP player) {
        return 0.0F;
    }
}

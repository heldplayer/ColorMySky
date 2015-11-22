package blue.heldplayer.mods.aurora.client.selection;

import blue.heldplayer.mods.aurora.client.effect.Effect;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import blue.heldplayer.mods.aurora.client.EffectManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public class MoonPhaseSelector extends EffectSelector {

    private boolean[] phases = new boolean[8];
    private Effect effect;

    @Override
    public void load(JsonObject object) {
        JsonElement phases = object.get("phases");
        if (phases != null) {
            if (phases.isJsonArray()) {
                JsonArray array = phases.getAsJsonArray();
                for (JsonElement element : array) {
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                        this.markActive(element.getAsString());
                    }
                }
            } else if (phases.isJsonPrimitive() && phases.getAsJsonPrimitive().isString()) {
                this.markActive(phases.getAsString());
            }
        }
        JsonElement effectElement = object.get("effect");
        if (effectElement != null && effectElement.isJsonObject()) {
            Effect effect = EffectManager.readEffect(effectElement.getAsJsonObject());
            if (effect != null) {
                this.effect = effect;
            }
        }
    }

    private void markActive(String name) {
        name = name.toLowerCase().replaceAll("[-_ ]", "");
        if (name.equals("fullmoon")) {
            this.phases[0] = true;
        } else if (name.equals("waninggibbous")) {
            this.phases[1] = true;
        } else if (name.equals("lastquarter")) {
            this.phases[2] = true;
        } else if (name.equals("waningcrescent")) {
            this.phases[3] = true;
        } else if (name.equals("newmoon")) {
            this.phases[4] = true;
        } else if (name.equals("waxingcrescent")) {
            this.phases[5] = true;
        } else if (name.equals("firstquarter")) {
            this.phases[6] = true;
        } else if (name.equals("waxinggibbous")) {
            this.phases[7] = true;
        }
    }

    @Override
    public Effect getEffect() {
        return this.effect;
    }

    @Override
    public boolean isValidNow(WorldClient world, EntityPlayerSP player) {
        return this.phases[world.getMoonPhase()];
    }
}

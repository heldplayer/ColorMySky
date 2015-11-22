package blue.heldplayer.mods.aurora.client.selection.logical;

import blue.heldplayer.mods.aurora.client.effect.Effect;
import blue.heldplayer.mods.aurora.client.selection.EffectSelector;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import blue.heldplayer.mods.aurora.client.EffectManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public abstract class ConditionSelector extends EffectSelector {

    public EffectSelector[] conditionals;
    private String brightnessStrategy = "max";
    private Effect effect;

    @Override
    public void load(JsonObject object) {
        JsonElement selections = object.get("selections");
        if (selections != null && (selections.isJsonObject() || selections.isJsonArray())) {
            List<EffectSelector> selectors = new ArrayList<EffectSelector>();
            if (selections.isJsonObject()) {
                EffectSelector effectSelector = EffectManager.readEffectSelector(selections.getAsJsonObject());
                if (effectSelector != null) {
                    selectors.add(effectSelector);
                }
            } else {
                for (JsonElement selector : selections.getAsJsonArray()) {
                    if (selector.isJsonObject()) {
                        EffectSelector effectSelector = EffectManager.readEffectSelector(selector.getAsJsonObject());
                        if (effectSelector != null) {
                            selectors.add(effectSelector);
                        }
                    }
                }
            }
            this.conditionals = selectors.toArray(new EffectSelector[selectors.size()]);
        }
        JsonElement brightnessStrategy = object.get("brightness-strategy");
        if (brightnessStrategy != null && brightnessStrategy.isJsonPrimitive() && brightnessStrategy.getAsJsonPrimitive().isString()) {
            this.brightnessStrategy = brightnessStrategy.getAsString().toLowerCase();
        }
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
    public void resetFast() {
        super.resetFast();
        for (EffectSelector selector : this.conditionals) {
            selector.resetFast();
        }
    }

    @Override
    public float getBrightness(WorldClient world, EntityPlayerSP player) {
        if (!this.isValidNowFast(world, player)) {
            return 0.0F;
        }
        if ("max".equals(this.brightnessStrategy)) {
            float max = 0.0F;
            for (EffectSelector selector : this.conditionals) {
                float brightness = Math.min(selector.getBrightness(world, player), 1.0F);
                if (brightness > max) {
                    max = brightness;
                }
            }
            return max;
        } else if ("min".equals(this.brightnessStrategy)) {
            float min = 1.0F;
            for (EffectSelector selector : this.conditionals) {
                float brightness = Math.max(selector.getBrightness(world, player), 0.0F);
                if (brightness < min) {
                    min = brightness;
                }
            }
            return min;
        } else if ("average".equals(this.brightnessStrategy)) {
            float acc = 0.0F;
            for (EffectSelector selector : this.conditionals) {
                acc += Math.max(selector.getBrightness(world, player), 0.0F);
            }
            return acc / (float) this.conditionals.length;
        } else if ("one".equals(this.brightnessStrategy)) {
            return 1.0F;
        } else if ("half".equals(this.brightnessStrategy)) {
            float acc = 0.0F;
            for (EffectSelector selector : this.conditionals) {
                acc += Math.max(selector.getBrightness(world, player), 0.0F);
            }
            return acc / (float) (this.conditionals.length << 1);
        } else if ("first".equals(this.brightnessStrategy)) {
            for (EffectSelector selector : this.conditionals) {
                if (selector.isValidNowFast(world, player)) {
                    return selector.getBrightness(world, player);
                }
            }
            return 1.0F;
        } else if ("last".equals(this.brightnessStrategy)) {
            float last = 1.0F;
            for (EffectSelector selector : this.conditionals) {
                if (selector.isValidNowFast(world, player)) {
                    last = selector.getBrightness(world, player);
                }
            }
            return last;
        }
        return 0.0F;
    }
}

package me.heldplayer.mods.aurora.client.selection.logical;

import com.google.gson.JsonObject;
import me.heldplayer.mods.aurora.client.selection.EffectSelector;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;

public class FalseConditionSelector extends EffectSelector {

    @Override
    public void load(JsonObject object) {
    }

    @Override
    public boolean isValidNow(WorldClient world, EntityClientPlayerMP player) {
        return false;
    }

    @Override
    public boolean isValidNowFast(WorldClient world, EntityClientPlayerMP playerMP) {
        return false;
    }

    @Override
    public void resetFast() {
    }

    @Override
    public float getBrightness(WorldClient world, EntityClientPlayerMP player) {
        return 0.0F;
    }
}

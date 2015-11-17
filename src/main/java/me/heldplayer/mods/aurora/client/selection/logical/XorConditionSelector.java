package me.heldplayer.mods.aurora.client.selection.logical;

import me.heldplayer.mods.aurora.client.selection.EffectSelector;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;

public class XorConditionSelector extends ConditionSelector {

    @Override
    public boolean isValidNow(WorldClient world, EntityClientPlayerMP player) {
        int count = 0;
        for (EffectSelector selector : this.conditionals) {
            if (selector.isValidNowFast(world, player)) {
                count++;
            }
        }
        return count == 1;
    }
}
package blue.heldplayer.mods.aurora.client.selection.logical;

import blue.heldplayer.mods.aurora.client.selection.EffectSelector;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public class NandConditionSelector extends ConditionSelector {

    @Override
    public boolean isValidNow(WorldClient world, EntityPlayerSP player) {
        for (EffectSelector selector : this.conditionals) {
            if (!selector.isValidNowFast(world, player)) {
                return true;
            }
        }
        return false;
    }
}

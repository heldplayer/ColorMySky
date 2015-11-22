package blue.heldplayer.mods.aurora.client.selection.logical;

import blue.heldplayer.mods.aurora.client.selection.EffectSelector;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public class XnorConditionSelector extends ConditionSelector {

    @Override
    public boolean isValidNow(WorldClient world, EntityPlayerSP player) {
        int count = 0;
        for (EffectSelector selector : this.conditionals) {
            if (selector.isValidNowFast(world, player)) {
                count++;
            }
        }
        return count != 1;
    }
}

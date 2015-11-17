package me.heldplayer.mods.aurora.integration.mystcraft;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.exception.APIUndefined;
import com.xcompwiz.mystcraft.api.exception.APIVersionRemoved;
import com.xcompwiz.mystcraft.api.exception.APIVersionUndefined;
import me.heldplayer.mods.aurora.Objects;

public class MystcraftIntegration {

    public static void register(APIInstanceProvider provider) {
        Objects.log.info("Received the Mystcraft API provider");

        MystcraftIntegration.getSymbolAPI(provider);
        MystcraftIntegration.getSymbolValsAPI(provider);
        MystcraftIntegration.getGrammarAPI(provider);
    }

    private static void getSymbolAPI(APIInstanceProvider provider) {
        try {
            Object api = provider.getAPIInstance("symbol-1");
            MystSymbol.getAPI(api);
        } catch (APIUndefined e) {
            Objects.log.error("The Mystcraft symbol API is missing", e);
        } catch (APIVersionUndefined e) {
            Objects.log.error("Mystcraft can't count to 1, missing the symbol API", e);
        } catch (APIVersionRemoved e) {
            Objects.log.error("Version 1 of the Mystcraft symbol API was removed, NEI Mystcraft Plugin needs to be updated", e);
        }
    }

    private static void getSymbolValsAPI(APIInstanceProvider provider) {
        try {
            Object api = provider.getAPIInstance("symbolvals-1");
            MystSymbol.getValsAPI(api);
        } catch (APIUndefined e) {
            Objects.log.error("The Mystcraft symbol values API is missing", e);
        } catch (APIVersionUndefined e) {
            Objects.log.error("Mystcraft can't count to 1, missing the symbol values API", e);
        } catch (APIVersionRemoved e) {
            Objects.log.error("Version 1 of the Mystcraft symbol values API was removed, NEI Mystcraft Plugin needs to be updated", e);
        }
    }

    private static void getGrammarAPI(APIInstanceProvider provider) {
        try {
            Object api = provider.getAPIInstance("grammar-1");
            MystGrammar.getAPI(api);
        } catch (APIUndefined e) {
            Objects.log.error("The Mystcraft grammar API is missing", e);
        } catch (APIVersionUndefined e) {
            Objects.log.error("Mystcraft can't count to 1, missing the grammar API", e);
        } catch (APIVersionRemoved e) {
            Objects.log.error("Version 1 of the Mystcraft grammar API was removed, NEI Mystcraft Plugin needs to be updated", e);
        }
    }
}

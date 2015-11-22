package blue.heldplayer.mods.aurora.client;

import blue.heldplayer.mods.aurora.Objects;
import blue.heldplayer.mods.aurora.client.effect.Effect;
import blue.heldplayer.mods.aurora.client.selection.EffectSelector;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.MC;
import org.apache.logging.log4j.Level;

public class EffectManager {

    private static Map<Integer, EffectSelector[]> dimensions = new HashMap<Integer, EffectSelector[]>();
    private static Map<String, EffectManager.Alias> aliases = new HashMap<String, EffectManager.Alias>();
    private static Map<String, Class<? extends Effect>> effectTypes = new HashMap<String, Class<? extends Effect>>();
    private static Map<String, Class<? extends EffectSelector>> effectSelectorTypes = new HashMap<String, Class<? extends EffectSelector>>();
    private static Gson gson = new Gson();

    public static void reloadEffects() {
        File file = new File("config" + File.separator + Objects.MOD_ID + "-effect-selection.json");
        if (!file.exists()) {
            FileOutputStream out = null;
            InputStream in = null;
            try {
                IResource resource = MC.getResourceManager().getResource(new ResourceLocation("colormysky:defaults/colormysky-effect-selection.json"));
                out = new FileOutputStream(file);
                in = resource.getInputStream();
                byte[] data = new byte[256];
                int read;
                while ((read = in.read(data)) > 0) {
                    out.write(data, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(file));
            EffectManager.loadEffects(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void loadEffects(Reader in) {
        EffectManager.dimensions.clear();
        EffectManager.aliases.clear();
        try {
            JsonArray array = new JsonParser().parse(new JsonReader(in)).getAsJsonArray();
            for (JsonElement element : array) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    JsonElement presets = obj.get("presets");
                    if (presets != null && presets.isJsonArray()) {
                        for (JsonElement preset : presets.getAsJsonArray()) {
                            if (preset.isJsonObject()) {
                                EffectManager.readEffectSelector(preset.getAsJsonObject());
                            }
                        }
                    }
                    JsonElement selections = obj.get("selections");
                    if (selections == null || (!selections.isJsonObject() && !selections.isJsonArray())) {
                        continue;
                    }
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
                    JsonElement dimensionId = obj.get("dimension-id");
                    if (dimensionId != null && dimensionId.isJsonPrimitive() && dimensionId.getAsJsonPrimitive().isNumber()) {
                        EffectManager.dimensions.put(dimensionId.getAsInt(), selectors.toArray(new EffectSelector[selectors.size()]));
                    }
                }
            }
            for (EffectManager.Alias alias : EffectManager.aliases.values()) {
                if (alias.reference == null) {
                    Objects.log.log(Level.WARN, "Unbound alias detected: " + alias.name);
                }
            }
        } catch (Exception e) {
            Objects.log.log(Level.ERROR, "Failed loading effect selectors", e);
        } finally {
            ClientProxy.reworkEffect(MC.getWorld());
        }
    }

    public static EffectSelector[] getForDimension(int dim) {
        return EffectManager.dimensions.get(dim);
    }

    public static void registerEffectType(String name, Class<? extends Effect> clazz) {
        EffectManager.effectTypes.put(name, clazz);
    }

    public static Effect createEffect(String name) {
        Class<? extends Effect> clazz = EffectManager.effectTypes.get(name);
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Effect readEffect(JsonObject object) {
        JsonElement type = object.get("type");
        if (type == null || !type.isJsonPrimitive() || !type.getAsJsonPrimitive().isString()) {
            return null;
        }
        Effect result = EffectManager.createEffect(type.getAsString());
        if (result != null) {
            result.load(object);
        }
        return result;
    }

    public static void registerEffectSelectorType(String name, Class<? extends EffectSelector> clazz) {
        EffectManager.effectSelectorTypes.put(name, clazz);
    }

    public static EffectSelector createEffectSelector(String name) {
        Class<? extends EffectSelector> clazz = EffectManager.effectSelectorTypes.get(name);
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static EffectSelector readEffectSelector(JsonObject object) {
        JsonElement type = object.get("type");
        if (type == null || !type.isJsonPrimitive() || !type.getAsJsonPrimitive().isString()) {
            JsonElement useAlias = object.get("use-alias");
            if (useAlias != null && useAlias.isJsonPrimitive() && useAlias.getAsJsonPrimitive().isString()) {
                return EffectManager.getOrCreateAlias(useAlias.getAsString());
            }
            return null;
        }
        EffectSelector result = EffectManager.createEffectSelector(type.getAsString());
        if (result != null) {
            result.load(object);
            JsonElement alias = object.get("alias");
            if (alias != null && alias.isJsonPrimitive() && alias.getAsJsonPrimitive().isString()) {
                EffectManager.getOrCreateAlias(alias.getAsString()).reference = result;
            }
        }
        return result;
    }

    private static EffectManager.Alias getOrCreateAlias(String name) {
        EffectManager.Alias result = EffectManager.aliases.get(name);
        if (result == null) {
            result = new EffectManager.Alias(name);
            EffectManager.aliases.put(name, result);
        }
        return result;
    }

    public static class Alias extends EffectSelector {

        private String name;
        private EffectSelector reference;

        public Alias(String name) {
            this.name = name;
        }

        public EffectSelector getReference() {
            return this.reference;
        }

        @Override
        public void load(JsonObject object) {
        }

        @Override
        public boolean isValidNow(WorldClient world, EntityPlayerSP player) {
            return this.reference != null && this.reference.isValidNowFast(world, player);
        }

        @Override
        public boolean isValidNowFast(WorldClient world, EntityPlayerSP player) {
            return this.reference != null && this.reference.isValidNowFast(world, player);
        }

        @Override
        public float getBrightness(WorldClient world, EntityPlayerSP player) {
            return this.reference != null ? this.reference.getBrightness(world, player) : 0.0F;
        }

        @Override
        public boolean canRender() {
            return this.reference != null && this.reference.canRender();
        }

        @Override
        public Effect getEffect() {
            return this.reference != null ? this.reference.getEffect() : null;
        }

        @Override
        public void resetFast() {
            if (this.reference != null) {
                this.reference.resetFast();
            }
        }
    }
}

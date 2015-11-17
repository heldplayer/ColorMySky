package me.heldplayer.mods.aurora.client.selection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.Set;
import me.heldplayer.mods.aurora.client.EffectManager;
import me.heldplayer.mods.aurora.client.effect.Effect;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeSelector extends EffectSelector {

    private Set<String> biomes = new HashSet<String>();
    private int searchRadius = 6;
    private Effect effect;

    @Override
    public void load(JsonObject object) {
        JsonElement biomes = object.get("biomes");
        if (biomes != null) {
            if (biomes.isJsonArray()) {
                JsonArray array = biomes.getAsJsonArray();
                for (JsonElement element : array) {
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                        this.biomes.add(element.getAsString());
                    }
                }
            } else if (biomes.isJsonPrimitive() && biomes.getAsJsonPrimitive().isString()) {
                this.biomes.add(biomes.getAsString());
            }
        }
        JsonElement searchRadius = object.get("search-radius");
        if (searchRadius != null) {
            if (searchRadius.isJsonPrimitive() && searchRadius.getAsJsonPrimitive().isNumber()) {
                this.searchRadius = searchRadius.getAsInt();
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

    @Override
    public Effect getEffect() {
        return this.effect;
    }

    @Override
    public boolean isValidNow(WorldClient world, EntityClientPlayerMP player) {
        BiomeGenBase lastBiome = null;
        for (int x = -this.searchRadius - 1; x <= this.searchRadius + 1; x++) {
            for (int z = -this.searchRadius - 1; z <= this.searchRadius + 1; z++) {
                BiomeGenBase biome = world.getBiomeGenForCoords((int) player.posX + x, (int) player.posZ + z);
                if (biome != lastBiome) {
                    if (this.biomes.contains(biome.biomeName)) {
                        return true;
                    }
                    lastBiome = biome;
                }
            }
        }
        return false; // Worst case, it doesn't match anything
    }

    @Override
    public float getBrightness(WorldClient world, EntityClientPlayerMP player) {
        int count1 = 0; // --
        int count2 = 0; // -+
        int count3 = 0; // ++
        int count4 = 0; // +-
        BiomeGenBase lastBiome = null; // Cache the last biome for a small performance gain, goes to hell when crossing biomes mostly
        boolean lastBiomeValid = false;
        for (int x = -this.searchRadius - 1; x <= this.searchRadius + 1; x++) {
            for (int z = -this.searchRadius - 1; z <= this.searchRadius + 1; z++) {
                BiomeGenBase biome = world.getBiomeGenForCoords((int) player.posX + x, (int) player.posZ + z);
                if (biome == lastBiome) {
                    if (lastBiomeValid) {
                        boolean a = x != -this.searchRadius - 1;
                        boolean b = z != -this.searchRadius - 1;
                        boolean d = z != this.searchRadius + 1;
                        boolean c = x != this.searchRadius + 1;
                        if (a && b) {
                            count1++;
                        }
                        if (a && d) {
                            count2++;
                        }
                        if (c && d) {
                            count3++;
                        }
                        if (c && b) {
                            count4++;
                        }
                    }
                } else {
                    if (this.biomes.contains(biome.biomeName)) {
                        lastBiomeValid = true;
                        boolean a = x != -this.searchRadius - 1;
                        boolean b = z != -this.searchRadius - 1;
                        boolean d = z != this.searchRadius + 1;
                        boolean c = x != this.searchRadius + 1;
                        if (a && b) {
                            count1++;
                        }
                        if (a && d) {
                            count2++;
                        }
                        if (c && d) {
                            count3++;
                        }
                        if (c && b) {
                            count4++;
                        }
                    } else {
                        lastBiomeValid = false;
                    }
                    lastBiome = biome;
                }
            }
        }
        float x = (float) (player.posX % 1);
        float z = (float) (player.posZ % 1);
        float weighted = x * z * count1 + x * (1.0F - z) * count2 + (1.0F - x) * (1.0F - z) * count3 + (1.0F - x) * z * count4;
        int total = (this.searchRadius * 2 + 2) * (this.searchRadius * 2 + 2);
        if (weighted > total) {
            weighted = total;
        }
        return weighted / (float) total;
    }
}

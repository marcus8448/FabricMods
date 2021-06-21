/*
 * Copyright (C) 2019-${year} ${company}
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.marcus8448.mods.snowy;

import io.github.marcus8448.mods.snowy.mixin.BiomeAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;

import java.util.LinkedList;
import java.util.List;

public class Snowy implements ModInitializer {
    public static int addition = Integer.MAX_VALUE;
    public static GameRules.Key<GameRules.BooleanRule> snowAlways = null;

    @Override
    public void onInitialize() {
        List<Biome> biomes = new LinkedList<>();
        for (Biome biome : BuiltinRegistries.BIOME) {
            biomes.add(biome);
        }
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

        for (Biome biome : biomes) {
            int id = BuiltinRegistries.BIOME.getRawId(biome);
            min = Math.min(id, min);
            max = Math.max(id, max);
        }

        addition = (max - min) + 1;
        boolean alter;
        for (Biome biome : biomes) {
//			GenerationStep.Feature.TOP_LAYER_MODIFICATION, ConfiguredFeatures.FREEZE_TOP_LAYER
            alter = biome.getPrecipitation() != Biome.Precipitation.NONE;
            BuiltinBiomes.register(BuiltinRegistries.BIOME.getRawId(biome) + addition, RegistryKey.of(Registry.BIOME_KEY, new Identifier(BuiltinRegistries.BIOME.getId(biome).toString() + "_snowymod")), new Biome.Builder().category(biome.getCategory()).depth(biome.getDepth()).downfall(biome.getDownfall()).effects(biome.getEffects()).generationSettings(biome.getGenerationSettings()).precipitation(alter ? Biome.Precipitation.SNOW : Biome.Precipitation.NONE).scale(biome.getScale()).spawnSettings(biome.getSpawnSettings()).temperature(alter ? -1.0f : biome.getTemperature()).temperatureModifier(((BiomeAccessor) biome).getWeather().temperatureModifier).build());
        }
        snowAlways = GameRuleRegistry.register("snowAlways", GameRules.Category.MISC, GameRules.BooleanRule.create(false, (minecraftServer, booleanRule) -> {}));
    }
}

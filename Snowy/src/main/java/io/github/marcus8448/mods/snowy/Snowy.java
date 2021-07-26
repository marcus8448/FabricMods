/*
 * Copyright (C) 2019-2021 marcus8448
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package io.github.marcus8448.mods.snowy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class Snowy implements ModInitializer {
    public static final SnowyConfig CONFIG = new SnowyConfig();
    @Override
    public void onInitialize() {
        BiomeModifications.create(new Identifier("snowy", "add_freeze_top")).add(ModificationPhase.ADDITIONS, biomeSelectionContext -> (CONFIG.data.nonOverworldBiomes || BiomeSelectors.foundInOverworld().test(biomeSelectionContext)) && (CONFIG.data.dryBiomes || biomeSelectionContext.getBiome().getPrecipitation() != Biome.Precipitation.NONE) && (CONFIG.data.dryBiomes || biomeSelectionContext.getBiome().getDownfall() > 0.0f) && !biomeSelectionContext.hasBuiltInFeature(ConfiguredFeatures.FREEZE_TOP_LAYER), context -> context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, ConfiguredFeatures.FREEZE_TOP_LAYER));
        BiomeModifications.create(new Identifier("snowy", "snow_in_overworld")).add(ModificationPhase.POST_PROCESSING, biomeSelectionContext -> (CONFIG.data.nonOverworldBiomes || BiomeSelectors.foundInOverworld().test(biomeSelectionContext)) && (CONFIG.data.dryBiomes || biomeSelectionContext.getBiome().getPrecipitation() != Biome.Precipitation.NONE) && (CONFIG.data.dryBiomes || biomeSelectionContext.getBiome().getDownfall() > 0.0f), context -> {
            context.getWeather().setPrecipitation(Biome.Precipitation.SNOW);
            context.getWeather().setTemperature(0.0f);
            if (CONFIG.data.temperatureNoise)
                context.getWeather().setTemperatureModifier(Biome.TemperatureModifier.FROZEN);
        });
    }
}

/*
 * Copyright (C) 2019-2021 marcus8448
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

package io.github.marcus8448.mods.gamemodeoverhaul;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ModMenuEntrypoint implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(screen)
                    .setTitle(new TranslatableText("title.gamemodeoverhaul.config"));
            builder.setGlobalized(true);

            ConfigCategory category = builder.getOrCreateCategory(new TranslatableText("category.gamemodeoverhaul.general"));
            category.addEntry(ConfigEntryBuilder.create().startBooleanToggle(new TranslatableText("config.gamemodeoverhaul.enable_gamemode_numbers"), GamemodeOverhaul.config.getConfig().enable_gamemode_numbers).setDefaultValue(true).setTooltip(new TranslatableText("config.gamemodeoverhaul.enable_gamemode_numbers.desc")).setSaveConsumer((enabled) -> GamemodeOverhaul.config.getConfig().enable_gamemode_numbers = enabled).build());
            category.addEntry(ConfigEntryBuilder.create().startBooleanToggle(new TranslatableText("config.gamemodeoverhaul.enable_gamemode_letters"), GamemodeOverhaul.config.getConfig().enable_gamemode_letters).setDefaultValue(true).setTooltip(new TranslatableText("config.gamemodeoverhaul.enable_gamemode_letters.desc")).setSaveConsumer((enabled) -> GamemodeOverhaul.config.getConfig().enable_gamemode_letters = enabled).build());
            category.addEntry(ConfigEntryBuilder.create().startBooleanToggle(new TranslatableText("config.gamemodeoverhaul.enable_defaultgamemode_numbers"), GamemodeOverhaul.config.getConfig().enable_defaultgamemode_numbers).setDefaultValue(true).setTooltip(new TranslatableText("config.gamemodeoverhaul.enable_defaultgamemode_numbers.desc")).setSaveConsumer((enabled) -> GamemodeOverhaul.config.getConfig().enable_defaultgamemode_numbers = enabled).build());
            category.addEntry(ConfigEntryBuilder.create().startBooleanToggle(new TranslatableText("config.gamemodeoverhaul.enable_defaultgamemode_letters"), GamemodeOverhaul.config.getConfig().enable_defaultgamemode_letters).setDefaultValue(true).setTooltip(new TranslatableText("config.gamemodeoverhaul.enable_defaultgamemode_letters.desc")).setSaveConsumer((enabled) -> GamemodeOverhaul.config.getConfig().enable_defaultgamemode_letters = enabled).build());
            category.addEntry(ConfigEntryBuilder.create().startBooleanToggle(new TranslatableText("config.gamemodeoverhaul.enable_excessively_short_commands"), GamemodeOverhaul.config.getConfig().enable_excessively_short_commands).setDefaultValue(false).setTooltip(new TranslatableText("config.gamemodeoverhaul.enable_excessively_short_commands.desc")).setSaveConsumer((enabled) -> GamemodeOverhaul.config.getConfig().enable_excessively_short_commands = enabled).build());
            category.addEntry(ConfigEntryBuilder.create().startBooleanToggle(new TranslatableText("config.gamemodeoverhaul.enable_difficulty_numbers"), GamemodeOverhaul.config.getConfig().enable_difficulty_numbers).setDefaultValue(true).setTooltip(new TranslatableText("config.gamemodeoverhaul.enable_difficulty_numbers.desc")).setSaveConsumer((enabled) -> GamemodeOverhaul.config.getConfig().enable_difficulty_numbers = enabled).build());
            category.addEntry(ConfigEntryBuilder.create().startBooleanToggle(new TranslatableText("config.gamemodeoverhaul.enable_toggledownfall"), GamemodeOverhaul.config.getConfig().enable_toggledownfall).setDefaultValue(true).setTooltip(new TranslatableText("config.gamemodeoverhaul.enable_toggledownfall.desc")).setSaveConsumer((enabled) -> GamemodeOverhaul.config.getConfig().enable_toggledownfall = enabled).build());

            builder.setSavingRunnable(GamemodeOverhaul.config::save);
            return builder.build();
        };
    }
}

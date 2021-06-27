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

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GamemodeOverhaulConfig {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File file = new File(FabricLoader.getInstance().getConfigDirectory(), "gamemode_overhaul.json");
    private Data config = new Data();

    public GamemodeOverhaulConfig() {
        this.load();
    }

    public Data getConfig() {
        return config;
    }

    public void save() {
        try {
            GamemodeOverhaul.LOGGER.info("Saving config!");
            FileUtils.writeStringToFile(this.file, this.gson.toJson(this.config), Charsets.UTF_8);
        } catch (IOException e) {
            GamemodeOverhaul.LOGGER.error("Failed to save config.", e);
        }
    }

    public void load() {
        try {
            this.file.getParentFile().mkdirs();
            if (!this.file.exists()) {
                GamemodeOverhaul.LOGGER.info("Failed to find config file, creating one.");
                this.save();
            } else {
                byte[] bytes = Files.readAllBytes(Paths.get(this.file.getPath()));
                this.config = this.gson.fromJson(new String(bytes, Charsets.UTF_8), Data.class);
            }
        } catch (IOException e) {
            GamemodeOverhaul.LOGGER.error("Failed to load config.", e);
        }
    }

    public static class Data {
        @Expose
        public boolean enable_gamemode_numbers = true;
        @Expose
        public boolean enable_gamemode_letters = true;
        @Expose
        public boolean enable_defaultgamemode_numbers = true;
        @Expose
        public boolean enable_defaultgamemode_letters = true;
        @Expose
        public boolean enable_excessively_short_commands = false;
        @Expose
        public boolean enable_difficulty_numbers = true;
        @Expose
        public boolean enable_toggledownfall = true;
    }
}

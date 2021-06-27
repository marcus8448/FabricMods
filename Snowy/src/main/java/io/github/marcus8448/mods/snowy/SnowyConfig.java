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

package io.github.marcus8448.mods.snowy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SnowyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public final Data data;

    public SnowyConfig() {
        Data data1 = null;
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "snowy.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                data1 = GSON.fromJson(reader, Data.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            data1 = new Data();
            try (FileWriter writer = new FileWriter(file)) {
                file.createNewFile();
                GSON.toJson(data1, Data.class, writer);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (data1 == null) {
            data1 = new Data();
        }

        this.data = data1;
    }

//    public void save() {
//        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "snowy.json");
//        try (FileWriter writer = new FileWriter(file)){
//            file.createNewFile();
//            GSON.toJson(this.data, Data.class, writer);
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static class Data {
        @Expose
        @SerializedName("always_snow")
        public boolean alwaysSnow = false;

        @Expose
        @SerializedName("temperature_noise")
        public boolean temperatureNoise = true;

        @Expose
        @SerializedName("dry_biomes")
        public boolean dryBiomes = false;

        @Expose
        @SerializedName("non_overworld_biomes")
        public boolean nonOverworldBiomes = false;
    }
}

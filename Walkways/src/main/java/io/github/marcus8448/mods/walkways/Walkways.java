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

package io.github.marcus8448.mods.walkways;

import io.github.marcus8448.mods.walkways.block.MovingWalkwayBlock;
import io.github.marcus8448.mods.walkways.block.WalkwayBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Walkways implements ModInitializer {
    public static final Block WALKWAY = new WalkwayBlock(AbstractBlock.Settings.of(Material.STONE).strength(1.0f, 3.0f));
    public static final Block MOVING_WALKWAY = new MovingWalkwayBlock(AbstractBlock.Settings.of(Material.STONE).strength(1.0f, 3.0f));

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(Constant.MOD_ID, Constant.Block.WALKWAY), WALKWAY);
        Registry.register(Registry.BLOCK, new Identifier(Constant.MOD_ID, Constant.Block.MOVING_WALKWAY), MOVING_WALKWAY);
        Registry.register(Registry.ITEM, new Identifier(Constant.MOD_ID, Constant.Block.WALKWAY), new BlockItem(WALKWAY, new Item.Settings().group(ItemGroup.TRANSPORTATION)));
        Registry.register(Registry.ITEM, new Identifier(Constant.MOD_ID, Constant.Block.MOVING_WALKWAY), new BlockItem(MOVING_WALKWAY, new Item.Settings().group(ItemGroup.TRANSPORTATION)));
    }
}


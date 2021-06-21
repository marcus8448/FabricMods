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

package io.github.marcus8448.mods.cyg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChangeYourGamemode implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            PersonalGameModeCommand.register(dispatcher);
            dispatcher.register(CommandManager.literal("clearp").executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayer();
                if (player != null) {
                    player.inventory.remove(stack -> true, -1, player.playerScreenHandler.method_29281());
                    player.inventory.setCursorStack(ItemStack.EMPTY);
                    player.currentScreenHandler.sendContentUpdates();
                    player.playerScreenHandler.onContentChanged(player.inventory);
                    player.updateCursorStack();
                }
                return 1;
            }));
            dispatcher.register(CommandManager.literal("killp").executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayer();
                if (player != null) {
                    player.kill();
                }
                return 1;
            }));
        });
    }
}

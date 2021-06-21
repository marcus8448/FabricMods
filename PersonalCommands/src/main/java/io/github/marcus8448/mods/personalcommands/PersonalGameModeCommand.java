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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.marcus8448.mods.personalcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

import java.util.Collection;
import java.util.Collections;

public class PersonalGameModeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamemodep");
        GameMode[] var2 = GameMode.values();

        for (GameMode gameMode : var2) {
            if (gameMode != GameMode.DEFAULT) {
                literalArgumentBuilder.then(CommandManager.literal(gameMode.getName()).executes(commandContext -> execute(commandContext, Collections.singleton(commandContext.getSource().getPlayer()), gameMode)));
                literalArgumentBuilder.then(CommandManager.literal(String.valueOf(gameMode.getId())).executes(commandContext -> execute(commandContext, Collections.singleton(commandContext.getSource().getPlayer()), gameMode)));
            }
        }

        dispatcher.register(literalArgumentBuilder);
    }

    private static void setGameMode(ServerCommandSource source, ServerPlayerEntity player, GameMode gameMode) {
        Text text = new TranslatableText("gameMode." + gameMode.getName());
        if (source.getEntity() == player) {
            source.sendFeedback(new TranslatableText("commands.gamemode.success.self", text), true);
        } else {
            if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendSystemMessage(new TranslatableText("gameMode.changed", text), Util.NIL_UUID);
            }

            source.sendFeedback(new TranslatableText("commands.gamemode.success.other", player.getDisplayName(), text), true);
        }

    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
        int i = 0;

        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (serverPlayerEntity.interactionManager.getGameMode() != gameMode) {
                serverPlayerEntity.changeGameMode(gameMode);
                setGameMode(context.getSource(), serverPlayerEntity, gameMode);
                ++i;
            }
        }

        return i;
    }
}

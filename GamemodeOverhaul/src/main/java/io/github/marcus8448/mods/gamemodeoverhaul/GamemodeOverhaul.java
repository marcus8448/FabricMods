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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;

/**
 * @author marcus8448
 */
@SuppressWarnings("unused")
public class GamemodeOverhaul implements ModInitializer {
    public static final String MOD_ID = "gamemodeoverhaul";
    public static final Logger LOGGER = LogManager.getLogger("GamemodeOverhaul");
    public static final GamemodeOverhaulConfig config = new GamemodeOverhaulConfig();

    private static void commandFeedback(ServerCommandSource source, ServerPlayerEntity player, GameMode mode) {
        TranslatableText text = new TranslatableText("gameMode." + mode.getName());
        if (source.getEntity() == player) {
            source.sendFeedback(new TranslatableText("commands.gamemode.success.self", text), true);
        } else {
            if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendSystemMessage(new TranslatableText("gameMode.changed", text), Util.NIL_UUID);
            }

            source.sendFeedback(new TranslatableText("commands.gamemode.success.other", player.getDisplayName(), text), true);
        }

    }

    private static int changeMode(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> collection, GameMode mode) {
        int i = 0;

        for (ServerPlayerEntity player : collection) {
            if (player.interactionManager.getGameMode() != mode) {
                player.changeGameMode(mode);
                commandFeedback(context.getSource(), player, mode);
                ++i;
            }
        }

        return i;
    }

    private static int changeMode(CommandContext<ServerCommandSource> context, GameMode mode) {
        ServerPlayerEntity player;
        try {
            player = context.getSource().getPlayer();
            if (player.interactionManager.getGameMode() != mode) {
                player.changeGameMode(mode);
                commandFeedback(context.getSource(), player, mode);
            }
        } catch (CommandSyntaxException ignore) {
            return 0;
        }
        return 1;
    }

    private static int changeModes(CommandContext<ServerCommandSource> context, GameMode mode) {
        for (ServerPlayerEntity p : context.getSource().getWorld().getPlayers()) {
            if (p.interactionManager.getGameMode() != mode) {
                p.changeGameMode(mode);
                commandFeedback(context.getSource(), p, mode);
            }
        }
        return context.getSource().getWorld().getPlayers().size();
    }

    private static int changeDefaultMode(ServerCommandSource source, GameMode mode) {
        int i = 0;
        MinecraftServer server = source.getMinecraftServer();
        server.setDefaultGameMode(mode);
        if (server.getForcedGameMode() == mode) {

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.interactionManager.getGameMode() != mode) {
                    player.changeGameMode(mode);
                    ++i;
                }
            }
        }

        source.sendFeedback(new TranslatableText("commands.defaultgamemode.success", mode.getTranslatableName()), true);
        return i;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("GamemodeOverhaul is initializing!");
        CommandRegistrationCallback.EVENT.register((dispatcher, b) -> {
            if (config.getConfig().enable_gamemode_numbers || config.getConfig().enable_gamemode_letters) {
                registerGamemodeCommands(dispatcher);
            }
            if (config.getConfig().enable_excessively_short_commands) {
                registerExcessivelyShortGamemodeCommands(dispatcher);
                registerExcessivelyShortDefaultGamemodeCommands(dispatcher);
            }
            if (config.getConfig().enable_defaultgamemode_numbers || config.getConfig().enable_defaultgamemode_letters) {
                registerDefaultGamemodeCommands(dispatcher);
            }
            if (config.getConfig().enable_difficulty_numbers) {
                registerDifficultyCommand(dispatcher);
            }
            if (config.getConfig().enable_toggledownfall) {
                registerToggledownfallCommand(dispatcher);
            }
        });
    }

    private void registerGamemodeCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> gamemode = CommandManager.literal("gamemode").requires((source) -> source.hasPermissionLevel(2));
        GameMode[] gameModes = GameMode.values();
        for (GameMode mode : gameModes) {
            if (mode != GameMode.DEFAULT) {
                if (config.getConfig().enable_gamemode_numbers) {
                    gamemode.then(CommandManager.literal(Integer.toString(mode.getId())).executes((context) -> changeMode(context, Collections.singleton(context.getSource().getPlayer()), mode)).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context) -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), mode))));
                }
                letters(gamemode, mode);
            }
        }
        dispatcher.register(gamemode);
    }

    private void letters(LiteralArgumentBuilder<ServerCommandSource> gamemode, GameMode mode) {
        if (config.getConfig().enable_gamemode_letters) {
            if (mode != GameMode.SPECTATOR) {
                gamemode.then(CommandManager.literal(Character.toString(mode.getName().charAt(0))).executes((context) -> changeMode(context, Collections.singleton(context.getSource().getPlayer()), mode))).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context) -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), mode)));
            } else {
                gamemode.then(CommandManager.literal("sp").executes((context) -> changeMode(context, Collections.singleton(context.getSource().getPlayer()), mode))).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context) -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), mode)));
            }
        }
    }

    private void registerExcessivelyShortGamemodeCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> gm = CommandManager.literal("gm").requires((source) -> source.hasPermissionLevel(2));
        LiteralArgumentBuilder<ServerCommandSource> gms = CommandManager.literal("gms").requires((source) -> source.hasPermissionLevel(2)).executes((context -> changeMode(context, GameMode.SURVIVAL))).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), GameMode.SURVIVAL))));
        LiteralArgumentBuilder<ServerCommandSource> gmc = CommandManager.literal("gmc").requires((source) -> source.hasPermissionLevel(2)).executes((context -> changeMode(context, GameMode.CREATIVE))).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), GameMode.CREATIVE))));
        LiteralArgumentBuilder<ServerCommandSource> gma = CommandManager.literal("gma").requires((source) -> source.hasPermissionLevel(2)).executes((context -> changeMode(context, GameMode.ADVENTURE))).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), GameMode.ADVENTURE))));
        LiteralArgumentBuilder<ServerCommandSource> gmsp = CommandManager.literal("gmsp").requires((source) -> source.hasPermissionLevel(2)).executes((context -> changeMode(context, GameMode.SPECTATOR))).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), GameMode.SPECTATOR))));

        GameMode[] gameModes = GameMode.values();
        for (GameMode mode : gameModes) {
            if (mode != GameMode.DEFAULT) {
                gm.then(CommandManager.literal(mode.getName()).executes((context) -> changeMode(context, Collections.singleton(context.getSource().getPlayer()), mode))).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context) -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), mode)));
                if (config.getConfig().enable_gamemode_numbers) {
                    gm.then(CommandManager.literal(Integer.toString(mode.getId())).executes((context) -> changeMode(context, Collections.singleton(context.getSource().getPlayer()), mode))).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context) -> changeMode(context, EntityArgumentType.getPlayers(context, "target"), mode)));
                }
                letters(gm, mode);
            }
        }
        dispatcher.register(gm);
        dispatcher.register(gms);
        dispatcher.register(gmc);
        dispatcher.register(gma);
        dispatcher.register(gmsp);
    }

    private void registerDefaultGamemodeCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> defaultgamemode = CommandManager.literal("defaultgamemode").requires((source) -> source.hasPermissionLevel(2));
        GameMode[] modes = GameMode.values();
        for (GameMode mode : modes) {
            if (mode != GameMode.DEFAULT) {
                if (config.getConfig().enable_defaultgamemode_numbers) {
                    defaultgamemode.then(CommandManager.literal(Integer.toString(mode.getId())).executes((context) -> changeDefaultMode(context.getSource(), mode)));
                }
                if (config.getConfig().enable_gamemode_letters) {
                    if (mode != GameMode.SPECTATOR) {
                        defaultgamemode.then(CommandManager.literal(Character.toString(mode.getName().charAt(0))).executes((context) -> changeDefaultMode(context.getSource(), mode)));
                    } else {
                        defaultgamemode.then(CommandManager.literal("sp").executes((context) -> changeDefaultMode(context.getSource(), mode)));
                    }
                }
            }
        }
        dispatcher.register(defaultgamemode);
    }

    private void registerExcessivelyShortDefaultGamemodeCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> dgm = CommandManager.literal("dgm").requires((source) -> source.hasPermissionLevel(2));
        LiteralArgumentBuilder<ServerCommandSource> dgms = CommandManager.literal("dgms").requires((source) -> source.hasPermissionLevel(2)).executes(context -> changeDefaultMode(context.getSource(), GameMode.SURVIVAL));
        LiteralArgumentBuilder<ServerCommandSource> dgmc = CommandManager.literal("dgmc").requires((source) -> source.hasPermissionLevel(2)).executes(context -> changeDefaultMode(context.getSource(), GameMode.CREATIVE));
        LiteralArgumentBuilder<ServerCommandSource> dgma = CommandManager.literal("dgma").requires((source) -> source.hasPermissionLevel(2)).executes(context -> changeDefaultMode(context.getSource(), GameMode.ADVENTURE));
        LiteralArgumentBuilder<ServerCommandSource> dgmsp = CommandManager.literal("dgmsp").requires((source) -> source.hasPermissionLevel(2)).executes(context -> changeDefaultMode(context.getSource(), GameMode.SPECTATOR));
        GameMode[] modes = GameMode.values();
        for (GameMode mode : modes) {
            if (mode != GameMode.DEFAULT) {
                if (config.getConfig().enable_defaultgamemode_numbers) {
                    dgm.then(CommandManager.literal(Integer.toString(mode.getId())).executes((context) -> changeDefaultMode(context.getSource(), mode)));
                }
                dgm.then(CommandManager.literal(mode.getName()).executes((context) -> changeDefaultMode(context.getSource(), mode)));
                if (config.getConfig().enable_defaultgamemode_letters) {
                    if (mode != GameMode.SPECTATOR) {
                        dgm.then(CommandManager.literal(Character.toString(mode.getName().charAt(0))).executes((context) -> changeDefaultMode(context.getSource(), mode)));
                    } else {
                        dgm.then(CommandManager.literal("sp").executes((context) -> changeDefaultMode(context.getSource(), mode)));
                    }
                }
            }
        }
        dispatcher.register(dgm);
        dispatcher.register(dgms);
        dispatcher.register(dgmc);
        dispatcher.register(dgma);
        dispatcher.register(dgmsp);
    }

    private void registerDifficultyCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> difficultyCommand = CommandManager.literal("difficulty");
        Difficulty[] difficulties = Difficulty.values();

        for (Difficulty difficulty : difficulties) {
            difficultyCommand.then(CommandManager.literal(Integer.toString(difficulty.getId())).executes((context) -> net.minecraft.server.command.DifficultyCommand.execute(context.getSource(), difficulty)));
        }

        dispatcher.register((difficultyCommand.requires((source) -> source.hasPermissionLevel(2))).executes((context) -> {
            Difficulty difficulty = context.getSource().getWorld().getDifficulty();
            context.getSource().sendFeedback(new TranslatableText("commands.difficulty.query", difficulty.getTranslatableName()), false);
            return difficulty.getId();
        }));
    }

    private void registerToggledownfallCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("toggledownfall").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(context -> {
            if (!(context.getSource().getWorld().isRaining() || context.getSource().getWorld().getLevelProperties().isRaining() || context.getSource().getWorld().isThundering() || context.getSource().getWorld().getLevelProperties().isThundering())) {
                context.getSource().getWorld().setWeather(0, 6000, true, false);
            } else {
                context.getSource().getWorld().setWeather(6000, 0, false, false);
            }
            context.getSource().sendFeedback(new TranslatableText("gamemodeoverhaul.command.toggledownfall.feedback"), false);
            return 6000;
        }));
    }

}

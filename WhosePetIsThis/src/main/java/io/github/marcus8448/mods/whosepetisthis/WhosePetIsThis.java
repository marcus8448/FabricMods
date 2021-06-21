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

package io.github.marcus8448.mods.whosepetisthis;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

import java.util.UUID;

public class WhosePetIsThis implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> {
            commandDispatcher.register(CommandManager.literal("getowner").then(CommandManager.argument("entity", EntityArgumentType.entity()).executes(context -> {
                Entity entity = EntityArgumentType.getEntity(context, "entity");
                if (entity instanceof TameableEntity) {
                    UUID uuid = ((TameableEntity) entity).getOwnerUuid();
                    GameProfile profile = context.getSource().getMinecraftServer().getUserCache().getByUuid(uuid);
                    if (profile != null) {
                        context.getSource().sendFeedback(new TranslatableText("command.whosepetisthis.pet_owner", profile.getName()), false);
                    } else {
                        context.getSource().sendFeedback(new TranslatableText("command.whosepetisthis.no_owner"), false);
                    }
                    return 1;
                } else {
                    context.getSource().sendError(new TranslatableText("command.whosepetisthis.not_tameable"));
                }
                return 0;
            })));
        });
    }
}


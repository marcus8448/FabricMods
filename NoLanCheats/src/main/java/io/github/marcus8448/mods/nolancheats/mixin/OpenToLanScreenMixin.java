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

package io.github.marcus8448.mods.nolancheats.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OpenToLanScreen.class)
@Environment(EnvType.CLIENT)
public class OpenToLanScreenMixin {
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/CyclingButtonWidget$Builder;build(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/CyclingButtonWidget$UpdateCallback;)Lnet/minecraft/client/gui/widget/CyclingButtonWidget;"))
    private <T> CyclingButtonWidget<T> createWidget(CyclingButtonWidget.Builder<T> builder, int x, int y, int width, int height, Text optionText, CyclingButtonWidget.UpdateCallback<T> callback) {
        if (MinecraftClient.getInstance().getServer().getForcedGameMode() != GameMode.CREATIVE)
            return builder.build(-100000, 100000, 1, 1, optionText, (a, b) -> {});
        return builder.build(x, y, width, height, optionText, callback);
    }

    @Dynamic("Synthetic lambda 1.17.1")
    @Redirect(method = "method_19851", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;openToLan(Lnet/minecraft/world/GameMode;ZI)Z"))
    private boolean noCheatsForceGamemode(IntegratedServer server, @Nullable GameMode gameMode, boolean cheatsAllowed, int port) {
        return server.openToLan(server.getDefaultGameMode() != GameMode.CREATIVE ? server.getDefaultGameMode() : gameMode, server.getDefaultGameMode() == GameMode.CREATIVE && cheatsAllowed, port);
    }
}

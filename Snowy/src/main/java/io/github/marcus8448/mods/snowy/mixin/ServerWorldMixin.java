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

package io.github.marcus8448.mods.snowy.mixin;

import io.github.marcus8448.mods.snowy.Snowy;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow
    public abstract void setWeather(int clearDuration, int rainDuration, boolean raining, boolean thundering);

    @Shadow
    public abstract ServerWorld toServerWorld();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isRaining()Z"))
    private void weather(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (Snowy.CONFIG.data.alwaysSnow) {
            if (Snowy.CONFIG.data.nonOverworldBiomes || this.toServerWorld().getRegistryKey() == World.OVERWORLD) {
                if (!this.toServerWorld().isThundering()) {
                    this.setWeather(0, 200, true, false);
                }
            }
        }
    }
}

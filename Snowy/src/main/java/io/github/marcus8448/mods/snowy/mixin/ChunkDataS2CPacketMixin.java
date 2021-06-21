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

package io.github.marcus8448.mods.snowy.mixin;

import io.github.marcus8448.mods.snowy.Snowy;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkDataS2CPacket.class)
public class ChunkDataS2CPacketMixin {
    private boolean run = false;

    @Redirect(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeArray;toIntArray()[I"))
    private int[] offsetBiomes(BiomeArray biomeArray) {
        int[] ints = biomeArray.toIntArray();
        if (run) {
            for (int i = 0; i < ints.length; i++) {
                ints[i] = ints[i] + Snowy.addition;
            }
        }
        return ints;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeArray;toIntArray()[I", shift = At.Shift.BEFORE))
    private void offsetBiomes_pre(WorldChunk chunk, int includedSectionsMask, CallbackInfo ci) {
        this.run = chunk.getWorld().getRegistryKey() == World.OVERWORLD;
    }
}

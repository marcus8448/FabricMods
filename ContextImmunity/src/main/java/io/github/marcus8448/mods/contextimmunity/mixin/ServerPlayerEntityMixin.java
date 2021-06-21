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

package io.github.marcus8448.mods.contextimmunity.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Unique
    private DamageSource source;

    @ModifyConstant(method = "damage", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO))
    private int ignoreInvulnerability(int i) {
        if (source.bypassesArmor() || source.isFire()) return 100000000;
        return i;
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void ignoreInvulnerability(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.source = source;
    }
}

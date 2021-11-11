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

package io.github.marcus8448.mods.personalcommands.mixin;

import net.minecraft.server.command.LocateBiomeCommand;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocateBiomeCommand.class)
public abstract class LocateBiomeCommandMixin {
    @Dynamic("Synthetic lambda 1.17.1")
    @ModifyConstant(method = "method_24494", constant = @Constant(intValue = 2))
    private static int allowLocate(int in) {
        return 0;
    }
}

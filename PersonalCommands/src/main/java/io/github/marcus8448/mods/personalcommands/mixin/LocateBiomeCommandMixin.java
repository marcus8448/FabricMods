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

package io.github.marcus8448.mods.personalcommands.mixin;

import net.minecraft.server.command.LocateBiomeCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocateBiomeCommand.class)
public abstract class LocateBiomeCommandMixin {
    @ModifyConstant(method = "method_24494", constant = @Constant(intValue = 2)) //synthetic
    private static int allowLocate(int in) {
        return 0;
    }
}

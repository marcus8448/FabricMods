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

package io.github.marcus8448.mods.walkways.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MovingWalkwayBlock extends WalkwayBlock {
    public MovingWalkwayBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if ((entity.getPos().y - 0.0625) % 1.0 == 0.0 && entity.getBlockPos().equals(pos)) {
            Direction direction = state.get(WalkwayBlock.FACING);
            entity.setVelocity(entity.getVelocity().add(direction.getOffsetX() * 0.05, 0, direction.getOffsetZ() * 0.05));
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}

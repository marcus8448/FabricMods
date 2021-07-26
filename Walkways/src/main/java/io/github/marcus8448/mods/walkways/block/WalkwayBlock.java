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

package io.github.marcus8448.mods.walkways.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class WalkwayBlock extends Block {
    protected static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final BooleanProperty CONNECTED_LEFT = BooleanProperty.of("left");
    private static final BooleanProperty CONNECTED_RIGHT = BooleanProperty.of("right");

    private static final VoxelShape BASE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape LEFT = Block.createCuboidShape(0.0, 1.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape LEFT_90 = Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape RIGHT = Block.createCuboidShape(15.0, 1.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape RIGHT_90 = Block.createCuboidShape(0.0, 1.0, 15.0, 16.0, 16.0, 16.0);

    public WalkwayBlock(Settings settings) {
        super(settings.dynamicBounds());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getPlayerFacing();
        Direction left = switch (facing) {
            case NORTH -> Direction.WEST;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
            case EAST -> Direction.NORTH;
            default -> throw new IllegalStateException("Invalid player facing!");
        };
        BlockState leftState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(left));
        BlockState rightState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(left.getOpposite()));
        return this.getDefaultState().with(FACING, facing)
                .with(CONNECTED_LEFT, leftState.getBlock() == this && leftState.get(FACING) == facing)
                .with(CONNECTED_RIGHT, rightState.getBlock() == this && rightState.get(FACING) == facing);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getRaycastShape(state, world, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getRaycastShape(state, world, pos);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getRaycastShape(state, world, pos);
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getRaycastShape(state, world, pos);
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        VoxelShape shape = BASE;
        if (!state.get(CONNECTED_LEFT)) {
            shape = VoxelShapes.combine(shape, switch (state.get(FACING)) {
                case NORTH -> LEFT;
                case SOUTH -> RIGHT;
                case WEST -> RIGHT_90;
                case EAST -> LEFT_90;
                default -> throw new IllegalStateException("Invalid facing!");
            }, BooleanBiFunction.OR);
        }
        if (!state.get(CONNECTED_RIGHT)) {
            shape = VoxelShapes.combine(shape, switch (state.get(FACING)) {
                case NORTH -> RIGHT;
                case SOUTH -> LEFT;
                case WEST -> LEFT_90;
                case EAST -> RIGHT_90;
                default -> throw new IllegalStateException("Invalid facing!");
            }, BooleanBiFunction.OR);
        }
        return shape;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction left = switch (state.get(FACING)) {
            case NORTH -> Direction.WEST;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
            case EAST -> Direction.NORTH;
            default -> throw new IllegalStateException("Invalid facing!");
        };
        boolean connect = neighborState.getBlock() == this && neighborState.get(FACING) == state.get(FACING);
        if (left == direction) {
            return state.with(CONNECTED_LEFT, connect);
        } else if (left.getOpposite() == direction) {
            return state.with(CONNECTED_RIGHT, connect);
        }
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING, CONNECTED_LEFT, CONNECTED_RIGHT));
    }


    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}

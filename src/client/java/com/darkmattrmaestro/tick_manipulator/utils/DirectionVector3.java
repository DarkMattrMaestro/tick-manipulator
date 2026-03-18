package com.darkmattrmaestro.tick_manipulator.utils;

import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.util.constants.Direction;

import java.util.Objects;

public class DirectionVector3 {
    public Vector3Int pos;
    public Direction dir;

    public DirectionVector3(int x, int y, int z, Direction dir) {
        super();
        this.pos = new Vector3Int(x, y, z);
        this.dir = dir;
    }

    public DirectionVector3(BlockPosition blockPos, BlockState blockState) {
        super();
        this.pos = new Vector3Int(blockPos.getGlobalX(), blockPos.getGlobalY(), blockPos.getGlobalZ());
        this.dir = blockState.getParamDirection("direction");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DirectionVector3 that = (DirectionVector3) o;
        return Objects.equals(pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pos);
    }
}

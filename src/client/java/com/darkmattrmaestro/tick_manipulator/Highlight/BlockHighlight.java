package com.darkmattrmaestro.tick_manipulator.Highlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.utils.DirectionVector3;
import com.darkmattrmaestro.tick_manipulator.utils.Vector3Int;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.InGame;
import com.badlogic.gdx.graphics.Camera;
import finalforeach.cosmicreach.util.Axis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class BlockHighlight {
    private static final float inflate = -0.05f;
    private static final float ARROW_TAIL_LENGTH = 0.64f;
    private static final float ARROW_HEAD_LENGTH = 0.3f;
    private static final Color borderColour = Color.valueOf("ff000088");
    private static final Color arrowColour = Color.RED;

    public Map<String, String> properties = new HashMap<String, String>();
    public ArrayList<DirectionVector3> highlightedBlocks = new ArrayList<DirectionVector3>();

//    public BlockHighlight() {
//        setPropertyCondition("type", "push");
//
//    }

    public void clear() {
        highlightedBlocks.clear();
        properties.clear();
    }

    public void setPropertyCondition(String property, String value) {
        properties.put(property, value);
    }

//    public static String clearFormatting(String str) {
//        return str.replaceAll("@[\\t\\n\\r ]+", "");
//    }

//    public <T> boolean checkMatchesProperties(T obj) {
//        Class<?> clazz = obj.getClass();
//        for (String key: properties.keySet()) {
//            boolean found = false;
//            String res = "";
//
//            try {
//                // Check fields
//                Field field = clazz.getDeclaredField(key);
//                field.setAccessible(true);
//                res = String.valueOf(field.get(obj));
//                found = true;
//            } catch (Exception _) {
//                try {
//                    // Check methods
//                    Method method = clazz.getDeclaredMethod(key);
//                    method.setAccessible(true);
//                    res = String.valueOf(method.invoke(obj));
//                    found = true;
//                } catch (Exception _) {}
//            }
//
//            if (!found || !properties.get(key).equals(clearFormatting(res))) {
//                return false;
//            }
//        }
//
//        return true;
//    }

    public boolean checkMatchesStateParams(BlockState blockState) {
        if (properties.isEmpty()) { return false; }

        for (String key: properties.keySet()) {
            if (!properties.get(key).equals(blockState.getParam(key))) {
                return false;
            }
        }

        return true;
    }

    public void updateHighlightedBlockList(BlockPosition blockPos) {
        Constants.LOGGER.warn(highlightedBlocks.size());
        if (blockPos == null) {
            return;
        }

        BlockState blockState = blockPos.getBlockState();
        if (blockState != null) {
            if (checkMatchesStateParams(blockState)) {
                Constants.LOGGER.error("True");
                highlightedBlocks.add(new DirectionVector3(blockPos, blockState));
            } else {
                Constants.LOGGER.error("False1");
                highlightedBlocks.removeIf((DirectionVector3 dirVec) -> (
                            dirVec.pos.x == blockPos.getGlobalX()
                            && dirVec.pos.y == blockPos.getGlobalY()
                            && dirVec.pos.z == blockPos.getGlobalZ()
                ));
            }
        } else {
            Constants.LOGGER.error("False2");
            highlightedBlocks.removeIf((DirectionVector3 dirVec) -> (
                    dirVec.pos.x == blockPos.getGlobalX()
                            && dirVec.pos.y == blockPos.getGlobalY()
                            && dirVec.pos.z == blockPos.getGlobalZ()
            ));
        }
    }

    public void draw(ShapeRenderer sr) {
        Camera camera = GameState.IN_GAME.getWorldCamera();
        sr.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Constants.LOGGER.warn(highlightedBlocks.size());
        for (DirectionVector3 dirVec: highlightedBlocks.toArray(DirectionVector3[]::new)) {
            Vector3 floatPos = dirVec.pos.toVector3();
            float width = 1 + 2 * inflate;

            float dist = Math.max(0.01f, floatPos.dst(InGame.getLocalPlayer().getPosition()));
            Gdx.gl.glLineWidth(10/dist);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(borderColour);
            sr.box(floatPos.x - inflate, floatPos.y - inflate, floatPos.z + inflate + 1, width, width, width);
            sr.end();

            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(arrowColour); // arrowColour
            if (dirVec.dir != null) {
                Vector3 line = new Vector3(
                        -dirVec.dir.getXOffset(),
                        -dirVec.dir.getYOffset(),
                        -dirVec.dir.getZOffset()
                ); // Already normalized

                Vector3 cross = line.cpy().crs(camera.direction).nor().scl(ARROW_HEAD_LENGTH / 2);

                Vector3 arrowHead = new Vector3(
                        floatPos.x + 0.5f + dirVec.dir.getXOffset() * ARROW_TAIL_LENGTH * 0.5f,
                        floatPos.y + 0.5f + dirVec.dir.getYOffset() * ARROW_TAIL_LENGTH * 0.5f,
                        floatPos.z + 0.5f + dirVec.dir.getZOffset() * ARROW_TAIL_LENGTH * 0.5f
                );
                Vector3 arrowTail = new Vector3(
                        floatPos.x + 0.5f - dirVec.dir.getXOffset() * ARROW_TAIL_LENGTH * 0.5f,
                        floatPos.y + 0.5f - dirVec.dir.getYOffset() * ARROW_TAIL_LENGTH * 0.5f,
                        floatPos.z + 0.5f - dirVec.dir.getZOffset() * ARROW_TAIL_LENGTH * 0.5f
                );

                Vector3 arrowX = line.cpy().scl(ARROW_HEAD_LENGTH);
                Vector3 arrowLineEnd1 = (arrowHead.cpy().add(cross)).add(arrowX);
                Vector3 arrowLineEnd2 = (arrowHead.cpy().sub(cross)).add(arrowX);

                sr.line(arrowTail, arrowHead);
                sr.line(arrowHead, arrowLineEnd1);
                sr.line(arrowHead, arrowLineEnd2);
            }

            sr.end();
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}

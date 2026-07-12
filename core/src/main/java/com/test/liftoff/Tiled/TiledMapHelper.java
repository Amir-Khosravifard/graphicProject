package com.test.liftoff.Tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class TiledMapHelper {
    private TiledMap tiledMap;

    public TiledMap loadMap(String path) {
        tiledMap = new TmxMapLoader().load(path);
        return tiledMap;
    }

    public ArrayList<Rectangle> getCollisionRectangles(String layerName) {
        ArrayList<com.badlogic.gdx.math.Rectangle> rectangles = new ArrayList<>();
        MapLayer layer = tiledMap.getLayers().get(layerName);

        if (layer == null) return rectangles;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                com.badlogic.gdx.math.Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                rectangles.add(new com.badlogic.gdx.math.Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
            }
        }
        return rectangles;
    }

    public Vector2 getObjectPosition(String layerName, String objectName) {
        MapLayer layer = tiledMap.getLayers().get(layerName);
        if (layer == null) return new Vector2(100, 150);

        MapObject object = layer.getObjects().get(objectName);


        if (object == null) {
            if (layer.getObjects().getCount() > 0) {
                object = layer.getObjects().get(0);
            } else {
                return new Vector2(100, 150);
            }
        }

        float x = object.getProperties().get("x", Float.class);
        float y = object.getProperties().get("y", Float.class);
        return new Vector2(x, y);
    }
}

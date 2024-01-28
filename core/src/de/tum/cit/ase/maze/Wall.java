package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Wall extends GameObject implements Collidable {
    public Wall(TextureRegion region, float x, float y) {
        super(region, x, y, region.getRegionWidth()-1, region.getRegionHeight()-1);
    }
    public Rectangle getBoundingBox() {

        return new Rectangle(x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }
    public ObjectType getObjectType() {
        return ObjectType.WALL; // Return the type for Wall
    }

    @Override
    public void handleCollision(Collidable other) {

    }
}
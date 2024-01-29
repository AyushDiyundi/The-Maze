package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Life extends GameObject implements Collidable {
    public Life(TextureRegion region, float x, float y) {
        super(region, x, y, region.getRegionWidth()-6, region.getRegionHeight()-8);
    }
    public Rectangle getBoundingBox() {

        return new Rectangle(x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }
    @Override
    public void handleCollision(Collidable other) {

    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.LIFE;
    }
}

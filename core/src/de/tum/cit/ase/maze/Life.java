package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Life extends GameObject implements Collidable {
    private boolean collected;

    public Life(TextureRegion region, float x, float y) {
        super(region, x, y, region.getRegionWidth() - 6, region.getRegionHeight() - 8);
        this.collected = false;
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public void handleCollision(Collidable other) {
        // No specific collision handling needed in this class
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.LIFE;
    }
}

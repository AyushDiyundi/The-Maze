package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Key extends GameObject implements Collidable {
    private boolean collected = false;

    public Key(TextureRegion region, float x, float y) {
        super(region, x, y, region.getRegionWidth(), region.getRegionHeight());
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public void handleCollision(Collidable other) {
        // Handle any specific behavior for Key collisions here
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.KEY;
    }


}

package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Key extends GameObject implements Collidable {
    public Key(TextureRegion region,float x, float y) {
        super(region,x, y, region.getRegionWidth(), region.getRegionHeight());

        // The textureRegion will be set by the GameObjectFactory or similar class
    }

    @Override
    public void handleCollision(Collidable other) {

    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.KEY;
    }
}

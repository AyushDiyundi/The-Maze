package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Trap extends GameObject implements Collidable {
    public Trap(TextureRegion region,float x, float y) {
        super(region,x, y, region.getRegionWidth()-4, region.getRegionHeight()-4);
        // The textureRegion will be set by the GameObjectFactory or similar class
    }

    @Override
    public void handleCollision(Collidable other) {

    }

    public ObjectType getObjectType() {
        return ObjectType.TRAP; // Return the type for Trap
    }
}

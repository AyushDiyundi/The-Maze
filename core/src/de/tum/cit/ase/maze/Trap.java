package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Trap extends GameObject {
    public Trap(TextureRegion region,float x, float y) {
        super(region,x, y, region.getRegionWidth()-1, region.getRegionHeight()-1);
        // The textureRegion will be set by the GameObjectFactory or similar class
    }
    public ObjectType getObjectType() {
        return ObjectType.TRAP; // Return the type for Trap
    }
}

package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Entry extends GameObject {
    public Entry(TextureRegion region,float x, float y) {
        super(region,x, y);
        // The textureRegion will be set by the GameObjectFactory or similar class
    }
}

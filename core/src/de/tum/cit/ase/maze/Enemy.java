package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy extends GameObject implements Collidable {
    public Enemy(TextureRegion region,float x, float y) {
        super(region,x, y, region.getRegionWidth(),region.getRegionHeight());
        // The textureRegion will be set by the GameObjectFactory or similar class
    }
    public ObjectType getObjectType() {
        return ObjectType.ENEMY; // Return the type for Enemy
    }
    @Override
    public void handleCollision(Collidable other) {

    }
}

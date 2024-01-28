package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public abstract class DynamicObject extends GameObject {
    protected Rectangle boundingBox;

    public DynamicObject(TextureRegion region, float x, float y) {
        super(region, x, y, region.getRegionWidth(), region.getRegionWidth());
        this.boundingBox = new Rectangle(x, y, region.getRegionWidth(), region.getRegionHeight());
    }
    protected void updateBoundingBox() {
        boundingBox.setPosition(x, y);
    }

    public abstract void onCollision(GameObject other);
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void move(float deltaX, float deltaY, Iterable<GameObject> otherObjects) {
        // Update the position
        x += deltaX;
        y += deltaY;

        // Update the bounding box
        updateBoundingBox();

        // Check for collisions
        for (GameObject otherObject : otherObjects) {
            if (this != otherObject && boundingBox.overlaps(otherObject.getBoundingBox())) {
                onCollision(otherObject);
            }
        }
    }

    // Other methods...
}
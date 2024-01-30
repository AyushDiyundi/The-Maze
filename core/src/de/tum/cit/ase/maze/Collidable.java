package de.tum.cit.ase.maze;

import com.badlogic.gdx.math.Rectangle;

public interface Collidable {
    void handleCollision(Collidable other);
    ObjectType getObjectType();

    Rectangle getBoundingBox();
}
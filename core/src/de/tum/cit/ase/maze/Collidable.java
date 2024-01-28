package de.tum.cit.ase.maze;
public interface Collidable {
    void handleCollision(Collidable other);
    ObjectType getObjectType();
}
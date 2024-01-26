package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class GameObject {
    protected TextureRegion textureRegion;
    protected float x, y;

    public GameObject(TextureRegion region, float x, float y) {
        this.textureRegion = region;
        this.x = x * 16; // Assuming each tile is 16x16 pixels
        this.y = y * 16;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(textureRegion, x, y);
    }
}
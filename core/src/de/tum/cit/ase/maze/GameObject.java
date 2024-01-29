package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    protected TextureRegion textureRegion;
    protected float x, y;
    protected Rectangle boundingBox;

    public GameObject(TextureRegion region, float x, float y, float width,float height) {
        this.textureRegion = region;
        this.x = x * 16;
        this.y = y * 16;
        this.boundingBox = new Rectangle(this.x,this.y, width,height);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(textureRegion,this.x,this.y);
    }
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

}
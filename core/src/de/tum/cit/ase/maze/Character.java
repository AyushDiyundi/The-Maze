package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Character extends GameObject {
    private Animation<TextureRegion> animation;
    private float stateTime = 0f;

    public Character(Animation<TextureRegion> animation, float x, float y) {
        super(null, x, y);
        this.animation = animation;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y);
    }

    // Getter methods for x and y
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}

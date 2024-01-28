package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Character extends GameObject {
    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0f;
    private float speed = 100f;
    private boolean isMoving;
    private GameScreen gameScreen;

    // Different animations for each direction
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private float x;
    private float y;



    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Character(TextureRegion region, float x, float y) {
        super(region, x, y);
    }

    public Character(Animation<TextureRegion> up, Animation<TextureRegion> down,
                     Animation<TextureRegion> left, Animation<TextureRegion> right,
                     float x, float y) {
        super(null, x, y);
        this.upAnimation = up;
        this.downAnimation = down;
        this.leftAnimation = left;
        this.rightAnimation = right;
        this.currentAnimation = down; // Default animation
    }

    public void move(float delta) {
        float previousX = x;
        float previousY = y;
        float distance = speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= distance;
            currentAnimation = leftAnimation;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += distance;
            currentAnimation = rightAnimation;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += distance;
            currentAnimation = upAnimation;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= distance;
            currentAnimation = downAnimation;
        }
        isMoving = x != previousX || y != previousY;

    }

    public void update(float delta) {
        if (isMoving) {
            stateTime += delta; // Only update stateTime if the character is moving
        }
    }


    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        float characterWidth = 14; // Increase for larger character
        float characterHeight = 20; // Increase for larger character

        batch.draw(currentFrame, x, y, characterWidth, characterHeight);
    }

}

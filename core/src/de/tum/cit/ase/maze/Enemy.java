package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;


public class Enemy extends GameObject {
    private float stateTime = 0f; // Initialized to 0
    private Direction currentDirection = Direction.RIGHT; // Initial direction
    private int steps = 0; // Steps counter
    private Animation<TextureRegion> animation;
    private int stepsToChangeDirection = 100; // Number of steps before changing direction

    private float x;
    private float y;
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Enemy(Animation<TextureRegion> animation, float x, float y) {
        super(null, x * 16, y * 16, 15,15);

        this.animation = animation;
        this.currentDirection = Direction.RIGHT; // Set an initial direction
        this.steps = 0;
        this.x = x * 16; // Store the pixel-based position
        this.y = y * 16;
    }


    public void update(float delta) {
        stateTime += delta;

        // Move the enemy in the current direction
        float speed = 1.0f; // Adjust the speed as needed
        float stepSize = 16.0f; // Assuming each step is 16 pixels

        switch (currentDirection) {
            case UP:
                y += speed * stepSize * delta;
                break;
            case DOWN:
                y -= speed * stepSize * delta;
                break;
            case LEFT:
                x -= speed * stepSize * delta;
                break;
            case RIGHT:
                x += speed * stepSize * delta;
                break;
        }

        // Check if it's time to change direction
        if (steps >= stepsToChangeDirection) {
            changeDirection();
            steps = 0;
        } else {
            steps++;
        }
        setX(this.x);
        setY(this.y);
    }

    private void changeDirection() {
        // Cycle through the directions
        Random random = new Random();
        currentDirection = Direction.values()[random.nextInt(Direction.values().length)];
        }


    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        batch.draw(currentFrame, x, y);
    }

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
}


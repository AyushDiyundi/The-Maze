package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


public class Character extends GameObject implements Collidable {
    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0f;
    private float speed = 40f;
    private boolean isMoving;
    private Rectangle boundingBox;

    // Different animations for each direction
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private GameScreen gameScreen;
    private float x,previousX ,potentialX;
    private float y,previousY,potentialY;
    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public Character(Animation<TextureRegion> up, Animation<TextureRegion> down,
                     Animation<TextureRegion> left, Animation<TextureRegion> right,
                     float x, float y) {
        super(null, x, y, 12.5f,14);
        this.x=x;
        this.y=y;
        this.upAnimation = up;
        this.downAnimation = down;
        this.leftAnimation = left;
        this.rightAnimation = right;
        this.currentAnimation = down; // Default animation
        this.boundingBox = new Rectangle(x, y, 12.5f, 14);
    }


    public void move(float delta) {
        previousX = x;
        previousY = y;
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
        Collidable collisionObject = gameScreen.checkCollision(x, y, this);
        if (collisionObject != null) {
            // Call the handleCollision method when a collision is detected
            handleCollision(collisionObject);
        } else {
            // If no collision, update the position
            setPosition(x, y);
        }

        isMoving = x!=previousX|| y !=previousY;
    }
    @Override
    public void handleCollision(Collidable other) {
        ObjectType type = other.getObjectType();

        switch (type) {
            case WALL:
                setPosition(previousX, previousY);
                System.out.println("wall collision works");
                break;
            case ENEMY:
                // Handle collision with enemy (e.g., decrease life, change color)
                break;
            // Add more cases for other object types as needed
        }
    }

    private void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        boundingBox.setPosition(x, y);
    }

    public void update(float delta) {
        if (isMoving) {
            stateTime += delta; // Only update stateTime if the character is moving
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        float characterWidth = 11; // Increase for larger character
        float characterHeight = 17; // Increase for larger character

        batch.draw(currentFrame, x, y, characterWidth, characterHeight);
    }
    @Override
    public ObjectType getObjectType() {
        return null;
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

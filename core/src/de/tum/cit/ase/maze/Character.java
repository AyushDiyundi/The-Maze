package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


public class Character extends GameObject implements Collidable {
    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0f;
    private float speed = 50f;
    private boolean isMoving;
    private Rectangle boundingBox;

    // Different animations for each direction
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private GameScreen gameScreen;
    private boolean hasKey = false;
    private boolean powerUpActive = false;
    private boolean isInvincible;
    private float invincibilityTime;
    private float x,previousX;
    private float y,previousY;

    private float powerUpTimer = 0f;
    private float originalSpeed = 50f; // Store the original speed
    private Color originalColor = Color.WHITE; // Store the original color
    private Collidable lastHazardCollided = null;

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public Character(Animation<TextureRegion> up, Animation<TextureRegion> down,
                     Animation<TextureRegion> left, Animation<TextureRegion> right,
                     float x, float y) {
        super(null, x, y, 12f,12);
        this.x=x;
        this.y=y;
        this.upAnimation = up;
        this.downAnimation = down;
        this.leftAnimation = left;
        this.rightAnimation = right;
        this.currentAnimation = down; // Default animation
        this.boundingBox = new Rectangle(x, y, 12.5f, 12);
        this.isInvincible = false;

        this.invincibilityTime = 3f;
    }

    public void move(float delta) {

        previousX = x;
        previousY = y;
        float potentialX = x;
        float potentialY = y;
        float distance = speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            potentialX -= distance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            potentialX += distance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            potentialY += distance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            potentialY -= distance;
        }

        // Check for collisions at the potential new position
        Collidable collidable = gameScreen.checkCollision(potentialX, potentialY, this);
        if (collidable != null) {
            this.handleCollision(collidable); // Handle collision for the character
            collidable.handleCollision(this); // Handle collision for the collided object
        } else {
            // Update the position if there is no collision
            setPosition(potentialX, potentialY);
        }
        updatePositionAndCheckCollisions(delta);
        // Update animation state
        updateAnimationState();
        isMoving = x!=previousX|| y !=previousY;
        if (lastHazardCollided != null && !boundingBox.overlaps(lastHazardCollided.getBoundingBox())) {
            lastHazardCollided = null; // Reset if no longer in collision with the last hazard
        }
    }

    private void updatePositionAndCheckCollisions(float delta) {
        Collidable collisionObject = gameScreen.checkCollision(x, y, this);
        if (collisionObject != null) {
            handleCollision(collisionObject);
        } else {
            setPosition(x, y); // Make sure this also updates the bounding box
        }
    }

    private void updateAnimationState() {
        if (x < previousX) {
            currentAnimation = leftAnimation;
        } else if (x > previousX) {
            currentAnimation = rightAnimation;
        } else if (y < previousY) {
            currentAnimation = downAnimation;
        } else if (y > previousY) {
            currentAnimation = upAnimation;
        }
        // You might need to add an else clause to handle the case where the character is not moving
    }
    @Override
    public void handleCollision(Collidable other) {
        ObjectType type = other.getObjectType();
        if (isInvincible||(lastHazardCollided == other)){
            return;
        }
        switch (type) {
            case WALL:
                setPosition(previousX, previousY);
                break;
            case ENEMY:
                if (!isInvincible) {
                    gameScreen.getGame().getHurt().play();
                    handleHazardCollision();}
                break;
            case ENTRY:
                gameScreen.getGame().getUnlock().play();
                setPosition(previousX,previousY);
                break;
            case KEY:
                Key key = (Key) other;
                gameScreen.getGame().getUnlock().play();
                // Mark the key as collected
                key.setCollected(true);
                setPosition(x, y);

                // Set the flag to indicate that the character has collected the key
                hasKey = true;
                updateHUD();
                setPosition(x,y);
                break;

            case EXIT:
                if (hasKey) {
                    gameScreen.getGame().getUnlock().play();
                    gameScreen.setVictory(true);
                    gameScreen.goToExitScreen();}
                break;
            case POWER:
                if (!powerUpActive) {
                    gameScreen.getGame().getBuff().play();
                    Power power = (Power) other;
                    powerUpActive = true;
                    powerUpTimer = 3f; // 3 seconds of increased speed
                    originalSpeed = speed; // Store the original speed
                    originalColor = Color.WHITE; // Store the original color
                    speed *= 2; // Double the character's speed (adjust as needed)
                    // Change the character's color to yellow
                    // setColor(Color.YELLOW);
                    // Remove the collected power-up from the gameObjects list
                    gameScreen.getGameObjects().remove(other);
                }
                setPosition(x,y);
                break;
            case TRAP:
                if (!isInvincible) {
                    gameScreen.getGame().getHurt().play();
                    handleHazardCollision();}
                break;

            case LIFE:
                if (!((Life) other).isCollected()) {
                    gameScreen.getGame().getBuff().play();
                    ((Life) other).setCollected(true); // Mark the life as collected
                    gameScreen.setLives(gameScreen.getLives() + 1); // Increase the character's lives by 1
                    // The HUD update should be handled within setLives()
                    // Remove the collected life from the gameObjects list
                    gameScreen.getGameObjects().remove(other);
                }
                break;



        }
    }
    private void handleHazardCollision() {
        gameScreen.setLives(gameScreen.getLives() - 1); // Notify GameScreen about the change in lives
        becomeInvincible();
}
    private void becomeInvincible() {
        isInvincible = true;
        invincibilityTime = 3.0f; // 3 seconds of invincibility, for example
    }

    private void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.boundingBox.setPosition(this.x, this.y);
    }

    public void update(float delta) {


        if (isInvincible) {
            invincibilityTime -= delta;
            if (invincibilityTime <= 0) {
                isInvincible = false; // Character is no longer invincible
            }
        }

        // Handle power-up timer
        if (powerUpActive) {
            powerUpTimer -= delta;
            if (powerUpTimer <= 0) {
                // Power-up duration has ended, revert speed and color
                powerUpActive = false;
                speed = originalSpeed; // Restore the original speed
                // setColor(originalColor); // Restore the original color
            }
        }

        if (isMoving) {
            stateTime += delta; // Only update stateTime if the character is moving
        }
    }
    @Override
    public void draw(SpriteBatch batch) {
        if ( isInvincible) {
            batch.setColor(Color.RED); // Red tint for knockback and invincibility
        } else if (powerUpActive) {
            batch.setColor(Color.YELLOW); // Yellow tint for power-up
        } else {
            batch.setColor(Color.WHITE); // Default color
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        float characterWidth = 13; // Increase for larger character
        float characterHeight = 18; // Increase for larger character

        batch.draw(currentFrame, x, y, characterWidth, characterHeight);
        batch.setColor(Color.WHITE);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.CHARACTER;
    }
    public void updateHUD() {
        if (gameScreen != null) {
            gameScreen.updateKeyVisibility(hasKey);
        }
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

    public boolean isInvincible() {
        return isInvincible;
    }
}

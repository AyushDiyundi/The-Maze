package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


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
    private float knockbackSpeed;
    private float knockbackTime;
    private float knockbackRemaining;
    private Vector2 knockbackDirection;
    private float x,previousX ,potentialX;
    private int lives;
    private boolean isInKnockback,handlingKnockback;
    private float y,previousY,potentialY;
    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public Character(Animation<TextureRegion> up, Animation<TextureRegion> down,
                     Animation<TextureRegion> left, Animation<TextureRegion> right,
                     float x, float y) {
        super(null, x, y, 12.5f,12);
        this.x=x;
        this.y=y;
        this.upAnimation = up;
        this.downAnimation = down;
        this.leftAnimation = left;
        this.rightAnimation = right;
        this.currentAnimation = down; // Default animation
        this.boundingBox = new Rectangle(x, y, 12.5f, 12);
        this.isInvincible = false;
        this.lives=3;
        this.invincibilityTime = 3f;
        this.knockbackSpeed = 0.7f;
        this.knockbackTime = 1f;
        this.knockbackDirection = new Vector2();
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
        Collidable collisionObject = gameScreen.checkCollision(potentialX, potentialY, this);
        if (collisionObject != null) {
            // Handle the collision
            handleCollision(collisionObject);
        } else {
            // Update the position if there is no collision
            setPosition(potentialX, potentialY);
        }
        updatePositionAndCheckCollisions(delta);

        // Update animation state
        updateAnimationState();
        isMoving = x!=previousX|| y !=previousY;
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
        if (isInvincible && (type == ObjectType.ENEMY || type == ObjectType.TRAP)) {
            return;
        }
        switch (type) {
            case WALL:
                setPosition(previousX, previousY);
                break;
            case ENEMY:
                gameScreen.setLives(getLives() - 1); // Decrease lives
                gameScreen.updateHUD(); // Update the HUD
                    Vector2 enemyPosition = new Vector2(((Enemy) other).getX(),((Enemy) other).getX());
                    //applyKnockback(enemyPosition);
                    becomeInvincible();
                break;
            case ENTRY:
                setPosition(previousX,previousY);
                break;
            case KEY:
                hasKey=true;
                setPosition(x,y);
                break;
            case EXIT:
                break;
            case POWER:
                powerUpActive=true;
                setPosition(x,y);
                break;
            case TRAP:
                gameScreen.setLives(getLives() - 1); // Decrease lives
                gameScreen.updateHUD(); // Update the HUD
                becomeInvincible();
                becomeInvincible();
                break;
            case LIFE:
                if (!((Life) other).isCollected()) {
                    ((Life) other).setCollected(true); // Mark the life as collected
                    gameScreen.setLives(getLives() + 1); // Increase the character's lives
                    gameScreen.createHeartImage(); // Add a new heart image to the HUD
                    // Remove the collected life from the gameObjects list
                    gameScreen.getGameObjects().remove(other);
                    setPosition(x, y); // Update the character's position
                }
                break;



        }
    }
    private void applyKnockback(Vector2 knockbackSource) {
        isInKnockback=true;
        knockbackTime =1f;
        knockbackDirection.set(this.x - knockbackSource.x, this.y - knockbackSource.y).nor();
        handlingKnockback = true; // Set the flag
        attemptKnockbackPosition();
        handlingKnockback = false;
    }
    private void attemptKnockbackPosition() {
        float potentialX = x + knockbackDirection.x * knockbackSpeed;
        float potentialY = y + knockbackDirection.y * knockbackSpeed;
        boolean canMoveX = gameScreen.checkCollision(potentialX, this.y, this) == null;
        boolean canMoveY = gameScreen.checkCollision(this.x, potentialY, this) == null;

        // Move in the free direction(s)
        if (canMoveX) {
            this.x = potentialX;
        }
        if (canMoveY) {
            this.y = potentialY;
        }
        // Update the character's position and bounding box
        setPosition(this.x, this.y);
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

        // Update knockback movement
        if (isInKnockback) {
            knockbackTime -= delta;
            attemptKnockbackPosition();
            if (knockbackTime <= 0) {
                isInKnockback = false; // End knockback effect
            }
        }

        if (isMoving) {
            stateTime += delta; // Only update stateTime if the character is moving
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isInKnockback || isInvincible) {
            batch.setColor(Color.RED); // Red tint for knockback and invincibility
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

    public void setInvincible(boolean invincible) {
        isInvincible = invincible;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;

        // Check if lives are 0 and transition to the menu screen
        if (this.lives <= 0) {
            gameScreen.goToMenuScreen();
        }
    }


    public boolean isHasKey() {
        return hasKey;
    }

    public boolean isPowerUpActive() {
        return powerUpActive;
    }
}

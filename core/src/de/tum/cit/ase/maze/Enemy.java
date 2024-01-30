package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;
public class Enemy extends GameObject implements Collidable {
    private float stateTime = 0f;
    private final float followThreshold;
    private Direction currentDirection;
    private Direction previousDirection;
    private final Animation<TextureRegion> animation;
    private final GameScreen gameScreen;
    private float speed;
    private final Vector2 targetPosition;
    private final Random random;
    private final float maxDuration;
    private final float normalSpeed;
    private float x, y;
    private float timer;
    private float speedReductionTimer;
    private boolean isFollowingPlayer;
    private boolean debugMode = false; // Set to true for debugging

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Enemy(GameScreen gameScreen, Animation<TextureRegion> animation, float x, float y) {
        super(null, x * 16, y * 16, 12, 12);
        this.gameScreen = gameScreen;
        this.animation = animation;
        this.x = x * 16;
        this.y = y * 16;
        this.normalSpeed = 15f;
        this.random = new Random();
        this.followThreshold =2*16;
        this.targetPosition = new Vector2(x, y);
        this.maxDuration = random.nextFloat(3f, 6f);
        this.speed = normalSpeed;
        this.speedReductionTimer = 0;
        this.isFollowingPlayer = false;
        this.currentDirection = Direction.RIGHT;
    }
    public void update(float delta) {
        stateTime += delta;
        if (speedReductionTimer > 0) {
            speedReductionTimer -= delta;
            speed = normalSpeed - 10; // Reduced speed, adjust as necessary
        } else {
            speed = normalSpeed; // Reset to normal speed
        }
        Vector2 moveDirection = getDirectionVector(currentDirection);
        float potentialX = x + moveDirection.x * speed * delta;
        float potentialY = y + moveDirection.y * speed * delta;

        // Check for collisions at the potential new position
        Collidable collidable = gameScreen.checkCollision(potentialX, potentialY, this);
        if (collidable != null) {
            this.handleCollision(collidable); // Handle collision for the enemy
            collidable.handleCollision(this); // Handle collision for the collided object
        } else {
            // If no collision, update position
            setPosition(potentialX, potentialY);
        }

        float distanceToPlayer = Vector2.dst(x, y, gameScreen.getPlayer().getX(), gameScreen.getPlayer().getY());
        if (distanceToPlayer <= followThreshold && !gameScreen.getPlayer().isInvincible()) {
            followPlayer(delta);
        } else {
            randomMove(delta);
        }
    }

    private void followPlayer(float delta) {
        targetPosition.set(gameScreen.getPlayer().getX(), gameScreen.getPlayer().getY());
        if (!isFollowingPlayer) {
            speed += 10;
            isFollowingPlayer = true;
        }
        moveToTarget(delta, targetPosition);
    }

    private void randomMove(float delta) {
        if (timer >= maxDuration) {
            changeDirectionAvoidingObstacles(); // Change direction with collision checking
            timer = 0;
        } else {
            timer += delta;
            Vector2 moveDirection = getDirectionVector(currentDirection);
            float potentialX = x + moveDirection.x * speed * delta;
            float potentialY = y + moveDirection.y * speed * delta;

            // Check if the new position is valid (no collision and within maze bounds)
            if (isPositionValid(potentialX, potentialY)) {
                setPosition(potentialX, potentialY); // Move to the new position if valid
            } else {
                changeDirectionAvoidingObstacles(); // Change direction if collision detected or out of bounds
            }
        }
    }

    private void changeDirectionAvoidingObstacles() {
        Direction newDirection=null;
        boolean validDirectionFound = false;

        for (int i = 0; i < Direction.values().length; i++) {
            newDirection = Direction.values()[random.nextInt(Direction.values().length)];

            if (!newDirection.equals(previousDirection) && isDirectionValid(newDirection)) {
                validDirectionFound = true;
                break;
            }
        }

        if (validDirectionFound) {
            previousDirection = currentDirection; // Update previous direction
            currentDirection = newDirection;      // Change to the new valid direction
        } else {
            // If no valid direction is found, revert to the previous direction
            currentDirection = previousDirection;
        }
    }

    private boolean isDirectionValid(Direction direction) {
        Vector2 moveDirection = getDirectionVector(direction);
        float potentialX = x + moveDirection.x;
        float potentialY = y + moveDirection.y;
        return isPositionValid(potentialX, potentialY);
    }
    private void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.getBoundingBox().setPosition(x, y);
        // Update the bounding box or any other relevant position-dependent properties
    }
    private Vector2 getDirectionVector(Direction direction) {
        if (direction == null) {
            return new Vector2(0, 0); // Default to stationary
        }
        switch (direction) {
            case UP:
                return new Vector2(0, 1); // Moving upwards increases the Y value
            case DOWN:
                return new Vector2(0, -1); // Moving downwards decreases the Y value
            case LEFT:
                return new Vector2(-1, 0); // Moving left decreases the X value
            case RIGHT:
                return new Vector2(1, 0); // Moving right increases the X value
            default:
                return new Vector2(0, 0); // Stationary or default case
        }
    }
    private void moveToTarget(float delta, Vector2 target) {
        Vector2 moveDirection = new Vector2(target.x - x, target.y - y).nor();
        float nextX, nextY;

        for (float step = 0; step < speed * delta; step += 1.0f) { // Smaller steps for collision checking
            nextX = x + moveDirection.x * step;
            nextY = y + moveDirection.y * step;

            if (!isPositionValid(nextX, nextY)) {
                slideAlongObstacle(moveDirection); // Slide along obstacle if collision detected
                return; // Stop moving after handling collision
            }
            else {
                handleCollisions(delta,isFollowingPlayer);
            }
        }

        // Apply full movement if no collision detected at increments
        x += moveDirection.x * speed * delta;
        y += moveDirection.y * speed * delta;
        this.getBoundingBox().setPosition(x, y);
    }
    private void handleCollisions(float delta, boolean isFollowingPlayer) {
        Collidable collidableObject = gameScreen.checkCollision(x, y, this);
        if (collidableObject != null) {
            if (isFollowingPlayer && collidableObject.getObjectType() != ObjectType.CHARACTER) {
                Vector2 moveDirection = getDirectionVector(currentDirection);
                slideAlongObstacle(moveDirection);
            } else {
                handleCollision(collidableObject);
            }
        }
    }
    private void slideAlongObstacle(Vector2 moveDirection) {
        // Determine slide direction based on moveDirection
        // For example, if moving right, check up and down for sliding

        // Example for sliding vertically:
        if (moveDirection.x != 0) {
            float slideY = moveDirection.x > 0 ? findSlidePositionY(x + 1, y) : findSlidePositionY(x - 1, y);
            if (slideY != y) {
                y = slideY;
                this.getBoundingBox().setPosition(x, y);
            }
        }
        // Similarly, handle horizontal sliding when moving vertically
    }

    private float findSlidePositionY(float x, float startY) {
        float slideY = startY;
        float step = 1.0f; // Incremental step for checking positions

        // Check upward for a valid Y position
        while (!isPositionValid(x, slideY)) {
            slideY += step;
            if (slideY >= gameScreen.getMazeHeight()) {
                // Reached the top boundary, return the original startY
                return startY;
            }
        }

        // Reset slideY to the original startY for downward checking
        slideY = startY;

        // Check downward for a valid Y position
        while (!isPositionValid(x, slideY)) {
            slideY -= step;
            if (slideY < 0) {
                // Reached the bottom boundary, return the original startY
                return startY;
            }
        }

        // Return the nearest valid Y position found
        return slideY;
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

    @Override
    public void handleCollision(Collidable other) {
        ObjectType type = other.getObjectType();

        switch (type) {
            case WALL,TRAP,ENEMY,ENTRY,EXIT:
                changeDirection();
                break;
            case CHARACTER:
                if (!((Character) other).isInvincible()) {
                    gameScreen.setLives(gameScreen.getLives() - 1);
                    gameScreen.updateHUD();
                    speedReductionTimer = 4.0f;
                    isFollowingPlayer=false;
                    changeDirection();
                    System.out.println("Enemy collided with non-invincible character");
                }
                else
                {
                    System.out.println("Enemy collided with invincible character");
                    changeDirection();
                }
                break;

        }

    }

    private void changeDirection() {
        if (debugMode) {
            System.out.println("change direction called");
        }
        Direction newDirection = currentDirection;
        boolean validDirectionFound = false;

        for (int i = 0; i < Direction.values().length; i++) {
            newDirection = Direction.values()[random.nextInt(Direction.values().length)];
            Vector2 moveDirection = getDirectionVector(newDirection);
            if (isPositionValid(x + moveDirection.x, y + moveDirection.y)) {
                validDirectionFound = true;
                break;
            }
        }

        if (validDirectionFound) {
            currentDirection = newDirection;
        } else {
            currentDirection = previousDirection;
        }

        if (debugMode) {
            System.out.println("New direction: " + currentDirection);
        }
    }
    private boolean isPositionValid(float x, float y) {
        return x >= 0 && x < gameScreen.getMazeWidth() &&
                y >= 0 && y < gameScreen.getMazeHeight() &&
                (gameScreen.checkCollision(x, y, this) )== null;
    }
    @Override
    public ObjectType getObjectType() {
        return ObjectType.ENEMY;
    }
}


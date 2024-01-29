package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;


public class Enemy extends GameObject implements Collidable {
    private float stateTime = 0f;
    private final float followThreshold = 2 * 16; // Follow player if within this distance
    private Direction currentDirection = Direction.RIGHT;
    private Animation<TextureRegion> animation;
    private GameScreen gameScreen;
    private float speedReductionTimer;
    private float speed;
    private float normalSpeed = 10.0f;
    private float x;
    private float y;
    private boolean isPatrolling;
    private Vector2 targetPosition, lastPatrolPosition;
    private float patrolTime;
    private float currentPatrolTime;
    private Random random;
    private boolean canChangeDirection = true;
    private float changeDirectionCooldown = 2.0f;
    private float timeSinceLastDirectionChange = 0.0f;
    boolean isFollowingPlayer;

    @Override
    public ObjectType getObjectType() {
        return ObjectType.ENEMY;
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Enemy(GameScreen gameScreen, Animation<TextureRegion> animation, float x, float y) {
        super(null, x * 16, y * 16, 12,14);
        this.gameScreen = gameScreen;
        this.animation = animation;
        this.speedReductionTimer=0;
        this.currentDirection = Direction.RIGHT; // Set an initial direction
        this.x = x * 16; // Store the pixel-based position
        this.y = y * 16;
        this.targetPosition = new Vector2(x, y);
        random = new Random();
        this.isPatrolling=true;
        patrolTime = 5f; // Time in seconds to change direction
        currentPatrolTime =0;
        changeDirection();
    }


    public void update(float delta) {
        stateTime += delta;
        speedReduction(delta);
        float distanceToPlayer = Vector2.dst(x, y, gameScreen.getPlayer().getX(), gameScreen.getPlayer().getY());
        boolean isFollowingPlayer = distanceToPlayer <= followThreshold;
        handleCooldown(delta);
        if (isFollowingPlayer) {
            handlePlayerFollowing();
        } else {
            handlePatrolling(delta);
        }
        moveToTarget(delta,targetPosition);
        handleCollisions(delta, isFollowingPlayer);

    }
    private void handleCooldown(float delta) {
        if (!canChangeDirection) {
            timeSinceLastDirectionChange += delta;
            if (timeSinceLastDirectionChange >= changeDirectionCooldown) {
                canChangeDirection = true;
                timeSinceLastDirectionChange = 0.0f;
            }
        }
    }
    private void handlePlayerFollowing() {
        lastPatrolPosition = new Vector2(x, y);
        speed = normalSpeed + 10;
        isPatrolling = false;
        targetPosition.set(gameScreen.getPlayer().getX(), gameScreen.getPlayer().getY());
    }

    private void handlePatrolling(float delta) {
        if (!isPatrolling && lastPatrolPosition != null) {
            targetPosition.set(lastPatrolPosition);
            isPatrolling = true;
        } else {
            patrol(delta);
        }
    }
    private void changeDirectionAvoidingObstacles() {
        if (!canChangeDirection || !isPatrolling) return;

        Direction newDirection;
        do {
            newDirection = Direction.values()[random.nextInt(Direction.values().length)];
        } while (newDirection == currentDirection || !isDirectionValid(newDirection));

        currentDirection = newDirection;
        canChangeDirection = false;
    }

private boolean isDirectionValid(Direction direction) {
    Vector2 moveDirection = getDirectionVector(direction);
    float potentialX = x + moveDirection.x;
    float potentialY = y + moveDirection.y;
    return gameScreen.checkCollision(potentialX, potentialY, this) == null &&
            potentialX >= 0 && potentialX < gameScreen.getMazeWidth() &&
            potentialY >= 0 && potentialY < gameScreen.getMazeHeight();
}
    private void speedReduction(float delta) {
        if (speedReductionTimer > 0) {
            speedReductionTimer -= delta;
            speed =normalSpeed-10; // Reduced speed
        } else {
            speed = normalSpeed; // Reset to normal speed
        }
    }
    private void moveToTarget(float delta, Vector2 target) {
        Vector2 moveDirection = new Vector2(target.x - x, target.y - y).nor();
        float nextX = x + moveDirection.x * speed * delta;
        float nextY = y + moveDirection.y * speed * delta;
        if (isPatrolling || gameScreen.checkCollision(nextX, nextY, this) == null) {
            setPosition(nextX, nextY);
        } else {
            changeDirection();
        }
    }
    private void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        // Ensure that the bounding box is updated with the new position
        this.getBoundingBox().setPosition(x, y);
    }
    private void patrol(float delta) {
        currentPatrolTime += delta;
        if (currentPatrolTime >= patrolTime) {
            currentPatrolTime = 0;
            changeDirection();
        }
        Vector2 moveDirection = getDirectionVector(currentDirection);
        moveToTarget(delta, new Vector2(x + moveDirection.x, y + moveDirection.y));
    }
    private Vector2 getDirectionVector(Direction direction) {
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
                return new Vector2(0, 0); // Stationary if direction is undefined
        }
    }

    private void handleCollisions(float delta, boolean isFollowingPlayer) {
        Collidable collidableObject = gameScreen.checkCollision(x, y, this);
        if (collidableObject != null) {
            if (isFollowingPlayer && collidableObject.getObjectType() != ObjectType.CHARACTER) {
                slideAlongObstacle(collidableObject);
            } else {
                handleCollision(collidableObject);
            }
        }
    }
    @Override
    public void handleCollision(Collidable other) {
        ObjectType type = other.getObjectType();

        switch (type) {
            case WALL,TRAP:
                slideAlongObstacle(other);
                break;
            case ENEMY,ENTRY,EXIT:
                changeDirection();
                break;
            case CHARACTER:
                speedReductionTimer = 4.0f;
                if (((Character) other).isInvincible()) {
                    isFollowingPlayer=false;
                    changeDirection();
                }
                break;

        }
    }
    private void slideAlongObstacle(Collidable obstacle) {
        // Check if sliding is possible on either axis
        boolean canSlideX = gameScreen.checkCollision(x + 1, y, this) == null ||
                gameScreen.checkCollision(x - 1, y, this) == null;
        boolean canSlideY = gameScreen.checkCollision(x, y + 1, this) == null ||
                gameScreen.checkCollision(x, y - 1, this) == null;

        if (canSlideX) {
            x += gameScreen.checkCollision(x + 1, y, this) == null ? 1 : -1;
        } else if (canSlideY) {
            y += gameScreen.checkCollision(x, y + 1, this) == null ? 1 : -1;
        } else {
            changeDirection(); // If sliding is not possible in either direction
        }
    }
    private void changeDirection() {
        Direction newDirection = currentDirection;
        boolean validDirectionFound = false;

        while (!validDirectionFound) {
            // Randomly choose a new direction
            newDirection = Direction.values()[random.nextInt(Direction.values().length)];

            // Calculate potential new position
            Vector2 moveDirection = getDirectionVector(newDirection);
            float potentialX = x + moveDirection.x;
            float potentialY = y + moveDirection.y;

            // Check if the new position is within the maze bounds
            if (!(potentialX < 0 || potentialX >= gameScreen.getMazeWidth() || potentialY < 0 || potentialY >= gameScreen.getMazeHeight())) {
                validDirectionFound = true;
            }
        }

        currentDirection = newDirection;
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


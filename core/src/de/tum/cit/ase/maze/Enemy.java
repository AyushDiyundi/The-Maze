package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;


public class Enemy extends GameObject implements Collidable {
    private float stateTime = 0f; // Initialized to 0
    private final float followThreshold = 7 * 16;
    private Direction currentDirection = Direction.RIGHT; // Initial direction
    private int steps = 0; // Steps counter
    private Animation<TextureRegion> animation;
    private GameScreen gameScreen;
    private float speedReductionTimer;
    private float speed;
    private float normalSpeed = 1.0f;
    private float followingSpeed = 2.0f;
    private float x;
    private float y;
    private Vector2 targetPosition;
    private float patrolTime;
    private float currentPatrolTime;
    private Random random;


    @Override
    public ObjectType getObjectType() {
        return ObjectType.ENEMY;
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Enemy(GameScreen gameScreen, Animation<TextureRegion> animation, float x, float y) {
        super(null, x * 16, y * 16, 15,15);
        this.gameScreen = gameScreen;
        this.animation = animation;
        this.speedReductionTimer=0;
        this.currentDirection = Direction.RIGHT; // Set an initial direction
        this.steps = 0;
        this.x = x * 16; // Store the pixel-based position
        this.y = y * 16;
        this.targetPosition = new Vector2(x, y);
        random = new Random();
        patrolTime = 5f; // Time in seconds to change direction
        currentPatrolTime = 0;
    }


    public void update(float delta) {
        stateTime += delta;
        float distanceToPlayer = Vector2.dst(x, y, gameScreen.getPlayer().getX(), gameScreen.getPlayer().getY());
        boolean isFollowingPlayer = distanceToPlayer <= followThreshold;
        if (isFollowingPlayer) {
            speed = followingSpeed;
            targetPosition.set(gameScreen.getPlayer().getX(), gameScreen.getPlayer().getY());
            moveToTarget(delta,targetPosition);
        } else {
            // Normal movement behavior
            speed = normalSpeed;
            patrol(delta);
        }
        handleCollisions(delta, isFollowingPlayer);

    }
    private void moveToTarget(float delta, Vector2 target) {
        Vector2 moveDirection = new Vector2(target.x - x, target.y - y).nor();
        x += moveDirection.x * speed * delta;
        y += moveDirection.y * speed * delta;
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
    private void avoidCollision(float delta, Collidable obstacle) {
        changeDirection();
        moveToTarget(delta, new Vector2(x, y));
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
                // Implement collision avoidance if needed
                avoidCollision(delta, collidableObject);
            } else {
                // Regular collision handling
                handleCollision(collidableObject);
            }
        }
    }
    @Override
    public void handleCollision(Collidable other) {
        ObjectType type = other.getObjectType();

        switch (type) {
            case WALL,TRAP,ENEMY,ENTRY,EXIT:
                changeDirection();
                break;
            case CHARACTER:
                speed=0.5f;
                speedReductionTimer = 2.0f;
                break;

        }
    }

    private void changeDirection() {
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


package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private MazeRunnerGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private List<GameObject> gameObjects;
    private Music gameMusic;
    private Character character;
    private int mazeWidth, mazeHeight;
    private TextureLoader textureLoader, enemyLoader,lifeLoader,powerLoader;
    private final float CAMERA_PADDING = 0.1f;
    private float spawnX, spawnY;
    private int[][] mazeData;
    private float initialZoom;
    private float minCameraX, maxCameraX, minCameraY, maxCameraY;
    private List<Vector2> enemySpawnPoints;
    private Animation<TextureRegion> bottomLeftEnemyAnimation;
    private Stage hudStage;
    private int lives; // Number of lives
    private boolean hasKey;
    private boolean powerUpActive;
    private float powerUpTimer;

    private ShapeRenderer shapeRenderer;
    private List<Image> heartImages;
    private Texture heartTexture;
    private Texture keyIconTexture;
    private Texture powerUpIconTexture;

    private void loadHudTextures() {
        heartTexture = new Texture(Gdx.files.internal("heart.png"));
        // Load key icon texture
        keyIconTexture = new Texture(Gdx.files.internal("powerUP.png"));
        // Load power-up icon texture
        powerUpIconTexture = new Texture(Gdx.files.internal("powerUp.png"));
    }

    private void initialize() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.5f;
        gameObjects = new ArrayList<>();
        enemySpawnPoints = new ArrayList<>(); // Initialize before loading game objects
        textureLoader = new TextureLoader(Gdx.files.internal("basictiles.png"));
        enemyLoader = new TextureLoader(Gdx.files.internal("mobs.png"));
        lifeLoader = new TextureLoader(Gdx.files.internal("objects.png"));
        powerLoader = new TextureLoader(Gdx.files.internal("basictiles.png"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("InGame.mp3"));
        bottomLeftEnemyAnimation = loadBottomLeftEnemyAnimation();
        calculateMazeDimensions();
        shapeRenderer = new ShapeRenderer();
    }
    private void initializeHUD() {
        hudStage = new Stage(new ScreenViewport());
        loadHudTextures();
        heartImages = new ArrayList<>();
    }


    public GameScreen(MazeRunnerGame game, int[][] mazeData) {
        this.game = game;
        this.mazeData = mazeData;
        this.lives=3;
        initialize(); // This will now properly initialize enemySpawnPoints before loadGameObjects is called
        initialZoom = calculateInitialZoom();
        calculateCameraConstraints();
        loadGameObjects(mazeData); // This is where game objects are actually loaded
        initializeHUD();
    }


    private float calculateInitialZoom() {
        // Calculate the initial zoom level based on maze and window dimensions
        float horizontalZoom = (float) Gdx.graphics.getWidth() / mazeWidth;
        float verticalZoom = (float) Gdx.graphics.getHeight() / mazeHeight;

        // Choose the smaller zoom factor to ensure the entire maze fits
        return Math.min(horizontalZoom, verticalZoom);
    }

    private void calculateCameraConstraints() {
        // Calculate camera position constraints based on zoom level
        float cameraWidth = Gdx.graphics.getWidth() / camera.zoom;
        float cameraHeight = Gdx.graphics.getHeight() / camera.zoom;

        // Calculate the middle 80% of the screen
        float paddingX = cameraWidth * 0.1f;
        float paddingY = cameraHeight * 0.1f;

        // Set camera position constraints
        minCameraX = paddingX;
        maxCameraX = mazeWidth - cameraWidth + paddingX;
        minCameraY = paddingY;
        maxCameraY = mazeHeight - cameraHeight + paddingY;

    }

    private void loadGameObjects(int[][] mazeData) {
        TextureRegion wallRegion = textureLoader.getTextureRegion(0, 0, 16, 16);
        TextureRegion keyRegion = textureLoader.getTextureRegion(6 * 16, 3 * 16, 16, 6);
        TextureRegion entryRegion = textureLoader.getTextureRegion(2 * 16, 6 * 16, 16, 16);
        TextureRegion exitRegion = textureLoader.getTextureRegion(0, 6 * 16, 16, 16);
        TextureRegion trapRegion = textureLoader.getTextureRegion(4* 16, 7 * 16, 16, 16);
        TextureRegion enemyRegion = enemyLoader.getTextureRegion(4 * 16, 5 * 16, 16, 16);
        TextureRegion lifeRegion = lifeLoader.getTextureRegion(4* 16, 0* 16, 16, 16);
        TextureRegion powerRegion = powerLoader.getTextureRegion(6* 16, 9* 16, 16, 16);

        Animation<TextureRegion> bottomLeftEnemyAnimation = loadBottomLeftEnemyAnimation();


        for (int x = 0; x < mazeData.length; x++) {
            for (int y = 0; y < mazeData[x].length; y++) {
                switch (mazeData[x][y]) {
                    case 0:
                        gameObjects.add(new Wall(wallRegion, x, y));
                        break;
                    case 1:
                        gameObjects.add(new Entry(entryRegion, x, y));
                        spawnX = x+1;
                        spawnY = y;
                        break;
                    case 2:
                        gameObjects.add(new Exit(exitRegion, x, y));
                        break;
                    case 3:
                        gameObjects.add(new Trap(trapRegion, x, y));
                        break;
                    case 4:
                        enemySpawnPoints.add(new Vector2(x, y));
                        break;
                    case 5:
                        gameObjects.add(new Key(keyRegion, x, y));
                        break;
                    case 6:
                        gameObjects.add(new Life(lifeRegion, x, y));
                        break;
                    case 7:
                        gameObjects.add(new Power(powerRegion, x, y));
                        break;
                    default:
                        break;
                }
            }
        }
        initializeCharacter();
        initializeEnemies();
    }

    private Animation<TextureRegion> loadBottomLeftEnemyAnimation() {
        Texture enemySheet = new Texture(Gdx.files.internal("mobs.png"));
        int frameCols = 3;  // Number of columns in the animation matrix for each enemy
        int frameRows = 4;  // Number of rows in the animation matrix for each enemy
        int enemyType = 1;  // Index of the enemy type you want to extract (0 for the first type)

        TextureRegion[][] tmpFrames = TextureRegion.split(enemySheet, 16, 16);
        TextureRegion[] bottomLeftEnemyFrames = new TextureRegion[frameCols * frameRows];

        // Calculate the starting row and column for the selected enemy type
        int startRow = enemyType * frameRows;
        int startCol = 0;  // Assuming the first enemy type always starts at column 0

        // Extract frames of the bottom-left enemy from the sprite sheet
        int frameIndex = 0;
        for (int row = startRow; row < startRow + frameRows; row++) {
            for (int col = startCol; col < startCol + frameCols; col++) {
                bottomLeftEnemyFrames[frameIndex] = tmpFrames[row][col];
                frameIndex++;
            }
        }


        return new Animation<>(0.1f, bottomLeftEnemyFrames);  // 0.1f is the frame duration
    }

    private void initializeEnemies() {
        for (Vector2 spawnPoint : enemySpawnPoints) {
            gameObjects.add(new Enemy(this, bottomLeftEnemyAnimation, spawnPoint.x, spawnPoint.y));
        }
    }


    private void initializeCharacter() {
        character = new Character(game.getCharacterUpAnimation(), game.getCharacterDownAnimation(),
                game.getCharacterLeftAnimation(), game.getCharacterRightAnimation(),
                spawnX * 16, spawnY * 16);
        character.setGameScreen(this);
    }
    private void renderHUD(SpriteBatch batch) {
        float hudX = 0; // Start at X coordinate 0
        float hudY = mazeWidth+33; // Start at Y coordinate 100 on the map
        int remainingLives =lives;
        for (int i = 0; i < 3; i++) { // Assuming the maximum number of lives is 3
            if (remainingLives > 0) {
                batch.draw(heartTexture, hudX, hudY, 16, 16);
                remainingLives--;
            }
            hudX += 20; // Move right for the next heart
        }

        // Draw key icon
        if (character.isHasKey()) {
            batch.draw(keyIconTexture, 0, hudY - 20, 16, 16);
            hudY -= 20;
        }

        // Draw power-up icon
        if (character.isPowerUpActive()) {
            batch.draw(powerUpIconTexture, 0, hudY, 20, 20);
        }
    }
    public Collidable checkCollision(float potentialX, float potentialY, GameObject object) {
        Rectangle potentialBounds = new Rectangle(potentialX, potentialY, object.getBoundingBox().width, object.getBoundingBox().height);
        for (GameObject gameObject : gameObjects) {
            // Skip the collision check for the object itself
            if (gameObject == object) {
                continue;
            }
            if (gameObject instanceof Collidable) {
                Collidable collidable = (Collidable) gameObject;

                // Check for an overlap between the two objects
                if (potentialBounds.overlaps(collidable.getBoundingBox())) {
                    return collidable;
                }
            }
        }
        return null;  // No collision detected
    }
    @Override

    // Clear the screen, update the game state, etc.
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Handle user input
        handleInput(delta);

        // Update the character and camera position
        if (character != null) {
            character.move(delta); // Update character movement
            character.update(delta); // Update character state
            updateCameraPosition(delta); // Update the camera to follow the character
        }

        // Update the camera and set the projection matrix
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        // Draw all game objects
        batch.begin();
        for (GameObject gameObject : gameObjects) {
            gameObject.draw(batch);
        }
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Enemy) {
                ((Enemy) gameObject).update(delta);
            }
            gameObject.draw(batch);
        }
        // Draw the character
        if (character != null) {
            character.draw(batch);
        }
        renderHUD(batch);
        batch.end();
        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        hudStage.draw();
    }
    public Character getPlayer() {
        return character;
    }


    private void updateCameraPosition(float delta) {
        float lerp = 0.1f; // Adjust this value as needed for smoother camera movement
        if (character != null) {
            float targetX = character.getX() + 14 / 2;
            float targetY = character.getY() + 20 / 2;
            camera.position.x += (targetX - camera.position.x) * lerp;
            camera.position.y += (targetY - camera.position.y) * lerp;
        }

        // Adjust zoom level if necessary
        camera.zoom = 0.8f; // Adjust these values as needed
        camera.update();
    }

    private void calculateMazeDimensions() {
        mazeWidth = mazeData.length * 16; // Assuming each cell is 16 pixels wide
        mazeHeight = mazeData[0].length * 16; // Assuming each cell is 16 pixels high
    }

    public void playGameMusic() {
        if (gameMusic != null) {
            gameMusic.setVolume(game.getSoundSettings().getGameMusicVolume());
            gameMusic.setLooping(true);
            gameMusic.play();
        }
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setPaused(true);
            game.goToMenu();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
        calculateCameraConstraints();
        hudStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
        if (!game.isPaused()) {
            playGameMusic();
        }
        if (character != null) {
            camera.position.set(character.getX(), character.getY(), 0);
            camera.zoom = 0.4f; // Adjust this value for desired zoom level
        }
        camera.update();
    }

    @Override
    public void hide() {
        if (gameMusic != null) {
            gameMusic.stop();
            gameMusic.dispose();
        }
    }

    @Override
    public void dispose() {
        textureLoader.dispose();
        batch.dispose();
        heartTexture.dispose();
        keyIconTexture.dispose();
        powerUpIconTexture.dispose();
        hudStage.dispose();
    }

    public float getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(float spawnX) {
        this.spawnX = spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(float spawnY) {
        this.spawnY = spawnY;
    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getLives() {
        return lives;
    }
}


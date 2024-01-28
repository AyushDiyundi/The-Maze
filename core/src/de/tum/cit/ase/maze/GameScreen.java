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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import org.w3c.dom.ls.LSOutput;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {
    private MazeRunnerGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private List<GameObject> gameObjects;
    private Music gameMusic;
    private Character character;
    private int mazeWidth, mazeHeight;
    private TextureLoader textureLoader,enemyLoader,characterLoader;
    private final float CAMERA_PADDING = 0.1f;
    private float spawnX, spawnY;
    private int[][] mazeData;
    private float initialZoom;
    private float minCameraX, maxCameraX, minCameraY, maxCameraY;
    private List<Vector2> enemySpawnPoints;
    private Animation<TextureRegion> bottomLeftEnemyAnimation;

    private void initialize() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.5f;
        gameObjects = new ArrayList<>();
        enemySpawnPoints = new ArrayList<>(); // Initialize before loading game objects
        textureLoader = new TextureLoader(Gdx.files.internal("basictiles.png"));
        enemyLoader = new TextureLoader(Gdx.files.internal("mobs.png"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("InGame.mp3"));
        bottomLeftEnemyAnimation = loadBottomLeftEnemyAnimation();
        calculateMazeDimensions();
        // loadGameObjects(mazeData); // Remove this redundant call
    }
    public GameScreen(MazeRunnerGame game, int[][] mazeData) {
        this.game = game;
        this.mazeData = mazeData;
        initialize(); // This will now properly initialize enemySpawnPoints before loadGameObjects is called
        initialZoom = calculateInitialZoom();
        calculateCameraConstraints();
        loadGameObjects(mazeData); // This is where game objects are actually loaded
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
        TextureRegion keyRegion = textureLoader.getTextureRegion(6*16, 3*16, 16, 6);
        TextureRegion entryRegion = textureLoader.getTextureRegion(2*16, 6*16, 16, 16);
        TextureRegion exitRegion = textureLoader.getTextureRegion(0, 6*16, 16, 16);
        TextureRegion trapRegion = textureLoader.getTextureRegion(2*16, 9*16, 16, 16);
        TextureRegion enemyRegion = enemyLoader.getTextureRegion(4*16, 5*16, 16, 16);
        Animation<TextureRegion> bottomLeftEnemyAnimation= loadBottomLeftEnemyAnimation();


        for (int x = 0; x < mazeData.length; x++) {
            for (int y = 0; y < mazeData[x].length; y++) {
                switch (mazeData[x][y]) {
                    case 0:
                        gameObjects.add(new Wall(wallRegion, x, y));
                        break;
                    case 1:
                        gameObjects.add(new Entry(entryRegion, x, y));
                        spawnX = x;
                        spawnY = y;

                        break;
                    case 2:
                        gameObjects.add(new Exit(exitRegion, x, y));
                        break;
                    case 3:
                        gameObjects.add(new Trap(trapRegion, x, y));
                        break;
                    case 4:
                        Gdx.app.log("Spawn", "Enemy spawn at: " + x + ", " + y);
                        enemySpawnPoints.add(new Vector2(x, y));


                        break;
                    case 5:
                        gameObjects.add(new Key(keyRegion, x, y));
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
        int frameCols = 4;  // 12 frames of animation
        int frameRows = 5;   // 8 types of enemies

        TextureRegion[][] tmpFrames = TextureRegion.split(enemySheet, 16, 16);
        TextureRegion[] bottomLeftEnemyFrames = new TextureRegion[frameCols];

        // Bottom left enemy is in the last row of the sprite sheet
        int bottomLeftRow = frameRows - 1;
        for (int col = 0; col < frameCols; col++) {
            bottomLeftEnemyFrames[col] = tmpFrames[bottomLeftRow][col];
        }

        return new Animation<>(0.1f, bottomLeftEnemyFrames);  // 0.1f is the frame duration
    }
    private void initializeEnemies() {
        for (Vector2 spawnPoint : enemySpawnPoints) {
            gameObjects.add(new Enemy(bottomLeftEnemyAnimation, spawnPoint.x, spawnPoint.y));
        }
    }



    private void initializeCharacter() {
        character = new Character(game.getCharacterUpAnimation(), game.getCharacterDownAnimation(),
                game.getCharacterLeftAnimation(), game.getCharacterRightAnimation(),
                spawnX*16  , spawnY*16 );
        character.setGameScreen(this);
    }
    @Override

        // Clear the screen, update the game state, etc.
        public void render ( float delta){
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



            batch.end();
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
        camera.zoom = 0.5f; // Adjust these values as needed

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
        camera.setToOrtho(false,width,height);
        camera.update();
        calculateCameraConstraints();
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

    public Collidable checkCollision(float potentialX, float potentialY, Character character) {
        Rectangle potentialBounds = new Rectangle(potentialX, potentialY, character.getBoundingBox().width, character.getBoundingBox().height);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Collidable && gameObject != character) {
                if (potentialBounds.overlaps(gameObject.getBoundingBox())) {
                    System.out.println("collision  "+gameObject);
                    return (Collidable) gameObject;
                }
            }
        }
        return null; // No collision detected
    }
}

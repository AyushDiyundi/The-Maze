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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {
    private final MazeRunnerGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private List<GameObject> gameObjects;
    private Music gameMusic;
    private Character character;
    private int mazeWidth, mazeHeight;
    private TextureLoader textureLoader, enemyLoader,lifeLoader,powerLoader,keyLoader;
    private float spawnX, spawnY;
    private final int[][] mazeData;
    private List<Vector2> enemySpawnPoints;
    private Animation<TextureRegion> bottomLeftEnemyAnimation;
    private Stage hudStage;
    private int lives; // Number of lives
    private boolean victory;

    private List<Image> heartImages;
    private Texture heartTexture;
    private Texture keyIconTexture;
    private int totalTime;
    private Image keyImage;
    private static final int BASE_SCORE = 1000;
    private static final int SCORE_PER_LIFE = 100;
    private static final int TIME_PENALTY = 10;


    private void loadHudTextures() {
        heartTexture = new Texture(Gdx.files.internal("heart.png"));
        // Load key icon texture
        keyIconTexture = new Texture(Gdx.files.internal("key2.png"));
    }

    private void initialize() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.5f;
        gameObjects = new ArrayList<>();
        enemySpawnPoints = new ArrayList<>(); // Initialize before loading game objects
        textureLoader = new TextureLoader(Gdx.files.internal("basictiles.png"));
        keyLoader=new TextureLoader(Gdx.files.internal("objects.png"));
        enemyLoader = new TextureLoader(Gdx.files.internal("mobs.png"));
        lifeLoader = new TextureLoader(Gdx.files.internal("objects.png"));
        powerLoader = new TextureLoader(Gdx.files.internal("basictiles.png"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("InGame.mp3"));
        bottomLeftEnemyAnimation = loadBottomLeftEnemyAnimation();
        calculateMazeDimensions();
    }



    public GameScreen(MazeRunnerGame game, int[][] mazeData) {
        this.game = game;
        this.mazeData = mazeData;
        this.lives=3;
        this.victory=false;
        loadHudTextures();
        initialize(); // This will now properly initialize enemySpawnPoints before loadGameObjects is called
        loadGameObjects(mazeData); // This is where game objects are actually loaded
        initializeHUD();
    }

    private void loadGameObjects(int[][] mazeData) {
        TextureRegion wallRegion = textureLoader.getTextureRegion(0, 0, 16, 16);
        TextureRegion keyRegion =  keyLoader.getTextureRegion(0, 4 * 16, 16, 16);
        TextureRegion entryRegion = textureLoader.getTextureRegion(2 * 16, 6 * 16, 16, 16);
        TextureRegion exitRegion = textureLoader.getTextureRegion(0, 6 * 16, 16, 16);
        TextureRegion trapRegion = textureLoader.getTextureRegion(4* 16, 7 * 16, 16, 16);
        TextureRegion enemyRegion = enemyLoader.getTextureRegion(4 * 16, 5 * 16, 16, 16);
        TextureRegion lifeRegion = lifeLoader.getTextureRegion(4* 16, 0, 16, 16);
        TextureRegion powerRegion = powerLoader.getTextureRegion(6* 16, 9* 16, 16, 16);
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



    public Collidable checkCollision(float potentialX, float potentialY, GameObject object) {
        Rectangle potentialBounds = new Rectangle(potentialX, potentialY, object.getBoundingBox().width, object.getBoundingBox().height);
        for (GameObject gameObject : gameObjects) {
            // Skip the collision check for the object itself
            if (gameObject == object) {
                continue;
            }
            if (gameObject instanceof Collidable collidable) {

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
            updateCameraPosition(); // Update the camera to follow the character
        }
        totalTime += delta;
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
        for (Iterator<GameObject> iterator = gameObjects.iterator(); iterator.hasNext();) {
            GameObject gameObject = iterator.next();
            if (gameObject instanceof Key key) {
                if (key.isCollected()) {
                    // Remove the collected key from the gameObjects list
                    iterator.remove();
                }
            }
        }
        if (character != null && getLives() <= 0) {

            goToExitScreen();
            return; // Stop further rendering and updating
        }
        batch.end();
        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        hudStage.draw();

    }
    public int calculateScore() {
        int score = BASE_SCORE;
        score += lives * SCORE_PER_LIFE;
        score -= totalTime * TIME_PENALTY;
        return Math.max(score, 0); // Ensure that the score does not go below 0
    }
    public Character getPlayer() {
        return character;
    }


    private void updateCameraPosition() {
        float lerp = 0.1f; // Adjust this value as needed for smoother camera movement
        if (character != null) {
            float targetX = character.getX() + 14 / 2;
            float targetY = character.getY() + 20 / 2;
            camera.position.x += (targetX - camera.position.x) * lerp;
            camera.position.y += (targetY - camera.position.y) * lerp;
        }

        // Adjust zoom level if necessary
        camera.zoom = 0.2f; // Adjust these values as needed
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
            camera.zoom = 0.4f;
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
        hudStage.dispose();
    }
    public int getMazeWidth() {
        return mazeWidth;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }

    public void setLives(int lives) {
        this.lives = lives;
        updateHUD(); // Update the HUD whenever lives change
    }

    public int getLives() {
        return lives;
    }
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
    private void initializeHUD() {
        hudStage = new Stage(new ScreenViewport());
        loadHudTextures();

        heartImages = new ArrayList<>();

        // Create heart images for the initial lives
        for (int i = 0; i < lives; i++) {
            addHeartImage();
        }

        // Initialize key image
        keyImage = new Image(keyIconTexture);
        float keySize = 50f; // Increased size
        keyImage.setSize(keySize, keySize);
        keyImage.setPosition(Gdx.graphics.getWidth() - keySize - 10, Gdx.graphics.getHeight() - keySize - 10);
        boolean keyCollected = false;
        keyImage.setVisible(keyCollected);
        hudStage.addActor(keyImage);
    }
    void addHeartImage() {
        Image heartImage = new Image(heartTexture);
        float heartSize = 50f; // Increased size
        heartImage.setSize(heartSize, heartSize);
        heartImages.add(heartImage);
        hudStage.addActor(heartImage);

        // Update positions of heart images
        for (int i = 0; i < heartImages.size(); i++) {
            Image img = heartImages.get(i);
            img.setPosition(10 + i * (heartSize + 10), Gdx.graphics.getHeight() - heartSize - 10);
        }
    }
    public void updateKeyVisibility(boolean hasKey) {
        if (keyImage != null) {
            keyImage.setVisible(hasKey);
        }
    }

   void updateHUD() {
        // Ensure no duplicate lives are added in HUD
        for (Image heartImage : heartImages) {
            hudStage.getActors().removeValue(heartImage, true);
        }
        heartImages.clear();

        for (int i = 0; i < lives; i++) {
            addHeartImage();
        }
    }
    public void goToExitScreen() {
        // You might need to stop the game music and dispose of other resources here
        int finalScore = calculateScore();
        Gdx.app.log("GameScreen", "Final Score: " + finalScore);
        if (gameMusic != null) {
            gameMusic.stop();
            gameMusic.dispose();
        }
        game.setScreen(new ExitScreen(game,victory));
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public MazeRunnerGame getGame() {
        return game;
    }
}

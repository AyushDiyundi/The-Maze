package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

public class MazeRunnerGame extends Game {

    private SpriteBatch spriteBatch;
    private Skin skin;
    private Animation<TextureRegion> characterDownAnimation;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private final Maps maps;
    private boolean paused = false;

    public MazeRunnerGame(NativeFileChooser fileChooser) {
        maps = new Maps(fileChooser, this);
    }
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));
        maps.configureMaps();
        loadCharacterAnimation();
        goToMenu();
    }

    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));
        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkFrames);
        walkSheet.dispose();
    }

    public void goToMenu() {
        if (gameScreen != null&& !paused) {
            gameScreen.hide(); // Stop game screen music
        }
        if (menuScreen == null) {
            menuScreen = new MenuScreen(this);
        }else {if (!paused) {
            menuScreen.playMenuMusic();
        }
        }
        setScreen(menuScreen);
        if (!paused) {
            disposeGameScreen();
        }
    }
    public void goToGame () {
        if (menuScreen != null&& !paused) {
            menuScreen.hide(); // Stop menu music
        }
            if (paused && gameScreen != null) {
                paused = false;
                setScreen(gameScreen);
            }
            else {
                int[][] defaultMazeData = maps.loadDefaultMazeData();
                if (defaultMazeData != null) {
                    startGameWithMaze(defaultMazeData);
                    gameScreen.playGameMusic();
                }
            }
    }
    public void startGameWithMaze(int[][] mazeData) {
        disposeMenuScreen();
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        gameScreen = new GameScreen(this, mazeData);
        setScreen(gameScreen);
    }

    public void loadMaps() {
        paused=false;
        maps.chooseMap();
    }
    @Override
    public void dispose() {
        if (screen != null) {
            screen.hide();
            screen.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }

    public void disposeGameScreen() {
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
    }

    public void disposeMenuScreen() {
        if (menuScreen != null) {
            menuScreen.dispose();
            menuScreen = null;
        }
    }
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }
}

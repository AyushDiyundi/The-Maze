package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion>  characterRightAnimation;
    private Animation<TextureRegion>characterLeftAnimation;
    private Sound hurt;
    private Sound unlock;
    private Sound buff;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private final Maps maps;
    private SoundSettings soundSettings;
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
        soundSettings=new SoundSettings();
        hurt= Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
        unlock= Gdx.audio.newSound(Gdx.files.internal("unlock.mp3"));
        buff = Gdx.audio.newSound(Gdx.files.internal("buff.mp3"));
        goToMenu();
    }
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));
        int frameWidth = 16;
        int frameHeight = 32;
        int framesPerAnimation = 4; // Number of frames per animation row

        TextureRegion[][] tmp = TextureRegion.split(walkSheet, frameWidth, frameHeight);

        // Extract only the first four frames from each row
        characterDownAnimation = extractAnimation(tmp, 0, framesPerAnimation);
        characterUpAnimation = extractAnimation(tmp, 2, framesPerAnimation);
        characterRightAnimation = extractAnimation(tmp, 1, framesPerAnimation);
        characterLeftAnimation = extractAnimation(tmp, 3, framesPerAnimation);
    }

    // Utility method to extract animation frames
    private Animation<TextureRegion> extractAnimation(TextureRegion[][] spriteSheet, int row, int frameCount) {
        TextureRegion[] animationFrames = new TextureRegion[frameCount];
        System.arraycopy(spriteSheet[row], 0, animationFrames, 0, frameCount);
        return new Animation<>(0.1f, animationFrames);
    }

    public void goToMenu() {
        if (gameScreen != null&& !paused) {
            gameScreen.hide(); // Stop game screen music
        }
        if (menuScreen == null) {
            menuScreen = new MenuScreen(this);
            menuScreen.getMenuMusic().setVolume(soundSettings.getMenuMusicVolume());
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
                 maps.selectMap(0);
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

    public void loadMaps(int n) {
        paused=false;
        maps.selectMap(n);
    }
    @Override
    public void dispose() {
        if (screen != null) {
            screen.hide();
            screen.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if(hurt !=null)
            hurt.dispose();
        if(buff!=null)
            buff.dispose();
        if(unlock !=null)
            unlock.dispose();
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        super.dispose();
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

    public void setCharacterDownAnimation(Animation<TextureRegion> characterDownAnimation) {
        this.characterDownAnimation = characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }


    public SoundSettings getSoundSettings() {
        return soundSettings;
    }
    public void render() {
        super.render(); // This is important for delegating the render call
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public Sound getHurt() {
        return hurt;
    }

    public Sound getUnlock() {
        return unlock;
    }

    public Sound getBuff() {
        return buff;
    }
}

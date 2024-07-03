package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The MenuScreen class is responsible for displaying the main menu and pause menu in the MazeRunnerGame.
 * It allows the user to navigate through options like starting the game, selecting levels, adjusting sound settings, and exiting the game.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private final MazeRunnerGame game;
    private final SpriteBatch batch;
    private Label welcomeLabel;
    private Label pausedLabel;
    private Label levelLabel;
    private Label soundSettingsLabel;
    private final boolean pauseMenuVisible;
    private final Texture backgroundTexture;
    private final Music menuMusic;
    /**
     * Constructor for the MenuScreen. Initializes the main components used in the menu screen,
     * including the stage, camera, and music.
     *
     * @param game The instance of MazeRunnerGame to which this menu screen belongs.
     */
    public MenuScreen(MazeRunnerGame game) {
        this.game = game;
        batch=new SpriteBatch();
        pauseMenuVisible = game.isPaused();
        OrthographicCamera camera = new OrthographicCamera();
        camera.zoom = 1.5f;
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());
        backgroundTexture = new Texture(Gdx.files.internal("MenuBG.png"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu2.mp3"));
        setupStages();
    }
    /**
     * Plays the menu music in a loop. The volume is adjusted based on the current settings.
     */
    public void playMenuMusic() {
        if (menuMusic != null) {
            menuMusic.setLooping(true);
            menuMusic.setVolume(game.getSoundSettings().getMenuMusicVolume());
            menuMusic.play();
        }
    }
    /**
     * Sets up the stages for the main menu and pause menu based on the game's state.
     */
    private void setupStages() {
        mainMenu();
        pauseMenu();
    }


    private Label createLabel(String text,String style, boolean visible) {
        Label label = new Label(text, game.getSkin(),style);
        label.setVisible(visible);
        return label;
    }

    private TextButton createButton(String text, Runnable action, boolean visible) {
        TextButton button = new TextButton(text, game.getSkin());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        button.setVisible(visible);
        button.pad(10);
        return button;
    }
    /**
     * Sets up the main menu with buttons for starting the game, changing levels,
     * adjusting settings, and exiting the game.
     */
    private void mainMenu() {
        Table table = createMenuTable();
        welcomeLabel = createLabel("The Maze  ","title" ,!pauseMenuVisible);
        welcomeLabel.setAlignment(Align.center);
        addMenuItems(table, welcomeLabel,
                createButton("Play", game::goToGame, !pauseMenuVisible),
                createButton("Maps",  this::levelMenu, !pauseMenuVisible),
                createButton("Sound Setting", this::soundMenu, !pauseMenuVisible),
                createButton("Exit", Gdx.app::exit, !pauseMenuVisible)
        );
    }

    /**
     * Sets up the pause menu which is displayed when the game is paused.
     */
    private void pauseMenu() {
        Table table = createMenuTable();
        pausedLabel = createLabel(" Game Paused ", "title",pauseMenuVisible);
        pausedLabel.setAlignment(Align.center);
        playMenuMusic();
        addMenuItems(table, pausedLabel,
                createButton("Resume", game::goToGame, pauseMenuVisible),
                createButton("Another Map", this::levelMenu, pauseMenuVisible),
                createButton("Sound Setting", this::soundMenu, pauseMenuVisible),
                createButton("Quit to Main Menu",() -> {
                    game.disposeMenuScreen();
                    game.setPaused(false);
                    game.goToMenu();
                },pauseMenuVisible));
    }
    private void levelMenu() {
        game.disposeMenuScreen();
        Table table = createMenuTable();
        levelLabel = createLabel("Select Level", "title", true);
        levelLabel.setAlignment(Align.center);
        playMenuMusic();
        table.add(levelLabel).center().padBottom(10); // Adjust padding as needed
        table.row();
        for (int i = 1; i <= 5; i++) {
            int lvl = i;
            addMenuItems(table, createButton("Level " +lvl, () -> game.loadMaps(lvl), true));
        }

        addMenuItems(table, createButton("Custom Level", () -> game.loadMaps(6), true));
        addMenuItems(table, createButton("Back", () -> {
            game.disposeMenuScreen();
            game.goToMenu();
        }, true));
    }
    private void soundMenu() {
        game.disposeMenuScreen();
        Table table = createMenuTable();
        soundSettingsLabel = createLabel("Sound Settings", "title", true);
        soundSettingsLabel.setAlignment(Align.center);
        addMenuItems(table, soundSettingsLabel);
        playMenuMusic();

        // Create and add sliders for menu and game music volume
        Slider menuMusicSlider = createSlider(game.getSoundSettings().getMenuMusicVolume());
        Slider gameMusicSlider = createSlider(game.getSoundSettings().getGameMusicVolume());

        menuMusicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = menuMusicSlider.getValue();
                game.getSoundSettings().setMenuMusicVolume(value);
                menuMusic.setVolume(value); // Assuming menuMusic is accessible here
            }
        });

        gameMusicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = gameMusicSlider.getValue();
                game.getSoundSettings().setGameMusicVolume(value);
            }
        });

        addMenuItems(table, new Label("Menu Music", game.getSkin()), menuMusicSlider);
        addMenuItems(table, new Label("Game Music", game.getSkin()), gameMusicSlider);
        addMenuItems(table, createButton("Save & Return", () -> {
            game.disposeMenuScreen();
            game.goToMenu();
        }, true));
    }

    private Slider createSlider(float initialValue) {
        Slider slider = new Slider(0f, 1f, 0.2f, false, game.getSkin());
        slider.setValue(initialValue);
        return slider;
    }

    private Table createMenuTable() {
        Table table = new Table();
        table.setFillParent(true);
        table.top();
        stage.addActor(table);
        return table;
    }

    private void addMenuItems(Table table, Actor... items) {
        for (Actor item : items) {
            table.add(item).width(500).pad(10).row();
        }
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        if(!pauseMenuVisible){
            batch.begin();
            batch.draw(backgroundTexture,0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();}
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage);
        if (!game.isPaused()) {
            playMenuMusic();
        }
        resume();

    }
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.dispose();
        }
        pause();
    }
    public Music getMenuMusic() {
        return menuMusic;
    }

}
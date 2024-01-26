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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private final MazeRunnerGame game;
    private SpriteBatch batch;
    private Label welcomeLabel, pausedLabel, gameOver, victory,levelLabel;
    private boolean pauseMenuVisible;
    private Texture backgroundTexture;
    private Music menuMusic;

    public MenuScreen(MazeRunnerGame game) {
        this.game = game;
        batch=new SpriteBatch();
        pauseMenuVisible = game.isPaused();
        OrthographicCamera camera = new OrthographicCamera();
        camera.zoom = 1.5f;
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());
        backgroundTexture = new Texture(Gdx.files.internal("MenuBG.png"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
        setupStages();
        setupLabels();
    }
    public void playMenuMusic() {
        if (menuMusic != null) {
            menuMusic.setLooping(true);
            menuMusic.play();
        }
    }
    private void setupStages() {
        mainMenu();
        pauseMenu();
    }

    private void setupLabels() {
        victory = createLabel("Congratulations! You Won!","title",false);
        gameOver = createLabel("Game Over. Better luck next time!","title", false);
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

    private void mainMenu() {
        Table table = createMenuTable();
        welcomeLabel = createLabel("Welcome to BlazeMaze  ","title" ,!pauseMenuVisible);
        welcomeLabel.setAlignment(Align.center);
        addMenuItems(table, welcomeLabel,
                createButton("Play", game::goToGame, !pauseMenuVisible),
                createButton("Maps",  game::loadMaps, !pauseMenuVisible),
                createButton("Exit", Gdx.app::exit, !pauseMenuVisible)
        );
    }

    private void pauseMenu() {
        Table table = createMenuTable();
        pausedLabel = createLabel(" Game Paused ", "title",pauseMenuVisible);
        pausedLabel.setAlignment(Align.center);
        addMenuItems(table, pausedLabel,
                createButton("Resume", game::goToGame, pauseMenuVisible),
                createButton("Another Map", game::loadMaps, pauseMenuVisible),
                createButton("Quit to Main Menu",() -> {
                    game.disposeMenuScreen();
                    game.setPaused(false);
                    game.goToMenu();
                },pauseMenuVisible));
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
}

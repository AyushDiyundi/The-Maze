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
public class ExitScreen implements Screen {

    private final Stage stage;
    private final MazeRunnerGame game;
    private SpriteBatch batch;
    private Label messageLabel,scoreLabel;
    private Texture backgroundTexture;
    private Music exitMusic;
    private boolean isVictory;


    public ExitScreen(MazeRunnerGame game, boolean isVictory) {
        this.game = game;
        this.isVictory = isVictory;
        batch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, batch);
        loadResources();
        setupUI();
    }

    private void loadResources() {
        if (isVictory) {
            backgroundTexture = new Texture(Gdx.files.internal("victory.png"));
            exitMusic = Gdx.audio.newMusic(Gdx.files.internal("victorymain.mp3"));
        } else {
            backgroundTexture = new Texture(Gdx.files.internal("defeat.png"));
            exitMusic = Gdx.audio.newMusic(Gdx.files.internal("gameover2.mp3"));
        }
    }
    private void setupUI() {
        messageLabel = new Label(isVictory ? "Victory!" : "Game Over!!", game.getSkin(), "title");
        messageLabel.setFontScale(1.7f);
        messageLabel.setAlignment(Align.center);
        // Setup the score message label
        String scoreMessage = isVictory ? "Your Score: " + game.getGameScreen().calculateScore(): "Better Luck Next Time!!";
        scoreLabel = new Label(scoreMessage, game.getSkin(),"title");
        scoreLabel.setFontScale(.7f);
        scoreLabel.setAlignment(Align.center);



        // Table for the message label at the top
        Table topTable = new Table();
        topTable.top();
        topTable.setFillParent(true);
        topTable.add(messageLabel).center().padTop(20);
        stage.addActor(topTable);
        // Add the score label to the top table
        topTable.row(); // Move to the next row in the table
        topTable.add(scoreLabel).center().padTop(10);

        // Table for buttons at the bottom
        Table bottomTable = new Table();
        bottomTable.bottom();
        bottomTable.setFillParent(true);

// Create buttons
        TextButton mainMenuButton = createButton("Main Menu", () -> {
            game.goToMenu();
        });
        TextButton quitButton = createButton("Quit", () -> {
            Gdx.app.exit();
        });

// Add buttons to the bottom table with space between them
        bottomTable.add(mainMenuButton).padLeft(10).left().padBottom(10).expandX();
        bottomTable.add().expandX(); // Empty cell as a spacer
        bottomTable.add(quitButton).padRight(10).right().padBottom(10).expandX();
        stage.addActor(bottomTable);
    }

    private TextButton createButton(String text, Runnable action) {
        TextButton button = new TextButton(text, game.getSkin());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return button;
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        exitMusic.play();
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        exitMusic.dispose();
    }
}
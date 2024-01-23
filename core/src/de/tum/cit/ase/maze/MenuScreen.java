package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private TextButton loadMap, resumeGame, exit, returnMenu, newGame, goToGameButton;
    private TextButton level1, level2, level3, level4, level5, custom;
    private Label welcomeLabel, pausedLabel, gameOver, victory,levelLabel;
    private boolean pauseMenuVisible,visible;

    // Setter methods for injecting UI components from external classes
    public void setLoadMap(TextButton loadMap) {
        this.loadMap = loadMap;
    }

    public void setExit(TextButton exit) {
        this.exit = exit;
    }

    public void setResumeGame(TextButton resumeGame) {
        this.resumeGame = resumeGame;
    }

    public void setPausedLabel(Label pausedLabel) {
        this.pausedLabel = pausedLabel;
    }

    public TextButton getReturnMenu() {
        return returnMenu;
    }

    public void setReturnMenu(TextButton returnMenu) {
        this.returnMenu = returnMenu;
    }

    public TextButton getNewGame() {
        return newGame;
    }

    public void setNewGame(TextButton newGame) {
        this.newGame = newGame;
    }

    public Label getVictory() {
        return victory;
    }

    public void setVictory(Label victory) {
        this.victory = victory;
    }

    public Label getGameOver() {
        return gameOver;
    }

    public void setGameOver(Label gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Constructor for MenuScreen.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        this.game = game;

        // Set up the camera and viewport
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;
        Viewport viewport = new ScreenViewport(camera);

        // Create the stage for UI elements
        stage = new Stage(viewport, game.getSpriteBatch());
         pauseMenuVisible=game.getPaused();
         visible=false;
        // Set up the main menu and pause menu
        mainMenu();
        pauseMenu();

        // Create labels for victory and game over messages
        victory = new Label("Congratulations! You Won!", game.getSkin(), "default");
        gameOver = new Label("Game Over. Better luck next time!", game.getSkin(), "default");

        // Set labels to be initially invisible
        victory.setVisible(false);
        gameOver.setVisible(false);
    }

    // Utility method to create a Label with a specific style and visibility
    private Label createLabel(String text, String style, boolean viz) {
        Label label = new Label(text, game.getSkin(), style);
        label.setVisible(viz);
        return label;
    }

    // Utility method to create a TextButton with a specific label and visibility, and attach a listener
    private TextButton createButton(String text, Runnable action,boolean viz) {
        TextButton button = new TextButton(text, game.getSkin());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        button.setVisible(viz);
        button.pad(10).row();
        return button;
    }

    // Utility method to add multiple elements to a table with a specified width and padding
    private void addElementsToTable(Table table, Actor... elements) {
        for (Actor element : elements) {
            table.add(element).width(500).pad(10).row();
        }
    }

    // Action to show the levels menu
    private void showLevelsMenu() {
        game.disposeMenuScreen();
        levelsMenu(true);
    }

    /**
     * Sets up the main menu UI components.
     */
    public void mainMenu() {
        // Create a table for layout
        Table tableM = new Table();
        tableM.setFillParent(true);
        stage.addActor(tableM);

        // Add a welcome label
        welcomeLabel = createLabel("Welcome to BlazeMaze  ", "title",!pauseMenuVisible);
        tableM.top();
        tableM.add(welcomeLabel).padBottom(80).row();
        welcomeLabel.setAlignment(Align.center);

        // Add a button to start the game
        goToGameButton = createButton("Play", this::goToGame,!pauseMenuVisible);

        // Create buttons for additional menu options
        loadMap = createButton("Levels", this::showLevelsMenu,!pauseMenuVisible);
        exit = createButton("Exit", Gdx.app::exit,!pauseMenuVisible);

        // Add buttons to main menu
        addElementsToTable(tableM, welcomeLabel, goToGameButton, loadMap, exit);
    }

    /**
     * Sets up the pause menu UI components.
     */
    public void pauseMenu() {
        Table tableP = new Table();
        tableP.setFillParent(true);
        stage.addActor(tableP);
        // Create and add the paused label
        // Initialize the pause menu visibility
        pausedLabel = createLabel(" Game Paused ", "title",pauseMenuVisible);
        tableP.top();
        tableP.add(pausedLabel).padBottom(80).row();
        pausedLabel.setAlignment(Align.center);
        // Add buttons to the paused menu
        resumeGame = createButton("Resume", game::resume,pauseMenuVisible);
        newGame = createButton("New Game", this::goToGame,pauseMenuVisible);
        loadMap = createButton("Load Map", this::showLevelsMenu,pauseMenuVisible);
        returnMenu = createButton("Quit to Main Menu",() -> {
            game.disposeMenuScreen();
            game.setPaused(false);
            game.goToMenu();
        },pauseMenuVisible);

        // Add buttons to pause menu
        addElementsToTable(tableP, pausedLabel, resumeGame, newGame, loadMap, returnMenu);
    }

    /**
     * Sets up the levels menu UI components.
     *
     * @param visible Whether the menu should be visible.
     */
    public void levelsMenu(boolean visible) {
        this.visible = visible;
        Table tableL = new Table();
        tableL.setFillParent(true);
        stage.addActor(tableL);
        levelLabel = createLabel("Choose Your Map", "title",visible);
        tableL.top();
        tableL.add(levelLabel).padBottom(80).row();
        levelLabel.setAlignment(Align.center);

        level1 = createButton("Level 1", () -> game.getGameScreen().loadMap("level1"),visible);
        level2 = createButton("Level 2", () -> game.getGameScreen().loadMap("level2"),visible);
        level3 = createButton("Level 3", () -> game.getGameScreen().loadMap("level3"),visible);
        level4 = createButton("Level 4", () -> game.getGameScreen().loadMap("level4"),visible);
        level5 = createButton("Level 5", () -> game.getGameScreen().loadMap("level5"),visible);
        custom = createButton("Custom", this::loadCustomMap,visible);
        returnMenu = createButton("Back", this::goBackToMainMenu,visible);

        // Add buttons to levels menu
        addElementsToTable(tableL, level1, level2, level3, level4, level5, custom, returnMenu);
    }

    // Placeholder for loading a custom map (you can implement the logic)
    private void loadCustomMap() {
        // Implement logic for loading a custom map
    }

    // Action to go back to the main menu from levels menu
    private void goBackToMainMenu() {
     game.disposeMenuScreen();
     if(pauseMenuVisible)
         game.goToMenu();
     else
     {
         game.setPaused(false);
         game.goToMenu();
     }
    }

    // Action to start the game
    private void goToGame() {
        game.goToGame();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
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
        resume();
    }

    // Other lifecycle methods (pause, resume, hide) are kept empty
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        pause();
    }
}

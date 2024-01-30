package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class VictoryScreen implements Screen {

    private MazeRunnerGame game;
    private Texture victoryTexture;
    private SpriteBatch batch;

    public VictoryScreen(MazeRunnerGame game) {
        this.game = game;
        batch = new SpriteBatch();
        victoryTexture = new Texture(Gdx.files.internal("victory_screen.png")); // Place a victory image in your assets
    }

    @Override
    public void show() {
        // This method will be called when this screen becomes the current screen.
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        // Draw your victory texture
        batch.draw(victoryTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Adjust any sizing and layout here if necessary
    }

    @Override
    public void pause() {
        // Implement pause logic if necessary
    }

    @Override
    public void resume() {
        // Implement resume logic if necessary
    }

    @Override
    public void hide() {
        // This method will be called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Dispose resources when not needed
        batch.dispose();
        victoryTexture.dispose();
    }
}
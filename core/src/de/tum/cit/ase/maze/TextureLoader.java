package de.tum.cit.ase.maze;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
public class TextureLoader {
    private Texture tilesetTexture;

    public TextureLoader(FileHandle path) {
        tilesetTexture = new Texture(path);
    }

    public TextureRegion getTextureRegion(int x, int y, int width, int height) {
        return new TextureRegion(tilesetTexture, x, y, width, height);
    }

    public void dispose() {
        tilesetTexture.dispose();
    }
}
package de.tum.cit.ase.maze;

public class SoundSettings {
    private float menuMusicVolume = 1.0f;
    private float gameMusicVolume = 1.0f;

    public float getMenuMusicVolume() {
        return menuMusicVolume;
    }

    public void setMenuMusicVolume(float volume) {
        this.menuMusicVolume = volume;
    }

    public float getGameMusicVolume() {
        return gameMusicVolume;
    }

    public void setGameMusicVolume(float volume) {
        this.gameMusicVolume = volume;
    }
}
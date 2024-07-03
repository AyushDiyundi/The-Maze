package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Maps {

    private final NativeFileChooser fileChooser;
    private NativeFileChooserConfiguration config;
    private final MazeRunnerGame game;

    public Maps(NativeFileChooser fileChooser, MazeRunnerGame game) {
        this.game = game;
        this.fileChooser = fileChooser;
    }

    public void configureMaps() {
        config = new NativeFileChooserConfiguration();
        config.directory = Gdx.files.internal("assets/maps");
        config.nameFilter = (dir, name) -> name.endsWith("properties");
        config.title = "Choose map file";
    }
    public void selectMap(int choice) {
        switch (choice) {
            case 1:
                handleFileChosen(Gdx.files.internal("maps/level-1.properties"));
                break;
            case 2:
                handleFileChosen(Gdx.files.internal("maps/level-2.properties"));
                break;
            case 3:
                handleFileChosen(Gdx.files.internal("maps/level-3.properties"));
                break;
            case 4:
                handleFileChosen(Gdx.files.internal("maps/level-4.properties"));
                break;
            case 5:
                handleFileChosen(Gdx.files.internal("maps/level-5.properties"));
                break;
            case 6:
                externalMap();
                break;
            default:
                handleFileChosen(Gdx.files.internal("maps/level-1.properties"));
        }
    }

    public void externalMap()
    {
        if (fileChooser == null) {
            System.out.println("Map chooser not configured");
            return;
        }

        fileChooser.chooseFile(config, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                handleFileChosen(file);
            }

            @Override
            public void onCancellation() {
                System.out.println("File not chosen :(");
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void handleFileChosen(FileHandle file) {
        System.out.println("File chosen :)");
        Properties data = new Properties();
        try {
            data.load(file.reader());
            int[][] mazeData = loadMazeData(data);
            game.startGameWithMaze(mazeData);
        } catch (IOException e) {
            System.out.println("Failed reading data: " + e);
        }
    }
    private int[][] loadMazeData(Properties data) {
        int maxX = data.stringPropertyNames().stream()
                .mapToInt(coord -> Integer.parseInt(coord.split(",")[0]))
                .max()
                .orElse(0);

        int maxY = data.stringPropertyNames().stream()
                .mapToInt(coord -> Integer.parseInt(coord.split(",")[1]))
                .max()
                .orElse(0);

        // Initialize the maze with default values (e.g., 0)
        int[][] mazeData = new int[maxX + 1][maxY + 1];
        List<String> availableIndices = new ArrayList<>();
        for (int i = 0; i <= maxX; i++) {
            for (int j = 0; j <= maxY; j++) {
                mazeData[i][j] = 9; // default value
                if (!data.containsKey(i + "," + j)) {
                    availableIndices.add(i + "," + j);
                }
            }
        }

        // Update the maze data only for coordinates present in the Properties object
        data.forEach((key, value) -> {
            String[] coordinates = key.toString().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int intValue = Integer.parseInt(value.toString());
            mazeData[x][y] = intValue;
        });
        int totalCells = (maxX + 1) * (maxY + 1);
        int Total = (int) Math.sqrt(totalCells)/5;
        Random rand = new Random();

        for (int i = 0; i < Total; i++) {
            if(i%2==0){   if (availableIndices.isEmpty()) {
                break;
            }
                String[] lives = availableIndices.remove(rand.nextInt(availableIndices.size())).split(",");
                mazeData[Integer.parseInt(lives[0])][Integer.parseInt(lives[1])] = 6;
            }
            else {

                if (!availableIndices.isEmpty()) {
                    String[] Flash = availableIndices.remove(rand.nextInt(availableIndices.size())).split(",");
                    mazeData[Integer.parseInt(Flash[0])][Integer.parseInt(Flash[1])] = 7;
                }
            }
        }
        return mazeData;
    }
}


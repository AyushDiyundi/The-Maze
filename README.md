# Maze Runner Game

## Introduction
Maze Runner is a 2D puzzle and adventure game developed using the LibGDX framework. Players navigate through mazes, avoiding obstacles and enemies to find the key and unlock the exit.

## Code Structure
The project is organized into the `de.tum.cit.ase.maze` package with several key classes:

- `MenuScreen`: Manages the main menu, allowing players to start the game, choose levels, adjust sound settings, or exit the game.
- `Character`: Represents the player's character, handling movement, collision, and interaction with game objects.
- `Enemy`: Represents enemies within the maze, which the player must avoid.
- `Life`: Represents a collectible item that increases the player's life count.
- `Power`: Represents a power-up that can temporarily alter the player's abilities.
- `GameObject`: An abstract class representing any object within the game that can be interacted with.

## Running the Game
To run Maze Runner:

1. Ensure you have Java and the LibGDX framework set up on your development environment.
2. Clone the repository to your local machine.
3. Open the project in an IDE like IntelliJ IDEA or Eclipse.
4. Run the main class that initializes the LibGDX application listener.

## Game Mechanics
- **Navigation**: Use arrow keys to move the character through the maze.
- **Obstacles**: Avoid traps and enemies, which can decrease your life count.
- **Collectibles**: Collect lives to increase your chances and keys to unlock exits.
- **Power-Ups**: Gain temporary abilities that can help you navigate the maze more easily.

## Classes and Methods

### MenuScreen
Handles the display and functionality of the main menu, including buttons for playing the game, choosing levels, changing sound settings, or exiting.

- `playMenuMusic()`: Plays background music for the menu.
- `createLabel()`: Creates a UI label.
- `createButton()`: Creates a UI button with an assigned action.
- `mainMenu()`: Sets up the main menu UI.
- `pauseMenu()`: Sets up the pause menu UI.

### Character
Represents the player's character within the game.

- `move()`: Handles the logic for moving the character.
- `handleCollision()`: Defines interaction with other collidable objects.

### Enemy
Represents the enemies within the maze.

- `update()`: Updates the enemy's behavior, such as following the player.
- `handleCollision()`: Defines what happens when the enemy collides with the player.

### Life
Represents an extra life collectible in the game.

- `isCollected()`: Checks if the life has been collected by the player.

### Power
Represents a power-up in the game.

- No specific methods documented, presumed to alter player abilities temporarily.

## Additional Notes
- The game is currently in development. Future updates may introduce new mechanics and levels.
- For debugging purposes, the `debugMode` flag can be set in the `Enemy` class to print collision information to the console.

## Contributions
We welcome contributions to the Maze Runner game. Please read `CONTRIBUTING.md` for guidelines on how to submit issues and pull requests.

## License
Maze Runner is open-sourced under the MIT license. See `LICENSE` for more information.

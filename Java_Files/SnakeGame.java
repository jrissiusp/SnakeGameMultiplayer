package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Texture;

import static java.lang.Thread.sleep;
/**
 * Multiplayer Snake Game
 *
 * This application was developed as a final work for the Object-Oriented Programming Discipline (SCC0204)
 *
 * This game is basically an enhanced version of the classic Snake Game, transforming it into Snake Battle.
 * Since the game is local multiplayer, the main objective is to get the other snake to collide.
 * In the event of a snake colliding with the body of another, the other wins.
 * If one snake collides with the wall, the other wins.
 * In case of collision of both snakes (at the same time with the wall or head to head) the size of the snake is used as a tiebreaker.
 * There is no collision between snakes and their own bodies.
 *
 * The game has screens: Start screen, skin choice screen (player 1 and player 2), the battlefield, victory screen (player 1 and player2)
 * There are 5 possible "skins" for the snake, which must be chosen before every game.
 * The music used is and the sound effects
 * 4 new types of food have been added in addition to the regular apple:
 * Silver apple - Guarantees 3 points and 3 additional size spaces
 * Golden apple - Guarantees 5 points and 5 additional size spaces
 * Ice flake - Guarantees 1 points and 1 additional size space in addition to leaving the other snake with half the movement
 * Radius - Guarantees 1 points and 1 additional space in addition to leaving the other snake with double the movement
 * Ice flake and lightning effects are cumulative up to 3 times and last for 11 moves (time can be extended)
 *
 * @author Victor Hugo Mendonça Melo - 14610386
 * @author Matheus Rodrigues Ferreira - 14762149
 * @author Joao Rissi Magnani - 14582823
 *
 * @version 2024.06.15
 */

/**
 * Main class for the Snake Game.
 * It extends ApplicationAdapter which provides basic game loop methods.
 */
public class SnakeGame extends ApplicationAdapter {

    ShapeRenderer shapeRenderer; // For rendering shapes like the snake body
    OrthographicCamera camera; // Camera for viewing the game world
    Stage stage; // Stage for UI elements
    SpriteBatch batch; // For rendering textures

    // Constants for board dimensions and square size
    public final static int WIDTH = 900;
    public final static int HEIGHT = 900;
    public final static int SQUARE_SIZE = 30;

    Texture startScreen; // Texture for the start screen
    Texture chooseScreen1; // Texture for player1 choose screen
    Texture chooseScreen2; // Texture for player2 choose screen
    Texture winScreen1; // Texture if player1 wins
    Texture winScreen2; // Texture if player2 wins

    Snake snake1; // First snake
    Snake snake2; // Second snake
    Food food1; // First food
    Food food2; // Second food

    Sound freezing, shock, eating; // Food related sound effects
    Sound choosing; // Sound effect related to buttons
    Music backgroundMusic; // Background music
    Music winning; // Music for when the game ends and the winner is determined

    // -1 = game hasn't started yet / 0 = snakes are fighting / 1 = The game is over
    int gameEndS1 = -1; // State of the game for snake 1
    int gameEndS2 = -1; // State of the game for snake 2

    // Textures for the "sample" snakes - skin selection screen
    Texture headGreen, headBlue, headRed, headYellow, headWhite;
    Texture bodyGreen, bodyBlue, bodyRed, bodyYellow, bodyWhite;
    Texture tailGreen, tailBlue, tailRed, tailYellow, tailWhite;

    int choosed1; // Chosen skin for snake 1
    int choosed2; // Chosen skin for snake 2

    // 0 = menu screen / 1 = player1 skin choice screen /  2 = player2 skin choice screen
    int menuState = 0; // Current menu state

    BitmapFont font;

    /**
     * Called when the application is created and repeated.
     * Initializes game objects and loads textures.
     */
    @Override
    public void create() {
        // LibGDX settings
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(new ScreenViewport());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        batch = new SpriteBatch();

        // Initializing sound effects
        freezing = Gdx.audio.newSound(Gdx.files.internal("freezesound.mp3"));
        shock = Gdx.audio.newSound(Gdx.files.internal("shockeffect.mp3"));
        eating = Gdx.audio.newSound(Gdx.files.internal("eatsoundeffect.wav"));
        choosing = Gdx.audio.newSound(Gdx.files.internal("choosesoundeffect.mp3"));

        // Initializing music
        winning = Gdx.audio.newMusic(Gdx.files.internal("winsoundeffect.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundmusic.wav"));

        // start the playback of the background music immediately
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Loading skins for the mannequins of choice
        headGreen = new Texture(Gdx.files.internal("headup.png"));
        headBlue = new Texture(Gdx.files.internal("headup_texture_blue.png"));
        headRed = new Texture(Gdx.files.internal("headup_texture_red.png"));
        headYellow = new Texture(Gdx.files.internal("headup_texture_yellow.png"));
        headWhite = new Texture(Gdx.files.internal("headup2.png"));

        bodyGreen = new Texture(Gdx.files.internal("bodyvertical.png"));
        bodyBlue = new Texture(Gdx.files.internal("bodyvertical_texture_blue.png"));
        bodyRed = new Texture(Gdx.files.internal("bodyvertical_texture_red.png"));
        bodyYellow = new Texture(Gdx.files.internal("bodyvertical_texture_yellow.png"));
        bodyWhite = new Texture(Gdx.files.internal("bodyvertical2.png"));

        tailGreen = new Texture(Gdx.files.internal("tailup.png"));
        tailBlue = new Texture(Gdx.files.internal("tailup_texture_blue.png"));
        tailRed = new Texture(Gdx.files.internal("tailup_texture_red.png"));
        tailYellow = new Texture(Gdx.files.internal("tailup_texture_yellow.png"));
        tailWhite = new Texture(Gdx.files.internal("tailup2.png"));

        // Load screens
        startScreen = new Texture(Gdx.files.internal("startscreen.png"));
        chooseScreen1 = new Texture(Gdx.files.internal("choosescreen1.png"));
        chooseScreen2 = new Texture(Gdx.files.internal("chosescreen2.png"));
        winScreen1 = new Texture(Gdx.files.internal("winscreenp1.png"));
        winScreen2 = new Texture(Gdx.files.internal("winscreenp2.png"));

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);

    }

    /**
     * Update method to handle game logic updates.
     * This method is called periodically to update the state of the game.
     */
    public void update() {
        int delay = 9; // Delay between updates

        // Check if snakes have eaten food
        checkEaten(snake1, snake2);
        checkEaten(snake2, snake1);

        // Calculate new direction for each snake
        snake1.dirCalc();
        snake2.dirCalc();

        // Update snake positions based on their mod value
        // Ensuring that if the snake has any speed effect, it is applied
        if (snake1.uptade % snake1.mod == 0) {
            snake1.updatePlayer();
        }
        if (snake2.uptade % snake2.mod == 0) {
            snake2.updatePlayer();
        }

        // Reset mod value if duration of any effect is zero
        if (snake1.duration == 0) {
            snake1.mod = 8;
        }
        if (snake2.duration == 0) {
            snake2.mod = 8;
        }

        // Increment update counters
        snake1.uptade = (snake1.uptade + 1) % 64;
        snake2.uptade = (snake1.uptade + 1) % 64;

        // Decrement duration if greater than zero
        if (snake1.duration > 0) {
            snake1.duration--;
        }
        if (snake2.duration > 0) {
            snake2.duration--;
        }

        // Check if game has ended for either snake
        gameEndS1 = snake1.checkGameEnd(snake2.body);
        gameEndS2 = snake2.checkGameEnd(snake1.body);

        stagger(delay); // Introduce a delay
    }

    /**
     * Check if a snake has eaten the food and handle the logic.
     *
     * @param snake The snake to check.
     * @param otherSnake The other snake in the game.
     */
    private void checkEaten(Snake snake, Snake otherSnake) {
        int type = 0;
        int foodEaten = snake.checkCollideWithFood(food1, food2);

        if (foodEaten == 1) {
            type = food1.eaten(snake, otherSnake);
            food1.renew(snake.body, otherSnake.body);
        }
        if (foodEaten == 2) {
            type = food2.eaten(snake, otherSnake);
            food2.renew(snake.body, otherSnake.body);
        }

        // Food sound effect
        switch (type){
            case 1:
            case 4:
            case 5:
                eating.play();
                break;
            case 2:
                freezing.play();
                break;
            case 3:
                shock.play();
                break;
            case 0:
                break;
        }
    }

    /**
     * Get the number key pressed.
     *
     * @return The number corresponding to the key pressed, or -1 if none.
     */
    private int getPressedNumber() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) return 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) return 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) return 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) return 4;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) return 5;
        return -1;
    }

    /**
     * Draw the start screen.
     */
    private void drawStart() {
        batch.begin();
        batch.draw(startScreen, -25, 238);
        batch.end();
    }

    /**
     * Draw the choose screen.
     *
     * @param chooseScreen The texture to be drawn for the choose screen.
     */
    private void drawChoose(Texture chooseScreen) {
        batch.begin();

        batch.draw(chooseScreen, -25, 425);

        // Draw snake heads
        batch.draw(headGreen, 265, 445);
        batch.draw(headWhite, 350, 445);
        batch.draw(headBlue, 435, 445);
        batch.draw(headRed, 520, 445);
        batch.draw(headYellow, 605, 445);

        // Draw snake bodies
        batch.draw(bodyGreen, 265, 415);
        batch.draw(bodyWhite, 350, 415);
        batch.draw(bodyBlue, 435, 415);
        batch.draw(bodyRed, 520, 415);
        batch.draw(bodyYellow, 605, 415);

        // Draw snake tails
        batch.draw(tailGreen, 265, 385);
        batch.draw(tailWhite, 350, 385);
        batch.draw(tailBlue, 435, 385);
        batch.draw(tailRed, 520, 385);
        batch.draw(tailYellow, 605, 385);

        batch.end();
    }

    /**
     * Draw the end screen.
     *
     * @param winner The player number who won.
     */
    private void drawEnd(int winner) {
        batch.begin();
        if (winner == 1) {
            batch.draw(winScreen1, -25, 238);
        } else {
            batch.draw(winScreen2, -25, 238);
        }

        // Print the final score for each snake
        font.setColor(new Color(0.45f, 1f, 0.3f, 1.0f));
        font.draw(batch, "Player 1 Score: " + snake1.size, 25, 80);
        font.draw(batch, "Player 2 Score: " + snake2.size, 25, 40);
        batch.end();
    }

    /**
     * Delay the game for a certain amount of time.
     *
     * @param delay The amount of time to delay in milliseconds.
     */
    private void stagger(int delay) {
        try {
            sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main render loop of the game.
     * Called periodically to render the game and handle user input.
     */
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;

        // Handle game states
        if (gameEndS1 == -1 && gameEndS2 == -1) {
            if (menuState == 0) {
                drawStart();
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    choosing.play();
                    menuState = 1;
                }
            } else if (menuState == 1) {
                drawChoose(chooseScreen1);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                    choosed1 = getPressedNumber();
                    choosing.play();
                    menuState = 2;
                }
            } else if (menuState == 2) {
                drawChoose(chooseScreen2);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                    choosed2 = getPressedNumber();
                    choosing.play();
                    menuState = 0;
                    gameEndS1 = 0;
                    gameEndS2 = 0;
                }

                // Initialize snakes and food
                snake1 = new Snake(SQUARE_SIZE, SQUARE_SIZE, Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D, SQUARE_SIZE, choosed1);
                snake2 = new Snake(WIDTH - 2 * SQUARE_SIZE, HEIGHT - 2 * SQUARE_SIZE, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, -SQUARE_SIZE, choosed2);

                food1 = new Food(snake1.body, snake2.body);
                food2 = new Food(snake1.body, snake2.body);
            }

        } else if (gameEndS1 == 0 && gameEndS2 == 0) {
            update();
            batch.begin();
            batch.draw(food1.foodImage, food1.x, food1.y);
            batch.draw(food2.foodImage, food2.x, food2.y);
            snake1.draw(batch);
            snake2.draw(batch);
            batch.end();

        } else if (gameEndS1 == 1 || gameEndS2 == 1) {
            backgroundMusic.pause();
            winning.play();
            if (gameEndS1 == gameEndS2) { // Both crashed
                if (snake1.size > snake2.size) {
                    drawEnd(1);
                } else {
                    drawEnd(2);
                }
            }

            else if (gameEndS1 == 1) {
                drawEnd(2);
            } else {
                drawEnd(1);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                choosing.play();
                winning.stop();
                gameEndS1 = -1;
                gameEndS2 = -1;
                create();
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
                choosing.play();
                winning.stop();
                System.exit(0);
            }
        }

        stage.act();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.end();
        stage.draw();
        stage.clear();
    }

    /**
     * Dispose of assets when the application is destroyed.
     */
    @Override
    public void dispose() {
        winning.dispose();
        freezing.dispose();
        backgroundMusic.dispose();
        shock.dispose();
        eating.dispose();
        food1.foodImage.dispose();
        food2.foodImage.dispose();
        shapeRenderer.dispose();
        stage.dispose();
    }

}
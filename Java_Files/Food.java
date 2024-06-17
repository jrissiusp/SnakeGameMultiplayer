package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.List;

/**
 * Represents a piece of food in the game that can be consumed by the snakes.
 */
public class Food extends Square {

    // Type of the food
    int type;

    // Image of the food
    Texture foodImage;

    /**
     * Constructor for the Food class.
     * Creates a new piece of food and sets its initial position and type.
     *
     * @param snake1 The list of squares representing the first snake.
     * @param snake2 The list of squares representing the second snake.
     */
    public Food(List<Square> snake1, List<Square> snake2) {
        super(0, 0);
        renew(snake1, snake2);
    }

    /**
     * Sets a new position and type for the food.
     *
     * @param snake1 The list of squares representing the first snake.
     * @param snake2 The list of squares representing the second snake.
     */
    public void renew(List<Square> snake1, List<Square> snake2) {
        Point pos = findNewPos();
        // Ensure the new position is not occupied by either snake
        if(checkPos(snake1, pos) && checkPos(snake2, pos)){
            x = pos.x;
            y = pos.y;
        }

        // Generate a random number to determine the type of food
        int prob = (int) (Math.random() * 100);

        // Assign the food type and image based on the probability
        if(prob <= 70) {
            type = 1;
            foodImage = new Texture(Gdx.files.internal("apple.png"));
        }
        else if(prob <= 80) {
            type = 2;
            foodImage = new Texture(Gdx.files.internal("snowflake.png"));
        }
        else if(prob <= 90) {
            type = 3;
            foodImage = new Texture(Gdx.files.internal("lightning.png"));
        }
        else if(prob <= 97) {
            type = 4;
            foodImage = new Texture(Gdx.files.internal("silverapple.png"));
        }
        else if(prob <= 99) {
            type = 5;
            foodImage = new Texture(Gdx.files.internal("goldenapple.png"));
        }
    }

    /**
     * Defines the actions to be taken when food is consumed by a snake and for some foods the effects on another snake.
     *
     * @param snake The snake that consumed the food.
     * @param otherSnake The other snake in the game.
     */
    public int eaten(Snake snake, Snake otherSnake){
        // Perform actions based on the type of food consumed
        switch(type){
            case 1:
                // Normal food: grow the snake
                snake.grow();
                break;
            case 2:
                // Snowflake: grow the snake, slow down the other snake
                snake.grow();
                otherSnake.mod = otherSnake.mod * 2;
                otherSnake.duration += 400;
                break;
            case 3:
                // Lightning: grow the snake, speed up the other snake
                snake.grow();
                otherSnake.mod = otherSnake.mod / 2;
                otherSnake.duration += 400;
                break;
            case 4:
                // Silver apple: grow the snake by 3 segments
                for(int i = 0; i < 3; i++) {
                    snake.grow();
                }
                break;
            case 5:
                // Golden apple: grow the snake by 5 segments
                for(int i = 0; i < 5; i++) {
                    snake.grow();
                }
                break;
        }

        return type;
    }

    /**
     * Finds a new position for the food on the game grid.
     *
     * @return The new position as a Point object.
     */
    public Point findNewPos() {
        // Calculate the number of squares in the game grid
        int x_grid = SnakeGame.WIDTH / SnakeGame.SQUARE_SIZE;
        int y_grid = SnakeGame.HEIGHT / SnakeGame.SQUARE_SIZE;

        // Generate random coordinates within the grid
        int x = ((int) (Math.random() * x_grid)) * SnakeGame.SQUARE_SIZE;
        int y = ((int) (Math.random() * y_grid)) * SnakeGame.SQUARE_SIZE;

        return new Point(x, y);
    }

    /**
     * Checks if the given position is valid (not occupied by the snake).
     *
     * @param snake The list of squares representing the snake.
     * @param pos The position to be checked.
     * @return true if the position is not occupied by the snake, false otherwise.
     */
    public boolean checkPos(List<Square> snake, Point pos){
        // Iterate through each square in the snake
        for (Square s : snake) {
            // If any square in the snake occupies the position, return false
            if (s.x == pos.x && s.y == pos.y) {
                return false;
            }
        }
        // Position is valid if not occupied by any part of the snake
        return true;
    }

}

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Snake in the game.
 */
public class Snake {

    // List to store all segments of the snake's body
    public final ArrayList<Square> body = new ArrayList<>();
    public Square head; // The head of the snake
    private float dx, dy; // Movement direction
    private float auxDx, auxDy; // Auxiliary direction variables used to ensure that the snake drawing is updated correctly

    float mod; // Used to determine if the snake is under any weather effects (default = 8)
    float duration; // Duration for power-ups
    float uptade; // Update counter

    // Key bindings for movement
    int up, down, left, right;

    int size; // Current size of the snake

    // Textures for different parts of the snake
    Texture headUp, headDown, headLeft, headRight;
    Texture bodyVertical, bodyHorizontal, bodyTurnUpRight, bodyTurnUpLeft, bodyTurnDownRight, bodyTurnDownLeft;
    Texture tailUp, tailDown, tailLeft, tailRight;

    /**
     * Constructor to initialize the Snake.
     *
     * @param x         Initial x-coordinate of the snake's head.
     * @param y         Initial y-coordinate of the snake's head.
     * @param up        Key binding for moving up.
     * @param down      Key binding for moving down.
     * @param left      Key binding for moving left.
     * @param right     Key binding for moving right.
     * @param direction Initial direction of movement.
     * @param skin      Chose skin by player
     */
    public Snake(int x, int y, int up, int down, int left, int right, float direction, int skin) {
        // Initialize the head of the snake
        head = new Square(x, y);
        body.add(head); // Add head to the body list
        dx = direction; // Set initial direction
        size = 1; // Initial size of the snake

        // Set movement key bindings
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;

        uptade = 0; // Initialize update counter
        mod = 8; // Initial modulus for movement
        duration = 0; // Initialize duration for power-ups

        chooseColor(skin); // Choose snake skin based on player number
        grow(); // Initial growth of the snake
    }

    /**
     * Update method to move the snake and update its state.
     */
    public void updatePlayer() {
        dirCalc(); // Calculate direction based on user input
        body.remove(body.size() - 1); // Remove the tail segment
        moveSnake(); // Move the snake based on calculated direction
    }

    /**
     * Moves the snake in the current direction.
     */
    private void moveSnake() {
        auxDx = dx; // Store current direction in auxiliary variables
        auxDy = dy; // Used to prevent the snake's head from printing without moving

        float new_x = head.x + dx; // Calculate new x-coordinate of the head
        float new_y = head.y + dy; // Calculate new y-coordinate of the head
        head = new Square(new_x, new_y); // Create a new head Square object

        body.add(0, head); // Add new head to the beginning of the body list
    }

    /**
     * Increase the size of the snake by adding a new segment.
     */
    public void grow() {
        Square tail = body.get(body.size()-1); // Get the current tail segment
        body.add(new Square(tail.x, tail.y)); // Add a new segment at the tail position
        size++; // Increment the size of the snake
    }

    /**
     * Check if the snake has collided with any of the two foods.
     *
     * @param food1 First food object.
     * @param food2 Second food object.
     * @return 1 if collided with food1, 2 if collided with food2, otherwise 0.
     */
    public int checkCollideWithFood(Food food1, Food food2) {
        if (head.overlaps(food1)) { // Check collision with food1
            return 1; // Return 1 if collided with food1
        }
        if (head.overlaps(food2)) { // Check collision with food2
            return 2; // Return 2 if collided with food2
        }
        return 0; // Return 0 if no collision with any food
    }

    /**
     * Check if the snake has collided with the boundaries or the other snake.
     *
     * @param otherSnake List of squares representing the other snake's body.
     * @return 1 if collided with boundaries or other snake, otherwise 0.
     */
    public int checkGameEnd(List<Square> otherSnake) {
        // Check collision with boundaries
        if (head.x == SnakeGame.WIDTH || head.x < 0 || head.y == SnakeGame.HEIGHT || head.y < 0) {
            return 1; // Return 1 if collided with boundaries
        }

        // Check collision with other snake's body segments
        for (Square s : otherSnake) {
            if (head.overlaps(s)) {
                return 1; // Return 1 if collided with other snake
            }
        }
        return 0; // Return 0 if no collision
    }

    /**
     * Calculate the direction based on user input.
     */
    public void dirCalc() {
        // Check user input for movement keys and set direction accordingly
        if ((Gdx.input.isKeyPressed(left) && dx < 1)) {
            dy = 0;
            dx = -SnakeGame.SQUARE_SIZE;
        } else if ((Gdx.input.isKeyPressed(right) && dx > -1)) {
            dy = 0;
            dx = SnakeGame.SQUARE_SIZE;
        } else if ((Gdx.input.isKeyPressed(up)) && dy > -1) {
            dx = 0;
            dy = SnakeGame.SQUARE_SIZE;
        } else if ((Gdx.input.isKeyPressed(down)) && dy < 1) {
            dx = 0;
            dy = -SnakeGame.SQUARE_SIZE;
        }
    }

    /**
     * Draw the snake on the screen using the provided SpriteBatch.
     *
     * @param spriteBatch The SpriteBatch to draw the snake.
     */
    public void draw(SpriteBatch spriteBatch) {
        // Draw the body segments
        for (int i = 1; i < body.size() - 1; i++) {
            Square current = body.get(i); // Current body segment
            Square next = body.get(i + 1); // Next body segment
            Square previous = body.get(i - 1); // Previous body segment

            // Determine the texture based on the orientation of the body segments
            if (previous.x == current.x && next.x == current.x) {
                spriteBatch.draw(bodyVertical, current.x, current.y);
            } else if (previous.y == current.y && next.y == current.y) {
                spriteBatch.draw(bodyHorizontal, current.x, current.y);
            } else {
                if ((previous.x < current.x && next.y > current.y) || (next.x < current.x && previous.y > current.y)) {
                    spriteBatch.draw(bodyTurnUpLeft, current.x, current.y);
                } else if ((previous.x > current.x && next.y > current.y) || (next.x > current.x && previous.y > current.y)) {
                    spriteBatch.draw(bodyTurnUpRight, current.x, current.y);
                } else if ((previous.x < current.x && next.y < current.y) || (next.x < current.x && previous.y < current.y)) {
                    spriteBatch.draw(bodyTurnDownLeft, current.x, current.y);
                } else if ((previous.x > current.x && next.y < current.y) || (next.x > current.x && previous.y < current.y)) {
                    spriteBatch.draw(bodyTurnDownRight, current.x, current.y);
                }
            }
        }

        // Draw the tail segment
        if (body.size() >= 2) {
            Square tail = body.get(body.size() - 1); // Tail segment
            Square beforeTail = body.get(body.size() - 2); // Segment before tail

            // Determine the texture based on the orientation of the tail segment
            if (beforeTail.x < tail.x) {
                spriteBatch.draw(tailRight, tail.x, tail.y);
            } else if (beforeTail.x > tail.x) {
                spriteBatch.draw(tailLeft, tail.x, tail.y);
            } else if (beforeTail.y < tail.y) {
                spriteBatch.draw(tailUp, tail.x, tail.y);
            } else if (beforeTail.y > tail.y) {
                spriteBatch.draw(tailDown, tail.x, tail.y);
            }
        }

        // Draw the head segment
        if (auxDx > 0) {
            spriteBatch.draw(headRight, head.x, head.y);
        } else if (auxDx < 0) {
            spriteBatch.draw(headLeft, head.x, head.y);
        } else if (auxDy > 0) {
            spriteBatch.draw(headUp, head.x, head.y);
        } else if (auxDy < 0) {
            spriteBatch.draw(headDown, head.x, head.y);
        }
    }

    /**
     * Choose the snake's color based on the player's choice.
     *
     * @param skin Player's choice for snake color.
     */
    void chooseColor(int skin) {
        switch (skin) {
            case 1:
                // Green Skin
                headUp = new Texture(Gdx.files.internal("headup.png"));
                headDown = new Texture(Gdx.files.internal("headdown.png"));
                headLeft = new Texture(Gdx.files.internal("headleft.png"));
                headRight = new Texture(Gdx.files.internal("headright.png"));

                bodyVertical = new Texture(Gdx.files.internal("bodyvertical.png"));
                bodyHorizontal = new Texture(Gdx.files.internal("bodyhorizontal.png"));
                bodyTurnUpRight = new Texture(Gdx.files.internal("curverightup.png"));
                bodyTurnUpLeft = new Texture(Gdx.files.internal("curveleftup.png"));
                bodyTurnDownRight = new Texture(Gdx.files.internal("curverightdown.png"));
                bodyTurnDownLeft = new Texture(Gdx.files.internal("curveleftdown.png"));

                tailUp = new Texture(Gdx.files.internal("taildown.png"));
                tailDown = new Texture(Gdx.files.internal("tailup.png"));
                tailLeft = new Texture(Gdx.files.internal("tailleft.png"));
                tailRight = new Texture(Gdx.files.internal("tailright.png"));
                break;

            case 2:
                // White Skin
                headUp = new Texture(Gdx.files.internal("headup2.png"));
                headDown = new Texture(Gdx.files.internal("headdown2.png"));
                headLeft = new Texture(Gdx.files.internal("headleft2.png"));
                headRight = new Texture(Gdx.files.internal("headright2.png"));

                bodyVertical = new Texture(Gdx.files.internal("bodyvertical2.png"));
                bodyHorizontal = new Texture(Gdx.files.internal("bodyhorizontal2.png"));
                bodyTurnUpRight = new Texture(Gdx.files.internal("curverightup2.png"));
                bodyTurnUpLeft = new Texture(Gdx.files.internal("curveleftup2.png"));
                bodyTurnDownRight = new Texture(Gdx.files.internal("curverightdown2.png"));
                bodyTurnDownLeft = new Texture(Gdx.files.internal("curveleftdown2.png"));

                tailUp = new Texture(Gdx.files.internal("taildown2.png"));
                tailDown = new Texture(Gdx.files.internal("tailup2.png"));
                tailLeft = new Texture(Gdx.files.internal("tailleft2.png"));
                tailRight = new Texture(Gdx.files.internal("tailright2.png"));
                break;

            case 3:
                // Blue skin
                headUp = new Texture(Gdx.files.internal("headup_texture_blue.png"));
                headDown = new Texture(Gdx.files.internal("headdown_texture_blue.png"));
                headLeft = new Texture(Gdx.files.internal("headleft_texture_blue.png"));
                headRight = new Texture(Gdx.files.internal("headright_texture_blue.png"));

                bodyVertical = new Texture(Gdx.files.internal("bodyvertical_texture_blue.png"));
                bodyHorizontal = new Texture(Gdx.files.internal("bodyhorizontal_texture_blue.png"));
                bodyTurnUpRight = new Texture(Gdx.files.internal("curverightup_texture_blue.png"));
                bodyTurnUpLeft = new Texture(Gdx.files.internal("curveleftup_texture_blue.png"));
                bodyTurnDownRight = new Texture(Gdx.files.internal("curverightdown_texture_blue.png"));
                bodyTurnDownLeft = new Texture(Gdx.files.internal("curveleftdown_texture_blue.png"));

                tailUp = new Texture(Gdx.files.internal("taildown_texture_blue.png"));
                tailDown = new Texture(Gdx.files.internal("tailup_texture_blue.png"));
                tailLeft = new Texture(Gdx.files.internal("tailleft_texture_blue.png"));
                tailRight = new Texture(Gdx.files.internal("tailright_texture_blue.png"));
                break;

            case 4:
                // Red skin
                headUp = new Texture(Gdx.files.internal("headup_texture_red.png"));
                headDown = new Texture(Gdx.files.internal("headdown_texture_red.png"));
                headLeft = new Texture(Gdx.files.internal("headleft_texture_red.png"));
                headRight = new Texture(Gdx.files.internal("headright_texture_red.png"));

                bodyVertical = new Texture(Gdx.files.internal("bodyvertical_texture_red.png"));
                bodyHorizontal = new Texture(Gdx.files.internal("bodyhorizontal_texture_red.png"));
                bodyTurnUpRight = new Texture(Gdx.files.internal("curverightup_texture_red.png"));
                bodyTurnUpLeft = new Texture(Gdx.files.internal("curveleftup_texture_red.png"));
                bodyTurnDownRight = new Texture(Gdx.files.internal("curverightdown_texture_red.png"));
                bodyTurnDownLeft = new Texture(Gdx.files.internal("curveleftdown_texture_red.png"));

                tailUp = new Texture(Gdx.files.internal("taildown_texture_red.png"));
                tailDown = new Texture(Gdx.files.internal("tailup_texture_red.png"));
                tailLeft = new Texture(Gdx.files.internal("tailleft_texture_red.png"));
                tailRight = new Texture(Gdx.files.internal("tailright_texture_red.png"));
                break;

            case 5:
                // Yellow Skin
                headUp = new Texture(Gdx.files.internal("headup_texture_yellow.png"));
                headDown = new Texture(Gdx.files.internal("headdown_texture_yellow.png"));
                headLeft = new Texture(Gdx.files.internal("headleft_texture_yellow.png"));
                headRight = new Texture(Gdx.files.internal("headright_texture_yellow.png"));

                bodyVertical = new Texture(Gdx.files.internal("bodyvertical_texture_yellow.png"));
                bodyHorizontal = new Texture(Gdx.files.internal("bodyhorizontal_texture_yellow.png"));
                bodyTurnUpRight = new Texture(Gdx.files.internal("curverightup_texture_yellow.png"));
                bodyTurnUpLeft = new Texture(Gdx.files.internal("curveleftup_texture_yellow.png"));
                bodyTurnDownRight = new Texture(Gdx.files.internal("curverightdown_texture_yellow.png"));
                bodyTurnDownLeft = new Texture(Gdx.files.internal("curveleftdown_texture_yellow.png"));

                tailUp = new Texture(Gdx.files.internal("taildown_texture_yellow.png"));
                tailDown = new Texture(Gdx.files.internal("tailup_texture_yellow.png"));
                tailLeft = new Texture(Gdx.files.internal("tailleft_texture_yellow.png"));
                tailRight = new Texture(Gdx.files.internal("tailright_texture_yellow.png"));
                break;
        }
    }

}

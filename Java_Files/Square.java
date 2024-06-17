package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;

/**
 * "auxiliary" class
 * used only to maintain the idea that food is a house and the snake is a vector of square squares on the board
 */

public class Square extends Rectangle {

    public Square(float x, float y) {
        super(x, y, SnakeGame.SQUARE_SIZE - 1, SnakeGame.SQUARE_SIZE - 1);
    }
}
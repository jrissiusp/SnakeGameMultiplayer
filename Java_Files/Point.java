package com.mygdx.game;

/**
 * "auxiliary" class
 * used only to facilitate the representation of food as a point (with two coordinates)
 */

public class Point {
    public float x;
    public float y;

    public Point(float x, float y) {this.x = x; this.y = y;}

    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }
}
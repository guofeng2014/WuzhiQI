package com.wuzhiqi;

/**
 * 作者：guofeng
 * ＊ 日期:16/7/1
 */
public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        Point p = ((Point) o);
        return p.getX() == x && p.getY() == y;
    }
}

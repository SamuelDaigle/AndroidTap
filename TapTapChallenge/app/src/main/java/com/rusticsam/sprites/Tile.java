package com.rusticsam.sprites;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.rusticsam.Game;
import com.rusticsam.GameView;


public abstract class Tile extends Sprite
{
    protected Paint paint;
    protected int gridX;
    protected int gridY;

    Tile(GameView view, Game game, int x, int y)
    {
        super(view, game);
        paint = new Paint();
        gridX = x;
        gridY = y;
    }

    public abstract void release();

    @Override
    public void draw(Canvas canvas) {
        this.height = this.width = canvas.getWidth() / 5;
        this.x = gridX * width;
        this.y = gridY * height + height;
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public abstract void onPress();

    public abstract boolean isGood();
}

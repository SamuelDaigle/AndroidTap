package com.rusticsam.sprites;

import com.rusticsam.Game;
import com.rusticsam.GameView;
import com.rusticsam.R;
import com.rusticsam.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class BadTile extends Tile {

    public static Bitmap globalBitmapRed;
    
    public BadTile(GameView view, Game game, int x, int y) {
        super(view, game, x, y);
    }

    @Override
    public void release()
    {

    }

    @Override
    public void draw(Canvas canvas) {
        this.height = this.width = canvas.getWidth() / 5;
        this.x = gridX * width;
        this.y = gridY * height + height;
    }

    @Override
    public void onPress() {

    }

    @Override
    public boolean isGood() {
        return false;
    }
}

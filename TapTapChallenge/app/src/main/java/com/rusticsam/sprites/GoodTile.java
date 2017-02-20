package com.rusticsam.sprites;

import com.rusticsam.Game;
import com.rusticsam.GameView;
import com.rusticsam.R;
import com.rusticsam.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class GoodTile extends Tile {
    public static Bitmap globalBitmap;
    public static Bitmap globalBitmapWarning;
    protected Timer deathTimer;

    public GoodTile(GameView view, Game game, int x, int y) {
        super(view, game, x, y);

        if(globalBitmap == null){
            globalBitmap = Util.getDownScaledBitmapAlpha8(game, R.drawable.bg_green);
        }
        if(globalBitmapWarning == null){
            globalBitmapWarning = Util.getDownScaledBitmapAlpha8(game, R.drawable.bg_red);
        }

        this.bitmap = globalBitmap;

        deathTimer = new Timer();
        deathTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                changeToRedTexture();
            }
        }, 750);
        deathTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                destroyItself();
            }
        }, 1500);
    }

    @Override
    public void release()
    {
        if (deathTimer != null)
        {
            Log.d("Tile", "destroy at " + gridX + " - " + gridX);
            deathTimer.cancel();
            deathTimer.purge();
            deathTimer = null;
        }
    }

    private void destroyItself()
    {
        if (deathTimer != null)
            view.destroyGoodTileAt(gridX, gridY);
    }

    private void changeToRedTexture()
    {
        this.bitmap = globalBitmapWarning;
    }

    @Override
    public void draw(Canvas canvas) {
        this.height = this.width = canvas.getWidth() / 5;
        this.x = gridX * width;
        this.y = gridY * height + height;

        dst.set(x, y, x + width, y + height);
        canvas.drawBitmap(this.bitmap, null, dst, null);
    }

    @Override
    public void onPress() {
        release();
    }

    @Override
    public boolean isGood() {
        return true;
    }
}

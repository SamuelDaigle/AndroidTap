package com.rusticsam.sprites;

import com.rusticsam.Game;
import com.rusticsam.GameView;
import com.rusticsam.R;
import com.rusticsam.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background extends Sprite {

    /** Static bitmap to reduce memory usage */
    public static Bitmap globalBitmap;
    
    public Background(GameView view, Game game) {
        super(view, game);
        
        if(globalBitmap == null){
            globalBitmap = Util.getDownScaledBitmapAlpha8(game, R.drawable.background);
        }

        this.bitmap = globalBitmap;
    }

    /**
     * Draws the bitmap to the Canvas.
     * The height of the bitmap will be scaled to the height of the canvas.
     * When the bitmap is scrolled to far to the left, so it won't cover the whole screen,
     * the bitmap will be drawn another time behind the first one.
     */
    @Override
    public void draw(Canvas canvas) {
        double factor = (1.0 * canvas.getHeight()) / bitmap.getHeight();
        x = (bitmap.getWidth() - canvas.getWidth()) / 2;

        int endBitmap = Math.min(x + (int) (canvas.getWidth() / factor), bitmap.getWidth());
        int endCanvas = (int) ((endBitmap + x) * factor) + 1;
        src.set(x, 0, endBitmap, bitmap.getHeight());
        dst.set(0, 0, endCanvas, canvas.getHeight());
        canvas.drawBitmap(this.bitmap, src, dst, null);
        
        if(endBitmap == bitmap.getWidth()){
            // draw second bitmap
            src.set(0, 0, (int) (canvas.getWidth() / factor), bitmap.getHeight());
            dst.set(endCanvas, 0, endCanvas + canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(this.bitmap, src, dst, null);
        }
    }
}
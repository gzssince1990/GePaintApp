package com.example.ge.gepaintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by yuanda on 2015/5/9.
 */
public class TestView2 extends View {
    Bitmap backgroundBitmap;
    Canvas backgroundCanvas;
    Context context;


    public TestView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        backgroundCanvas = new Canvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(backgroundBitmap,0,0,null);
    }

    public void loadTheme(int themeId,int height, int width){

        backgroundBitmap = BitmapFactory.decodeResource(getResources(),themeId);
        backgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888,true);
        backgroundBitmap = getResizedBitmap(backgroundBitmap,height,width);
        //backgroundCanvas.setBitmap(backgroundBitmap);

        //refresh screen;
        postInvalidate();

        Log.w("theme", "i am working");
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;

        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP

        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,false);

        return resizedBitmap;

    }
}

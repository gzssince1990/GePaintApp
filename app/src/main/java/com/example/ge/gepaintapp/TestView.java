package com.example.ge.gepaintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Ge on 2015/5/9.
 */
public class TestView extends View {

    Bitmap backgroundBitmap;
    Canvas backgroundCanvas;
    Bitmap myBitmap;
    Canvas myCanvas;

    Paint myPaint;
    Context context;

    String fileName;

    static final int LOAD_BACKGROUND = 2;
    static final int LOAD_DRAWING = 3;

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);

        myPaint = new Paint();
        this.context = context;
        backgroundCanvas = new Canvas();
        myCanvas = new Canvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(backgroundBitmap,0,0,null);
    }

    public String load(String fileName){
        this.fileName = fileName;
        return load(LOAD_DRAWING)+ "\n" +  load(LOAD_BACKGROUND);
    }

    public String load(int loadMode){
        String filePath = "";

        if (loadMode == LOAD_DRAWING){
            filePath = loadToFiles(fileName);
        } else if (loadMode == LOAD_BACKGROUND){
            filePath = load("background",fileName);
        }

        return filePath;
    }

    public String load(String dirStr,String fileName){
        /* to imageDir inner storage;
        path and name */
        String filePath = "";
        //String fileName = "mydrawing.png";

        //get path
        File dir = context.getDir(dirStr, Context.MODE_PRIVATE);
        File file = new File(dir,fileName);

        //read file to map
        try {
            FileInputStream fis = new FileInputStream(file);
            backgroundBitmap = BitmapFactory.decodeStream(fis);
            fis.close();

            filePath = file.getAbsolutePath();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        //make myCanvas mutable;
        backgroundCanvas.setBitmap(
                backgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888,true));
        backgroundCanvas.drawBitmap(myBitmap,0,0,null);


        //refresh screen;
        //postInvalidate();

        return filePath;
    }

    public String loadToFiles(String fileName){
        /* load from /data/data/package_name/files(default)*/

        String filePath = "";
        //String fileName = "mydrawing.png";

        try {
            FileInputStream fis = context.openFileInput(fileName);
            myBitmap = BitmapFactory.decodeStream(fis);
            fis.close();

            filePath = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        myBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888,true);
        myCanvas.setBitmap(myBitmap);

        return filePath;
        /* default */
    }
}

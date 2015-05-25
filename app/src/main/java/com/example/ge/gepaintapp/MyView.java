package com.example.ge.gepaintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

/**
 * Created by Ge on 2015/4/10.
 */
public class MyView extends View {
    Bitmap backgroundBitmap;
    Canvas backgroundCanvas;
    Bitmap myBitmap;
    Canvas myCanvas;
    Bitmap backgroundBackup;

    Paint myPaint;
    Path myPath;
    Context context;
    int width,height;
    int backgroundColor;

    Stack<Bitmap> undoStack;
    Stack<Bitmap> redoStack;

    static final int SAVE_BACKGROUND = 0;
    static final int SAVE_DRAWING = 1;
    static final int LOAD_BACKGROUND = 2;
    static final int LOAD_DRAWING = 3;
    static final String BACKGROUND_PATH = "background";

    public MyView(Context context, int w, int h) {
            super(context);
            this.context = context;
            width = w;
            height = h;
            backgroundColor = Color.WHITE;

            myPath = new Path();

            initialPaint();
            setBitmap(width,height);

        undoStack = new Stack<>();
        redoStack = new Stack<>();
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap);
        undoStack.push(tempBitmap);

        }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        backgroundColor = Color.WHITE;

        myPath = new Path();

        initialPaint();
    }



    public void initialPaint(){
        myPaint = new Paint();

        myPaint.setAntiAlias(true);
        myPaint.setColor(Color.BLACK);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(5);
    }

    public void setBitmap(int w,int h){

        width = w;
        height = h;

        backgroundCanvas = new Canvas();
        backgroundBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        backgroundCanvas.setBitmap(backgroundBitmap);

        myCanvas = new Canvas();
        myBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        myCanvas.setBitmap(myBitmap);

        //set background color; we don't want it's opaque;
        backgroundCanvas.drawColor(backgroundColor);
        backgroundBackup = Bitmap.createBitmap(backgroundBitmap);
        //mergeLayers();
        postInvalidate();

        undoStack = new Stack<>();
        redoStack = new Stack<>();
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap);
        undoStack.push(tempBitmap);

        //another way to make canvas white;
        //myCanvas.drawRect(0,0,width,height,backgroundPaint);
    }

    public void setColor(int color){
        myPaint.setColor(color);
    }

    @Override
    public void setBackgroundColor(int color){
        backgroundColor = color;
        backgroundCanvas.drawColor(backgroundColor);
        backgroundBackup = Bitmap.createBitmap(backgroundBitmap);
        invalidate();
    }

    public void reset(){
        backgroundColor = Color.WHITE;
        undoStack.clear();
        redoStack.clear();
        setBitmap(width,height);
        postInvalidate();

        //Bitmap tempBitmap = Bitmap.createBitmap(myBitmap);
        //undoStack.push(tempBitmap);
    }

    public void mergeLayers(){
        backgroundCanvas.drawBitmap(myBitmap,0,0,null);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        mergeLayers();
        canvas.drawBitmap(backgroundBitmap,0,0,null);

        //canvas.drawPath(myPath,myPaint);
        //Toast.makeText(context,"I can feel you",Toast.LENGTH_LONG).show();
    }

    private float pointX, pointY;
    private static final float OFFSET_TOLERANCE = 4;

    private void actionDown(float x, float y){
        myPath = new Path();
        myPath.moveTo(x,y);
        pointX = x;
        pointY = y;
        myCanvas.drawPath(myPath,myPaint);
    }

    private void actionMove(float x, float y){
        float dx = Math.abs(x - pointX);
        float dy = Math.abs(y - pointY);
        if (dx >= OFFSET_TOLERANCE || dy >= OFFSET_TOLERANCE){
            myPath.quadTo(pointX, pointY, (x + pointX) / 2, (y + pointY) / 2);
            pointX = x;
            pointY = y;
            myCanvas.drawPath(myPath,myPaint);
        }
    }

    private void actionUp(float x, float y){
        myCanvas.drawPath(myPath,myPaint);

        //debug
        //Log.w("debugging","("+ myBitmap.getWidth() + ", " + myBitmap.getHeight() + ")");
        //debug end

        //add to undo stack and clean redo stack
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap);
        undoStack.push(tempBitmap);
        redoStack.clear();
    }

    public void undo(){

        if (undoStack.size()>1){

            redoStack.push(undoStack.pop());

            myBitmap = Bitmap.createBitmap(undoStack.peek());
            myCanvas.setBitmap(myBitmap);
            //setBackgroundColor(backgroundColor);
            backgroundBitmap = Bitmap.createBitmap(backgroundBackup);
            backgroundCanvas.setBitmap(backgroundBitmap);
            postInvalidate();
        }
    }

    public void redo(){

        if (redoStack.size()>0){
            undoStack.push(redoStack.pop());

            myBitmap = Bitmap.createBitmap(undoStack.peek());
            myCanvas.setBitmap(myBitmap);
            //setBackgroundColor(backgroundColor);
            backgroundBitmap = Bitmap.createBitmap(backgroundBackup);
            backgroundCanvas.setBitmap(backgroundBitmap);
            postInvalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                actionDown(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(x,y);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(x,y);
                break;
        }
        postInvalidate();
        return true;
    }

    //for save
    public String save(String fileName){
        return save(SAVE_BACKGROUND,fileName)+ "\n"+ save(SAVE_DRAWING, fileName);
    }

    public String save(int saveMode, String fileName){
        String filePath = "";
        if (saveMode == SAVE_BACKGROUND){
            filePath = save(backgroundBitmap,BACKGROUND_PATH,fileName);
        }else if (saveMode == SAVE_DRAWING){
            filePath = save(myBitmap,fileName);
        }
        return filePath;
    }

    public String save(Bitmap bitmap, String dirString, String fileName){
        /* store in inner storage */
        //to imageDir
        //String fileName = "mydrawing1.png";
        String filePath = "";
        File dir = context.getDir(dirString, Context.MODE_PRIVATE);
        File file = new File(dir,fileName);

        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();
            filePath = file.getAbsolutePath();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return filePath;
        /* store in inner storage; */
    }

    public String save(Bitmap bitmap, String fileName){
        /* store in inner storage , got the reason has to be world_readable*/
        /* store in inner storage; */
        //String fileName = "mydrawing1.png";
        String filePath = "";

        try {
            //file.createNewFile();
            FileOutputStream out = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            //out.write(filePath.getBytes());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            filePath = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }




    public String load(String fileName){
        return load(LOAD_DRAWING,fileName)+ "\n" +  load(LOAD_BACKGROUND, fileName);
    }
    //wait for testing. All code below need more test.
    public String load(int loadMode, String fileName){
        String filePath = "";

        if (loadMode == LOAD_DRAWING){
            filePath = loadToFiles(fileName);
        } else if (loadMode == LOAD_BACKGROUND){
            filePath = load(BACKGROUND_PATH,fileName);
        }

        return filePath;
    }

    public String load(String dirStr,String fileName){
        /* to imageDir inner storage;
        path and name */
        reset();
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
        backgroundBackup  = Bitmap.createBitmap(backgroundBitmap);
        backgroundCanvas.drawBitmap(myBitmap,0,0,null);


        //refresh screen;
        postInvalidate();

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

        postInvalidate();

        return filePath;
        /* default */
    }

    //load the theme
    public void loadTheme(int themeId){

        backgroundBitmap = BitmapFactory.decodeResource(getResources(),themeId);

        backgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888,true);
        backgroundBitmap = getResizedBitmap(backgroundBitmap,height,width);
        backgroundCanvas.setBitmap(backgroundBitmap);
        backgroundBackup  = Bitmap.createBitmap(backgroundBitmap);
        backgroundCanvas.drawBitmap(myBitmap,0,0,null);

        /*debugging
        String fileName = "mydrawing.png";
        //String filePath = "";
        File dir = new File("/sdcard/ge/");
        File file = new File(dir,fileName);

        //filePath = "/sdcard/cmpt551/ge/" + fileName;
        if (!dir.exists()){
            dir.mkdir();
        }else if (file.exists()){
            file.delete();
        }

        try {
            //file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            backgroundBackup.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        */


        /*
        //make myCanvas mutable;
        done
        */




        //refresh screen;
        postInvalidate();

        Log.w("theme","i am working");
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


    //use ByteArrayOutputStream;
    public String saveToFiles(){
        /* to /data/data/package_name/files (default)*/
        String fileName = "mydrawing.png";
        String filePath = "go find i";

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(bArr);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public String saveToSdCard(String dirStr) {
        /* store in sd card; */
        /* e.g. /sdcard/ge/; */
        String fileName = "mydrawing.png";
        String filePath = "";
        File dir = new File(dirStr);
        File file = new File(dir,fileName);

        //filePath = "/sdcard/cmpt551/ge/" + fileName;
        if (!dir.exists()){
            dir.mkdir();
        }else if (file.exists()){
            file.delete();
        }

        try {
            //file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            myBitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();
            filePath = file.getPath();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return filePath;
        /* store in sd card; */
    }

    public String loadFromSdCard(String dirStr){
        //from sd card;
        //path and name
        String filePath = "";
        String fileName = "mydrawing.png";

        //get path
        File dir = new File(dirStr);
        File file = new File(dir,fileName);

        //read file to map
        try {
            FileInputStream fis = new FileInputStream(file);
            myBitmap = BitmapFactory.decodeStream(fis);
            fis.close();

            filePath = file.getCanonicalPath();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        //make myCanvas mutable;
        myCanvas.setBitmap(myBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888,true));

        //refresh screen;
        postInvalidate();

        return filePath;
    }




}

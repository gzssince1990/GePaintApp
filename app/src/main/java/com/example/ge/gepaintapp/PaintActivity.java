package com.example.ge.gepaintapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;


public class PaintActivity extends ActionBarActivity implements ColorPickerDialog.OnColorChangedListener{
    MyView myView;
    String filePath;
    int id;
    //Button testButton;
    AlertDialog dialog;
    final int ERASER_WIDTH = 20;
    int penSize = 5;
    int currentColor = Color.BLACK;

    String fileName;

    //voice record
    Button recordButton;
    Button playButton;
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;


    //for add text
    TextView storyText;

    //for Edit
    int Mode;


    //for file format
    private static final String _PNG = ".png";
    private static final String _3GP = ".3gp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int width = intent.getIntExtra("width",0);
        int height = intent.getIntExtra("height",0);

        setContentView(R.layout.activity_paint);
        myView = (MyView) findViewById(R.id.drawer);
        //that button is for me to test if I can add something to the same screen with myView;
        //testButton = (Button) findViewById(R.id.test_btn);
        myView.setBitmap(width,height);

        //Log.w("checking pa", "(" + width + ", " + height + ")"); debugger

        fileName = intent.getStringExtra("fileName");



        //where to store the audio file
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/" + fileName + _3GP;

        //add text to story
        storyText = (TextView) findViewById(R.id.story_text);


        //for edit mode
        Mode = intent.getIntExtra("Mode",0);
        if (Mode == MyFragment.EDIT_MODE){
            myView.load(fileName + _PNG);

            loadText("text",fileName);
        }
        //edit mode end

        /* older way to set up myView
        filePath = "";
        Intent intent = getIntent();
        int width = intent.getIntExtra("width",0);
        int height = intent.getIntExtra("height",0);
        myView = new MyView(this,width,height);
        setContentView(myView);
        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_paint, menu);
        super.onCreateOptionsMenu(menu);

        menu.add(0,Menu.FIRST  ,0,"color");
        menu.add(0,Menu.FIRST+1,1,"save");
        menu.add(0,Menu.FIRST+2,2,"load");
        menu.add(0,Menu.FIRST+3,3,"reset");
        menu.add(0,Menu.FIRST+4,4,"background");
        menu.add(0,Menu.FIRST+5,5,"undo");
        menu.add(0,Menu.FIRST+6,6,"redo");
        menu.add(0,Menu.FIRST+7,7,"Stroke");
        menu.add(0,Menu.FIRST+8,8,"pen");
        menu.add(0,Menu.FIRST+9,9,"record");
        menu.add(0,Menu.FIRST+10,10,"text");
        menu.add(0,Menu.FIRST+11,11,"theme");


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        id = item.getItemId();

        switch (id){
            case Menu.FIRST:
                new ColorPickerDialog(this,this,"pen", Color.BLACK,Color.WHITE).show();
                break;
            case Menu.FIRST+1:
                filePath = myView.save(fileName + _PNG);

                //set background, add story to database
                DatabaseManager myManager = new DatabaseManager(this);
                boolean isAdded = myManager.addRow(fileName,1);

                if (isAdded){
                    Toast.makeText(this,"Welcome " + fileName + "!",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,"Something wrong!",Toast.LENGTH_LONG).show();
                }
                myManager.close();
                /*Set the background, add story to database */

                Toast.makeText(this,filePath + "\n" + saveText("text",fileName,storyText.getText().toString()),Toast.LENGTH_LONG).show();
                break;
            case Menu.FIRST+2:
                filePath = myView.load(fileName + _PNG);
                Toast.makeText(this,filePath,Toast.LENGTH_LONG).show();
                break;
            case Menu.FIRST+3:
                myView.reset();
                break;
            case Menu.FIRST+4:
                new ColorPickerDialog(this,this,"background",Color.BLACK,Color.WHITE).show();
                break;
            case Menu.FIRST+5:
                myView.undo();
                break;
            case Menu.FIRST+6:
                myView.redo();
                break;
            case Menu.FIRST+7:
                LayoutInflater layout = LayoutInflater.from(PaintActivity.this);
                View view1 = layout.inflate(R.layout.stroke_layout, null);;

                dialog = new AlertDialog.Builder(PaintActivity.this)
                        .create();
                Window window = dialog.getWindow();

                window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
                window.setWindowAnimations(R.style.mystyle);  //添加动画
                dialog.show();
                window.setContentView(view1);
                break;
            case Menu.FIRST+8:
                LayoutInflater layout1 = LayoutInflater.from(PaintActivity.this);
                View view2 = layout1.inflate(R.layout.pen_layout, null);;

                dialog = new AlertDialog.Builder(PaintActivity.this)
                        .create();
                Window window1 = dialog.getWindow();

                window1.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
                window1.setWindowAnimations(R.style.mystyle);  //添加动画
                dialog.show();
                window1.setContentView(view2);
                break;
            case Menu.FIRST+9:
                LayoutInflater layout2 = LayoutInflater.from(PaintActivity.this);
                View view3 = layout2.inflate(R.layout.record_layout,null);
                recordButton = (Button) view3.findViewById(R.id.record_btn);
                playButton = (Button) view3.findViewById(R.id.play_btn);

                dialog = new AlertDialog.Builder(PaintActivity.this)
                        .create();
                dialog.setTitle("Voice Recorder");

                Window window2 = dialog.getWindow();
                dialog.show();
                window2.setContentView(view3);
                break;
            case Menu.FIRST+10:
                //storyText.setText("i worked"); debug
                AlertDialog.Builder  alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Telling Story");
                //alertDialog.setIcon(R.drawable.ic_action_name);
                alertDialog.setMessage("What do you want to say?");
                final EditText textInput = new EditText(this);
                textInput.setTextColor(Color.BLACK);
                textInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                alertDialog.setView(textInput);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storyText.setText(textInput.getText().toString());
                    }
                });
                alertDialog.setNegativeButton("Cancel",null);
                alertDialog.show();
                //add text end
                break;
            case Menu.FIRST+11:
                LayoutInflater layout3 = LayoutInflater.from(PaintActivity.this);
                View view4 = layout3.inflate(R.layout.theme_layout,null);

                dialog = new AlertDialog.Builder(PaintActivity.this)
                        .create();

                Window window3 = dialog.getWindow();
                dialog.show();
                window3.setContentView(view4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //below are the drawing setup functions
    public void changeStrokeSize(View view){

        switch (view.getId()){
            case R.id.large_stroke:
                penSize = 20;
                break;
            case R.id.middle_stroke:
                penSize = 10;
                break;
            case R.id.small_stroke:
                penSize = 5;
                break;
        }
        myView.myPaint.setStrokeWidth(penSize);
        dialog.dismiss();
    }

    public void changePenStyle(View view){

        switch (view.getId()){
            case R.id.pencil:
                myView.myPaint.setStrokeCap(Paint.Cap.ROUND);
                myView.myPaint.setColor(currentColor);
                myView.myPaint.setStrokeWidth(penSize);
                break;
            case R.id.brush:
                myView.myPaint.setStrokeCap(Paint.Cap.SQUARE);
                myView.myPaint.setColor(currentColor);
                myView.myPaint.setStrokeWidth(penSize);
                break;
            case R.id.eraser:
                myView.myPaint.setColor(myView.backgroundColor);
                myView.myPaint.setStrokeWidth(ERASER_WIDTH);
                myView.myPaint.setStrokeCap(Paint.Cap.SQUARE);
                break;
        }
        dialog.dismiss();
    }

    @Override
    public void colorChanged(String key, int color) {
        switch (id){
            case Menu.FIRST:
                currentColor = color;
                myView.setColor(color);
                break;
            case Menu.FIRST+4:
                myView.setBackgroundColor(color);
                break;
        }


    }


    // below are methods for record voice;
    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };


        public RecordButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };


        public PlayButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    /*  debugging  */
    public void onClickRecord(View view){
        if (recordButton.getText().equals("Record")) {
            recordButton.setText("Stop");
            onRecord(true);
            playButton.setEnabled(false);
        } else {
            recordButton.setText("Record");
            onRecord(false);
            playButton.setEnabled(true);
        }
    }

    public void onClickPlay(View view){
        if (playButton.getText().equals("Play")) {
            playButton.setText("Stop");
            onPlay(true);
            recordButton.setEnabled(false);
            Toast.makeText(this,mFileName,Toast.LENGTH_LONG).show();
        } else {
            playButton.setText("Play");
            onPlay(false);
            recordButton.setEnabled(true);
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    // voice record end


    //for save text
    public String saveText(String dirString, String fileName, String storyText){
        /* store in inner storage */
        //to imageDir
        //String fileName = "mydrawing1";
        String filePath = "";
        File dir = getDir(dirString, Context.MODE_PRIVATE);
        File file = new File(dir,fileName);

        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(storyText.getBytes());
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

    //for load text
    public String loadText(String dirString, String fileName){
         /* to imageDir inner storage;
        path and name */
        String filePath = "";
        //String fileName = "mydrawing";

        //get path
        File dir = getDir(dirString, Context.MODE_PRIVATE);
        File file = new File(dir,fileName);

        //String builder
        StringBuilder myText = new StringBuilder();

        //read file to map
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null){
                myText.append(line);
                myText.append('\n');
            }
            reader.close();

            filePath = file.getAbsolutePath();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        storyText.setText(myText.toString());
        Toast.makeText(this, myText.toString(),Toast.LENGTH_LONG).show();
        //debugger
        return filePath;
    }

    //for theme
    public void onClickTheme(View view){
        int id = view.getId();
        switch (id){
            case R.id.bear_theme:
                myView.loadTheme(R.drawable.bear_hd);
                Toast.makeText(this," bear",Toast.LENGTH_LONG).show();
                break;
            case R.id.ninja_theme:
                myView.loadTheme(R.drawable.ninja_hd);
                Toast.makeText(this," ninja",Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
    }

}

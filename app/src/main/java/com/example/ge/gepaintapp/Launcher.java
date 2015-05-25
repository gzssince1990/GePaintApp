package com.example.ge.gepaintapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;


public class Launcher extends FragmentActivity {
    //Button startButton;
    //int screenWidth;
    //int screenHeight;
    //ViewPager viewPager;

    //private DatabaseManager myManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        Log.w("watching","I am onCreate");
        reset();

        /*another test
        myManager = new DatabaseManager(this);
        myManager.openReadable();
        ArrayList<Fragment> af = new ArrayList<>();
        ArrayList<Story> storyList = myManager.retrieveRows();
        myManager.close();

        if (storyList.isEmpty()){
            af.add(new MyFragment().newInstance(Color.GREEN, ""));
        }else {
            int isEmpty = 0;
            for (Story story: storyList){
                isEmpty |= story.getStoryStatus();
            }

            if (isEmpty == 0){
                af.add(new MyFragment().newInstance(Color.GREEN, ""));
            }

            int storyNum = storyList.size();
            for (int i=0;i<storyNum; i++){
                if (storyList.get(i).getStoryStatus() == 1){
                    af.add(new MyFragment().newInstance(Color.RED,storyList.get(i).getStoryName()) );
                }
                Log.w("checking status",storyList.get(i).getStoryStatus() + "");
            }
        }
        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), af);
        viewPager.setAdapter(adapter);
       //testing end
       */
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w("watching","I am onRestart");
        reset();
    }







    /*
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("watching","I am onResume");
        reset();
    }


    public void init() {
        //get phone window size;
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
    }
    */

    public void reset(){

        DatabaseManager myManager = new DatabaseManager(this);
        myManager.openReadable();
        ArrayList<Fragment> af = new ArrayList<>();
        ArrayList<Story> storyList = myManager.retrieveRows();
        myManager.close();

        if (storyList.isEmpty()){
            af.add(new HomeFragment().newInstance());
        }else {

            int isEmpty = 0;
            for (Story story: storyList){
                isEmpty |= story.getStoryStatus();
            }

            if (isEmpty == 0){
                af.add(new HomeFragment().newInstance());
            }

            int storyNum = storyList.size();
            for (int i=0;i<storyNum; i++){
                if (storyList.get(i).getStoryStatus() == 1){
                    af.add(new MyFragment().newInstance(Color.RED,storyList.get(i).getStoryName()) );
                }
            }
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), af);
        viewPager.setAdapter(adapter);
    }







    /* old functions
    //don't use it now
    public void onClickStart(View view){

        AlertDialog.Builder  alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("New Story");
        //alertDialog.setIcon(R.drawable.ic_action_name);
        alertDialog.setMessage("Enter your story name ");
        final EditText storyName = new EditText(this);
        storyName.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialog.setView(storyName);
        alertDialog.setPositiveButton("OK", new AlertDialogListener(storyName));
        alertDialog.setNegativeButton("Cancel",new AlertDialogListener(storyName) );
        alertDialog.show();
    }
    */


    /*
    public class AlertDialogListener implements DialogInterface.OnClickListener{

        EditText storyName;

        public AlertDialogListener(EditText storyName){
            this.storyName = storyName;
        }

        public void onClick(DialogInterface dialog, int buttonId) {

            if (buttonId == -1) {
                String str = storyName.getText().toString();
                Toast.makeText(Launcher.this,"Welcome " + str + "!",Toast.LENGTH_LONG).show();

                myManager = new DatabaseManager(Launcher.this);
                boolean isAdded = myManager.addRow(str,1);

                if (isAdded){
                    Toast.makeText(Launcher.this,"Welcome " + str + "!",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Launcher.this,"Something wrong!",Toast.LENGTH_LONG).show();
                }
                myManager.close();
                *Set the background, add story to database


                init();
                Intent intent = new Intent(Launcher.this,PaintActivity.class);
                intent.putExtra("width",screenWidth);
                intent.putExtra("height",screenHeight);
                intent.putExtra("fileName",str);
                startActivity(intent);
                *start paint
                }else {//for didn't save
                init();
                Intent intent = new Intent(Launcher.this,PaintActivity.class);
                intent.putExtra("width",screenWidth);
                intent.putExtra("height",screenHeight);
                //Log.w("checking","("+ screenWidth + ", " + screenHeight + ")");
                intent.putExtra("fileName","Ge3");
                startActivity(intent);
            }
        }
    }
    */
}

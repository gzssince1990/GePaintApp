package com.example.ge.gepaintapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Administrator on 2015/5/11.
 */
public class HomeFragment extends Fragment {

    View myView;

    String fileName;

    //for Edit
    static final int EDIT_MODE = 1;


    //format
    final static String _PNG = ".png";
    final static String _3GP = ".3gp";


    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){


        myView = inflater.inflate(R.layout.fragment_layout_test,container,false);



        // click and enter the edit mode
        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Long touch to start the menu",Toast.LENGTH_LONG).show();
            }
        });

        //long click and pop put the menu
        myView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(getActivity(),"I long clicked you!",Toast.LENGTH_LONG).show();


                LayoutInflater layout = LayoutInflater.from(getActivity());
                final View menuView = layout.inflate(R.layout.launcher_menu,null);

                final ListView optionView = (ListView) menuView.findViewById(R.id.menu_list);

                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .create();
                //dialog.setTitle("Menu");

                Window window2 = dialog.getWindow();
                dialog.show();
                window2.setContentView(menuView);

                final String[] optionStr = getResources().getStringArray(R.array.menu_options);
                optionView.setAdapter(new MyListViewAdapter(optionStr,getActivity()));


                optionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(getActivity(),optionStr[position],Toast.LENGTH_LONG).show();
                        switch (position){
                            case 0:
                                AlertDialog.Builder  alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle("New Story");
                                //alertDialog.setIcon(R.drawable.ic_action_name);
                                alertDialog.setMessage("Enter your story name ");
                                final EditText storyName = new EditText(getActivity());
                                storyName.setInputType(InputType.TYPE_CLASS_TEXT);
                                alertDialog.setView(storyName);

                                alertDialog.setPositiveButton("OK", new AlertDialogListener(storyName) );
                                alertDialog.setNegativeButton("Cancel",new AlertDialogListener(storyName) );
                                alertDialog.show();
                                dialog.dismiss();
                                break;
                            case 1:
                                dialog.dismiss();
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                });


                return true;
            }
        });

        return myView;
    }





    //custom list view
    private class MyListViewAdapter extends BaseAdapter {

        private String[] options;
        private Context context;

        public MyListViewAdapter(String[] options, Context context){
            this.options = options;
            this.context = context;
        }


        @Override
        public int getCount() {
            return options.length;
        }

        @Override
        public Object getItem(int position) {
            return options[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null)
            {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.option_layout, null);
            }

            TextView optionView = (TextView) v.findViewById(R.id.option);
            optionView.setText(options[position]);

            return v;
        }
    }

    //for start option
    public class AlertDialogListener implements DialogInterface.OnClickListener{

        EditText storyName;

        public AlertDialogListener(EditText storyName){
            this.storyName = storyName;
        }

        public void onClick(DialogInterface dialog, int buttonId) {

            if (buttonId == -1) {
                String str = storyName.getText().toString();
                Toast.makeText(getActivity(), "Welcome " + str + "!", Toast.LENGTH_LONG).show();


                WindowManager wm = getActivity().getWindowManager();
                Display display = wm.getDefaultDisplay();
                int screenWidth = display.getWidth();
                int screenHeight = display.getHeight();
                Intent intent = new Intent(getActivity(),PaintActivity.class);
                intent.putExtra("width",screenWidth);
                intent.putExtra("height",screenHeight);
                intent.putExtra("fileName",str);
                startActivity(intent);
                /*start paint*/
            }else {//for didn't save
                WindowManager wm = getActivity().getWindowManager();
                Display display = wm.getDefaultDisplay();
                int screenWidth = display.getWidth();
                int screenHeight = display.getHeight();
                Intent intent = new Intent(getActivity(),PaintActivity.class);
                intent.putExtra("width",screenWidth);
                intent.putExtra("height",screenHeight);
                //Log.w("checking","("+ screenWidth + ", " + screenHeight + ")");
                intent.putExtra("fileName","Ge3");
                startActivity(intent);
            }
        }
    }
}

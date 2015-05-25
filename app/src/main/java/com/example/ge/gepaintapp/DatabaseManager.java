package com.example.ge.gepaintapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yuanda on 2015/5/9.
 */
public class DatabaseManager{

    public static final String DB_NAME = "drawing";
    public static final String DB_TABLE = "stories";
    public static final int DB_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE "
            + DB_TABLE + " (story_name TEXT, story_status INTEGER);";
    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseManager(Context context){
        this.context = context;
        helper = new SQLHelper(context);
        this.db = helper.getWritableDatabase();
    }

    public DatabaseManager openReadable() throws SQLException{
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close(){
        helper.close();
    }

    public boolean addRow(String storyName,int storyStatus){
        ContentValues newStory = new ContentValues();
        newStory.put("story_name",storyName);
        newStory.put("story_status", storyStatus);

        try {
            db.insertOrThrow(DB_TABLE,null,newStory);
        } catch (Exception e){
            Log.e("Inserting rows error ", e.toString());
            e.printStackTrace();
            return false;
        }

        db.close();
        return true;
    }

    public ArrayList<Story> retrieveRows(){
        ArrayList<Story> stories = new ArrayList<>();
        String[] columns = new String[]{"story_name","story_status"};
        Cursor cursor = db.query(DB_TABLE,columns,null,null,null,null,null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            stories.add(new Story(cursor.getString(0),cursor.getInt(1)));
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        return stories;
    }

    public void delete(String storyName){
        ContentValues deleteStatus = new ContentValues();
        deleteStatus.put("story_status",0);

        db.update(DB_TABLE,deleteStatus,"story_name='"+storyName+"'",null);

        db.close();
    }


    public class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context context){
            super(context,DB_NAME,null,DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Stories table","Upgrading database i.e. dropping table and recreating it");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}

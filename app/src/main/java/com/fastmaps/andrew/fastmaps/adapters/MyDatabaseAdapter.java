package com.fastmaps.andrew.fastmaps.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.fastmaps.andrew.fastmaps.MapData;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseAdapter{

    protected MyDatabaseHelper myDatabaseHelper;
    public MyDatabaseAdapter (Context context){
        myDatabaseHelper= new MyDatabaseHelper(context);
    }

    public long AddData(String name, String place){
        // method to add an entry (name/place) into SQLite db. returns id, which indicates whether
        // the addition was successful

        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(myDatabaseHelper.NAME, name);
        contentValues.put(myDatabaseHelper.PLACE, place);
        long id = sqLiteDatabase.insert(myDatabaseHelper.TABLE_NAME, null, contentValues);
        Log.d("AddData", "Ran AddData on " + name + " id:" + id);
        sqLiteDatabase.close();
        return id;
    }

    public void DeleteAllData(){
        // method to delete all data from SQLiteDatabase table
        Log.d("DeleteAllData", "Ran DeleteAllData");
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL(myDatabaseHelper.DELETE_TABLE);
        sqLiteDatabase.close();
    }

    public List<MapData> GetAllData(){
        // method to retrieve all data from the SQLiteDatabase. Returns mapdataList to MainActivity
        Log.d("GetAllData", "Ran GetAllData");
        List<MapData> mapdataList = new ArrayList<>(25);

        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
        String[] columns = {myDatabaseHelper.UID, myDatabaseHelper.NAME, myDatabaseHelper.PLACE};
        Cursor cursor = sqLiteDatabase.query(myDatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);

        while(cursor.moveToNext()){
            // the cursor iterates through the table, adding each data point to mapdataList
            MapData mapData = new MapData();
            String name = cursor.getString(cursor.getColumnIndex(myDatabaseHelper.NAME));
            String place = cursor.getString(cursor.getColumnIndex(myDatabaseHelper.PLACE));
            mapData.setName(name);
            mapData.setPlace(place);
            Log.d("while", "inside while loop " + mapData.getName() + " " + mapData.getPlace());
            mapdataList.add(mapData);
        }
        sqLiteDatabase.close();
        return mapdataList;
    }

    static class MyDatabaseHelper extends SQLiteOpenHelper{

        //SQLite Database schema setup
        private static final String DATABASE_NAME = "fastmapsdatabse";
        private static final String TABLE_NAME = "FASTMAPSTABLE";
        private static final int DATABASE_VERSION = 1;
        private static final String UID = "_uid";
        private static final String NAME = "Name";
        private static final String PLACE= "Place";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + UID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " VARCHAR(255), " + PLACE
                + " VARCHAR(255));";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME;
        private Context context;

        public MyDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(CREATE_TABLE);
        }
        catch (SQLException e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try{
            Toast.makeText(context, "onUpgrade", Toast.LENGTH_LONG).show();
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
        catch (SQLException e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }



}}

package com.dakrori.atlasmeasurements.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "Icarda";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "readings";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    private static final String LAT = "lat";

    private static final String LONGITUDE = "longitude";

    private static final String TYPE = "typeOfData";

    private static final String VALUE = "valueOfData";
    private static final String DateTime = "datetimeNow";
    private static final String WaterOrSoil = "WaterOrSoil";
    private static final String Address = "Address";
    private static final String SOIL_TYPE = "SOIL_TYPE";
    private static final String CROP_TYPE = "CROP_TYPE";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LAT + " TEXT,"
                + LONGITUDE + " TEXT,"
                + TYPE + " TEXT,"
                + VALUE + " TEXT,"
                + DateTime + " DATETIME,"
                + WaterOrSoil + " TEXT,"
                + Address + " TEXT,"
                + SOIL_TYPE + " TEXT,"
                + CROP_TYPE + " TEXT )";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addReading(String LAT_data
            , String LONGITUDE_data, String type_data, String Value_data, Date datetimeNow_data,
                           String WaterOrSoil1,String Address1,String SOIL_TYPE1,String CROP_TYPE1) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(LAT, LAT_data);
        values.put(LONGITUDE, LONGITUDE_data);
        values.put(TYPE, type_data);
        values.put(VALUE, Value_data);
        values.put(DateTime,getDateTime(datetimeNow_data));
        values.put(WaterOrSoil, WaterOrSoil1);
        values.put(Address, Address1);
        values.put(SOIL_TYPE, SOIL_TYPE1);
        values.put(CROP_TYPE, CROP_TYPE1);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }


    public List<ReadingObject> getReadings(String DateStart,String DateEnd){
        SQLiteDatabase db = this.getReadableDatabase();
        List<ReadingObject> ReadingList = new ArrayList<ReadingObject>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME +" WHERE "+DateTime+" BETWEEN \""+DateStart
                +"\" AND  \""+DateEnd+"\"";
        Log.v("Ahmedsql",selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ReadingObject readData = new ReadingObject(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9));
                ReadingList.add(readData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return ReadingList;

    }




    public float getReadings_of_TYPE_and_WATERORSOIL(String DateStart,String DateEnd,String TYPE_DATA,
                                                                   String WATERORSOIL){
        SQLiteDatabase db = this.getReadableDatabase();
        List<ReadingObject> ReadingList = new ArrayList<ReadingObject>();

        String selectQuery = "SELECT  AVG("+VALUE+") FROM " + TABLE_NAME +" WHERE "+TYPE+" = \""+TYPE_DATA+"\" AND "+
                WaterOrSoil+" = \""+WATERORSOIL+"\" AND "+DateTime+" BETWEEN \""+DateStart
                +"\" AND  \""+DateEnd+"\"";
        Log.v("Ahmedsql",selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                return Float.parseFloat(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // return contact list
        return 0;

    }




    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        return dateFormat.format(date);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
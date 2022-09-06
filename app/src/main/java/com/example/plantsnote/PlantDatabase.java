package com.example.plantsnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlantDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "plant_db";
    public static final int DB_VERSION = 1;

    public static final String TB_NAME = "plant";
    public static final String CLN_ID_NAME = "id";
    public static final String CLN_PLANT_NAME = "plantName";
    public static final String CLN_LOCATION_NAME = "location";
    public static final String CLN_DESCRIPTION_NAME = "description";
    public static final String CLN_IMAGE_NAME = "imageName";
    public static final String CLN_IMAGE_PATH_NAME = "imagePath";

    public PlantDatabase(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME+" ( "+CLN_ID_NAME+" INTEGER PRIMARY KEY AUTOINCREMENT , "+ CLN_PLANT_NAME +" TEXT, "+CLN_LOCATION_NAME+" TEXT, "+CLN_DESCRIPTION_NAME+" TEXT, "+ CLN_IMAGE_NAME +" TEXT, " + CLN_IMAGE_PATH_NAME + " TEXT ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}

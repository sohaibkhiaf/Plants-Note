package com.example.plantsnote;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseAccess {
    private SQLiteDatabase database;
    private SQLiteOpenHelper openHelper;
    private static DatabaseAccess instance;

    public DatabaseAccess(Context context){
        openHelper = new PlantDatabase(context);
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance == null ){
            instance = new DatabaseAccess(context);
        }

        return instance;
    }

    public void open(){
        database = openHelper.getWritableDatabase();
    }

    public void close(){
        if(database != null){
            database.close();
        }
    }

    public boolean insertPlant(Plant plant) {

        ContentValues values = new ContentValues();
        values.put(PlantDatabase.CLN_PLANT_NAME, plant.getName());
        values.put(PlantDatabase.CLN_LOCATION_NAME, plant.getLocation());
        values.put(PlantDatabase.CLN_DESCRIPTION_NAME, plant.getDescription());
        values.put(PlantDatabase.CLN_IMAGE_NAME, plant.getImageName());
        values.put(PlantDatabase.CLN_IMAGE_PATH_NAME, plant.getImagePath());

        long insertedRowNumber = database.insert(PlantDatabase.TB_NAME, null, values);
        return (insertedRowNumber != -1);
    }

    public boolean updatePlant(Plant plant) {

        ContentValues values = new ContentValues();
        values.put(PlantDatabase.CLN_PLANT_NAME, plant.getName());
        values.put(PlantDatabase.CLN_LOCATION_NAME, plant.getLocation());
        values.put(PlantDatabase.CLN_DESCRIPTION_NAME, plant.getDescription());
        values.put(PlantDatabase.CLN_IMAGE_NAME, plant.getImageName());
        values.put(PlantDatabase.CLN_IMAGE_PATH_NAME, plant.getImagePath());

        String[] args = {plant.getId()+""};

        int updatedRowsCount = database.update(PlantDatabase.TB_NAME, values, "id=?", args);
        return (updatedRowsCount != 0);
    }

    public long getPlantsCount() {
        return DatabaseUtils.queryNumEntries(database, PlantDatabase.TB_NAME);
    }

    public boolean deletePlant(Plant plant) {

        String[] args = {plant.getId()+""};

        int deletedRowsCount = database.delete(PlantDatabase.TB_NAME, "id=?", args);

        return (deletedRowsCount > 0);
    }

    @SuppressLint("Range")
    public ArrayList<Plant> getAllPlants() {

        ArrayList<Plant> allPlants = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + PlantDatabase.TB_NAME , null);

        if(cursor.moveToFirst())
        {
            do{
                int id = cursor.getInt(cursor.getColumnIndex(PlantDatabase.CLN_ID_NAME));
                String plantName = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_PLANT_NAME));
                String location = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_LOCATION_NAME));
                String description = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_DESCRIPTION_NAME));
                String imageName = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_IMAGE_NAME));
                String imagePath = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_IMAGE_PATH_NAME));

                allPlants.add( new Plant(id, plantName, location, description, imageName, imagePath));
            }while(cursor.moveToNext());
            cursor.close();
        }

        return allPlants;
    }

    @SuppressLint("Range")
    public ArrayList<Plant> getPlantsByName(String name) {

        ArrayList<Plant> plants = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PlantDatabase.TB_NAME +" WHERE "+PlantDatabase.CLN_PLANT_NAME +" LIKE ? ", new String[]{"%"+name+"%"});

        if(cursor.moveToFirst())
        {
            do{
                int id = cursor.getInt(cursor.getColumnIndex(PlantDatabase.CLN_ID_NAME));
                String plantName = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_PLANT_NAME));
                String location = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_LOCATION_NAME));
                String description = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_DESCRIPTION_NAME));
                String imageName = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_IMAGE_NAME));
                String imagePath = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_IMAGE_PATH_NAME));

                plants.add( new Plant(id, plantName, location , description, imageName, imagePath));
            }while(cursor.moveToNext());
            cursor.close();
        }

        return plants;
    }

    @SuppressLint("Range")
    public Plant getPlantById(int plantId) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + PlantDatabase.TB_NAME + " WHERE "+PlantDatabase.CLN_ID_NAME+" =? ", new String[]{String.valueOf(plantId)});

        if(cursor!= null && cursor.moveToFirst())
        {
            int id = cursor.getInt(cursor.getColumnIndex(PlantDatabase.CLN_ID_NAME));
            String plantName = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_PLANT_NAME));
            String location = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_LOCATION_NAME));
            String description = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_DESCRIPTION_NAME));
            String imageName = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_IMAGE_NAME));
            String imagePath = cursor.getString(cursor.getColumnIndex(PlantDatabase.CLN_IMAGE_PATH_NAME));

            Plant plant = new Plant(id, plantName, location, description, imageName, imagePath);
            cursor.close();
            return plant;
        }

        return null;
    }

}

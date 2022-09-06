package com.example.plantsnote.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;

public class Utils {

    public static Bitmap load(Context context , ImageHolder holder){

        File file = new File(holder.getPath(), holder.getName());
        try {
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            Toast.makeText(context, "Image is not saved.", Toast.LENGTH_LONG).show();

            return null;
        }

    }

    public static ImageHolder store(Context context, Bitmap bitmap){

        // get time
        String timeStamp = DateFormat.getDateTimeInstance().format(new Date());
        // create file name
        String fileName = "img_" + timeStamp + ".png";
        // initialize wrapper
        ContextWrapper wrapper = new ContextWrapper(context.getApplicationContext());
        // create directory
        File directory = wrapper.getDir("plants_note_images", Context.MODE_PRIVATE);
        // create path
        File path = new File(directory, fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(path);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            Toast.makeText(context, "File " + fileName + " created.", Toast.LENGTH_LONG).show();

            return new ImageHolder(fileName, directory.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return null;
        }

    }

}

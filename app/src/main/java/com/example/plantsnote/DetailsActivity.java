package com.example.plantsnote;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plantsnote.Utils.ImageHolder;
import com.example.plantsnote.Utils.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class DetailsActivity extends AppCompatActivity {

    // ui
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextInputEditText plantName_et, plantLocation_et, plantDescription_et;
    private ImageView plantImage_iv;
    private MaterialButton loadImage_mBtn;
    private MaterialButton deleteImage_mBtn;

    // db
    private DatabaseAccess database;

    // intent
    public static final int NULL_RESULT_CODE = 0;
    public static final int INSERT_RESULT_CODE = 1;
    public static final int UPDATE_RESULT_CODE = 2;
    public static final int DELETE_RESULT_CODE = 3;

    public static final String PLANT_EXTRA_KEY = "plant_key";

    private ActivityResultLauncher<Intent> launcher;

    // variables
    private int plantId = -1;
    private String imageName = null;
    private String imagePath = null;
    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // inflate XML
        collapsingToolbar = findViewById(R.id.detailsAct_collapsingToolbarLayout_collapsingToolbar);
        toolbar = findViewById(R.id.detailsAct_tb_toolbar);
        plantName_et = findViewById(R.id.detailsAct_textInputEditText_plantName);
        plantLocation_et = findViewById(R.id.detailsAct_textInputEditText_plantLocation);
        plantDescription_et = findViewById(R.id.detailsAct_textInputEditText_plantDescription);
        plantImage_iv = findViewById(R.id.detailsAct_iv_toolbarImage);
        loadImage_mBtn = findViewById(R.id.detailsAct_materialButton_loadImage);
        deleteImage_mBtn = findViewById(R.id.detailsAct_materialButton_deleteImage);

        // set toolbar
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitleEnabled(false);

        //create database
        database = DatabaseAccess.getInstance(this);

        // get intent and intent extras
        Intent intent = getIntent();
        plantId = intent.getIntExtra(MainActivity.PLANT_ID_KEY, -1);

        //set input fields content using plantId
        setInputFieldsContent();

        // set image
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if(result.getResultCode() == RESULT_OK && result.getData() != null){

                    Uri uri = result.getData().getData();

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);

                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        plantImage_iv.setImageBitmap(bitmap);

                        // copy to internal storage & get path
                        ImageHolder holder = Utils.store(getBaseContext(), bitmap);

                        if(holder != null){
                            imageName = holder.getName();
                            imagePath = holder.getPath();
                            imageChanged = true;
                        }



                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailsActivity.this, R.string.toastMessage_getContentException, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        // set load material button 'on click' listener
        loadImage_mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request permission
                if(ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(DetailsActivity.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MainActivity.PERMISSION_REQUEST_CODE);
                }else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    launcher.launch(intent);
                }
            }
        });

        // set delete material button 'on click' listener
        deleteImage_mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plantImage_iv.setImageBitmap(null);
                imageChanged = true;
            }
        });

    }

    // options menu
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.menu_details, menu);

        // set menu items visibility
        MenuItem done = menu.findItem(R.id.detailsMenu_item_done);
        MenuItem edit = menu.findItem(R.id.detailsMenu_item_edit);
        MenuItem delete = menu.findItem(R.id.detailsMenu_item_delete);

        if(plantId > -1){ // view / edit
            done.setVisible(false);
            edit.setVisible(true);
            delete.setVisible(true);
        }else if(plantId == -1){  // add
            done.setVisible(true);
            edit.setVisible(false);
            delete.setVisible(false);
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String plantName, plantLocation, plantDescription, plantImageName, plantImagePath;

        switch(item.getItemId()){
            // done clicked
            case R.id.detailsMenu_item_done:
                // get fields content
                if(plantName_et.getText() != null){
                    plantName = plantName_et.getText().toString();
                } else{plantName = "";}

                if(plantLocation_et.getText() != null){
                    plantLocation = plantLocation_et.getText().toString();
                } else{plantLocation = "";}

                if(plantDescription_et.getText() != null){
                    plantDescription = plantDescription_et.getText().toString();
                } else{plantDescription = "";}

                plantImageName = imageName;
                plantImagePath = imagePath;


                // combine content in Plant object
                Plant newPlant = new Plant(plantId, plantName, plantLocation, plantDescription, plantImageName, plantImagePath);

                // if no field is empty
                if(!plantName.isEmpty() && !plantLocation.isEmpty() && !plantDescription.isEmpty()){
                    // if editing item
                    if(plantId > -1){
                        // get edited item from database in a new object to store old values
                        database.open();
                        Plant oldPlant = database.getPlantById(plantId);
                        database.close();
                        // if nothing changed
                        if(oldPlant.getName().equals(newPlant.getName()) && oldPlant.getLocation().equals(newPlant.getLocation()) && oldPlant.getDescription().equals(newPlant.getDescription()) && !imageChanged ){
                            // don't change any thing & disable fields
                            fillPlantToFields(oldPlant);
                            disableFields();
                            // reset menu items
                            toolbar.getMenu().findItem(R.id.detailsMenu_item_done).setVisible(false);
                            toolbar.getMenu().findItem(R.id.detailsMenu_item_edit).setVisible(true);
                            toolbar.getMenu().findItem(R.id.detailsMenu_item_delete).setVisible(true);
                        }else{ // if the values of the item changed
                            // dialog : are you sure you want to modify this item?
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                            dialogBuilder.setIcon(R.drawable.ic_edit_black)
                                    .setTitle(R.string.dialogTitle_modifyItem)
                                    .setMessage(R.string.dialogMessage_modifyItem)
                                    .setPositiveButton(R.string.dialogPositiveButton_yes, new DialogInterface.OnClickListener() {
                                        // dialog yes :
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            database.open();

                                            if(! imageChanged){
                                                newPlant.setImageName(oldPlant.getImageName());
                                                newPlant.setImagePath(oldPlant.getImagePath());
                                            }
                                            // modify item (update )
                                            boolean updated = database.updatePlant(newPlant);
                                            database.close();

                                            disableFields();
                                            toolbar.getMenu().findItem(R.id.detailsMenu_item_done).setVisible(false);
                                            toolbar.getMenu().findItem(R.id.detailsMenu_item_edit).setVisible(true);
                                            toolbar.getMenu().findItem(R.id.detailsMenu_item_delete).setVisible(true);
                                            // snackBar : item modified successfully. undo?
                                            if(updated){
                                                Snackbar snackbar = Snackbar.make(plantName_et, R.string.snackBarMessage_itemModified, Snackbar.LENGTH_LONG);
                                                snackbar.setAction(R.string.snackBarButton_undo, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        // snackBar undo : reset old values
                                                        database.open();
                                                        database.updatePlant(oldPlant);
                                                        database.close();

                                                        fillPlantToFields(oldPlant);
                                                        imageChanged = false;
                                                    }
                                                });

                                                snackbar.show();
                                            }

                                        }
                                    })
                                    // dialog no :
                                    .setNegativeButton(R.string.dialogNegativeButton_no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // return to main activity
                                            setResult(NULL_RESULT_CODE, null);
                                            finish();
                                        }
                                    })
                                    // dialog cancel :
                                    .setNeutralButton(R.string.dialogNeutralButton_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // cancel dialog
                                            dialogInterface.cancel();
                                        }
                                    }).create();

                            dialogBuilder.show();
                        }

                    }else if(plantId == -1){ // if adding item

                        // add plant item
                        database.open();
                        database.insertPlant(newPlant);
                        database.close();

                        // return to main activity
                        setResult(INSERT_RESULT_CODE, null);
                        finish();
                    }
                }else{ // fields not filled completely ( some are empty)
                    Toast.makeText(this, R.string.toastMessage_fieldsEmpty, Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.detailsMenu_item_edit:
                // show edit menu buttons and enable fields
                toolbar.getMenu().findItem(R.id.detailsMenu_item_done).setVisible(true);
                toolbar.getMenu().findItem(R.id.detailsMenu_item_edit).setVisible(false);
                toolbar.getMenu().findItem(R.id.detailsMenu_item_delete).setVisible(false);

                enableFields();

                return true;

            case R.id.detailsMenu_item_delete:
                // delete clicked
                // dialog : are you sure you want to delete this item?
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setIcon(R.drawable.ic_delete_black)
                        .setTitle(R.string.dialogTitle_deleteItem)
                        .setMessage(R.string.dialogMessage_deleteItem)
                        // dialog yes :
                        .setPositiveButton(R.string.dialogPositiveButton_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // delete plant
                                database.open();
                                Plant deletedPlant = database.getPlantById(plantId);
                                database.deletePlant(deletedPlant);
                                database.close();

                                // set intent extra and return to main activity
                                Intent deleteIntent = new Intent();
                                deleteIntent.putExtra(PLANT_EXTRA_KEY, deletedPlant );
                                setResult(DELETE_RESULT_CODE, deleteIntent);

                                finish();
                            }
                        })
                        // dialog no :
                        .setNegativeButton(R.string.dialogNegativeButton_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // cancel dialog
                                dialogInterface.cancel();
                            }
                        }).create();

                dialogBuilder.show();

                return true;
        }

        return false;
    }

    // on back pressed actions
    @Override
    public void onBackPressed() {
        String plantName, plantLocation, plantDescription, plantImageName, plantImagePath;

        // get fields content
        if(plantName_et.getText() != null){
            plantName = plantName_et.getText().toString();
        } else{plantName = "";}

        if(plantLocation_et.getText() != null){
            plantLocation = plantLocation_et.getText().toString();
        } else{plantLocation = "";}

        if(plantDescription_et.getText() != null){
            plantDescription = plantDescription_et.getText().toString();
        } else{plantDescription = "";}

        plantImageName = imageName;
        plantImagePath = imagePath;


        // combine content in Plant object
        Plant newPlant = new Plant(plantId, plantName, plantLocation, plantDescription, plantImageName, plantImagePath);

        // if editing item
        if(plantId > -1){
            // get plant item before editing fields
            database.open();
            Plant oldPlant = database.getPlantById(plantId);
            database.close();

            // if fields are enabled and their contents are not the same with the item plant values (user changed fields content)
            if(areFieldsEnabled() && (!oldPlant.getName().equals(newPlant.getName()) || !oldPlant.getLocation().equals(newPlant.getLocation()) || !oldPlant.getDescription().equals(newPlant.getDescription()) || imageChanged ) ){
                // dialog : are you sure you do not want to modify this item?
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setIcon(R.drawable.ic_back_black)
                        .setTitle(R.string.dialogTitle_backTitle)
                        .setMessage(R.string.dialogMessage_saveChanges)
                        // dialog yes:
                        .setPositiveButton(R.string.dialogPositiveButton_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // if no fields are empty
                                if(!plantName.isEmpty() && !plantLocation.isEmpty() && !plantDescription.isEmpty()){
                                    // update plant item
                                    database.open();
                                    database.updatePlant(newPlant);
                                    database.close();

                                    Intent updateIntent = new Intent();
                                    updateIntent.putExtra( PLANT_EXTRA_KEY ,oldPlant);
                                    setResult(UPDATE_RESULT_CODE, updateIntent);
                                    DetailsActivity.super.onBackPressed();
                                }else{ // if some fields are empty
                                    dialogInterface.cancel();
                                    Toast.makeText(DetailsActivity.this, R.string.toastMessage_fieldsEmpty, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        // dialog no:
                        .setNegativeButton(R.string.dialogNegativeButton_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // return to main activity
                                setResult(NULL_RESULT_CODE, null);
                                DetailsActivity.super.onBackPressed();
                            }
                        })
                        // dialog cancel:
                        .setNeutralButton(R.string.dialogNeutralButton_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //cancel dialog
                                dialogInterface.cancel();
                            }
                        })
                        .create();

                dialogBuilder.show();
            }else{ // if fields content did not changed
                super.onBackPressed();
            }

        }else if(plantId == -1){ // if adding item
            // if fields are not empty and some fields are not empty
            if(areFieldsEnabled() && (!newPlant.getName().isEmpty() || !newPlant.getLocation().isEmpty() || !newPlant.getDescription().isEmpty()) ){
                // dialog : do you want to add this item?
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setIcon(R.drawable.ic_back_black)
                        .setTitle(R.string.dialogTitle_backTitle)
                        .setMessage(R.string.dialogMessage_addItem)
                        // dialog yes :
                        .setPositiveButton(R.string.dialogPositiveButton_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(!plantName.isEmpty() && !plantLocation.isEmpty() && !plantDescription.isEmpty()){
                                    // insert plant item
                                    database.open();
                                    database.insertPlant(newPlant);
                                    database.close();

                                    Intent insertIntent = new Intent();
                                    insertIntent.putExtra(PLANT_EXTRA_KEY, newPlant);
                                    setResult(INSERT_RESULT_CODE, insertIntent);
                                    DetailsActivity.super.onBackPressed();
                                }else{ // if some fields are empty
                                    dialogInterface.cancel();
                                    Toast.makeText(DetailsActivity.this, R.string.toastMessage_fieldsEmpty, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        // dialog no
                        .setNegativeButton(R.string.dialogNegativeButton_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // return to main
                                setResult(NULL_RESULT_CODE, null);
                                DetailsActivity.super.onBackPressed();
                            }
                        })
                        // dialog cancel
                        .setNeutralButton(R.string.dialogNeutralButton_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .create();

                dialogBuilder.show();
            }else{ // if fields empty (no data entered)
                super.onBackPressed();
            }

        }else{ // if not adding nor editing (for completeness)
            super.onBackPressed();
        }

    }

    // return whether fields are enabled or not
    private boolean areFieldsEnabled() {
        boolean doneVisible = toolbar.getMenu().findItem(R.id.detailsMenu_item_done).isVisible();
        boolean nameFieldEnabled = plantName_et.isEnabled();
        boolean locationFieldEnabled = plantLocation_et.isEnabled();
        boolean descriptionFieldEnabled = plantDescription_et.isEnabled();
        boolean loadButtonEnabled = loadImage_mBtn.isEnabled();
        boolean deleteButtonEnabled = deleteImage_mBtn.isEnabled();
        return (doneVisible & nameFieldEnabled& locationFieldEnabled & descriptionFieldEnabled & loadButtonEnabled & deleteButtonEnabled);
    }

    // set input fields content
    private void setInputFieldsContent() {
        if(plantId > -1) {

            // view/ edit
            database.open();
            fillPlantToFields(database.getPlantById(plantId));
            database.close();

            disableFields();

        }else if(plantId == -1){

            // add
            enableFields();
            clearFields();
        }
    }

    // fill all input fields
    private void fillPlantToFields(Plant plant)
    {
        plantName_et.setText(plant.getName());
        plantLocation_et.setText(plant.getLocation());
        plantDescription_et.setText(plant.getDescription());

        if(plant.getImageName() != null && plant.getImagePath() != null){

            Bitmap bitmap = Utils.load(this, new ImageHolder(plant.getImageName(), plant.getImagePath()) );

            plantImage_iv.setImageBitmap(bitmap);
        }else{
            plantImage_iv.setImageBitmap(null);
        }

    }

    // enable all input fields
    private void enableFields()
    {
        plantName_et.setEnabled(true);

        plantLocation_et.setEnabled(true);

        plantDescription_et.setEnabled(true);

        loadImage_mBtn.setEnabled(true);
        loadImage_mBtn.setStrokeColorResource(R.color.green_mbutton_stroke_enabled);

        deleteImage_mBtn.setEnabled(true);
        deleteImage_mBtn.setStrokeColorResource(R.color.green_mbutton_stroke_enabled);
    }

    // disable all input fields
    private void disableFields()
    {
        plantName_et.setEnabled(false);

        plantLocation_et.setEnabled(false);

        plantDescription_et.setEnabled(false);

        loadImage_mBtn.setEnabled(false);
        loadImage_mBtn.setStrokeColorResource(R.color.green_mbutton_stroke_disabled);

        deleteImage_mBtn.setEnabled(false);
        deleteImage_mBtn.setStrokeColorResource(R.color.green_mbutton_stroke_disabled);

    }

    // clear all input fields
    private void clearFields()
    {
        plantName_et.setText("");
        plantLocation_et.setText("");
        plantDescription_et.setText("");
        plantImage_iv.setImageBitmap(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MainActivity.PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);
            }
        }

    }
}
package com.example.plantsnote;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private PlantsRVAdapter adapter;
    private RecyclerView rv;

    private DatabaseAccess database;

    private ActivityResultLauncher<Intent> launcher;
    public static final String PLANT_ID_KEY = "plant_id";
    private FloatingActionButton addPlant_fab;

    public static final int PERMISSION_REQUEST_CODE = 1;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inflate XML
        toolbar = findViewById(R.id.mainAct_mToolbar_toolbar);
        rv = findViewById(R.id.mainAct_rv_plantItems);
        addPlant_fab = findViewById(R.id.mainAct_floatingActionButton_addPlant);

        // set toolbar
        setSupportActionBar(toolbar);

        // create database
        database = DatabaseAccess.getInstance(this);

        // set launcher
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {

                resetRVAdapter();

                if(result.getResultCode() == DetailsActivity.INSERT_RESULT_CODE && result.getData() == null){

                    Snackbar snackbar = Snackbar.make(addPlant_fab, R.string.snackBarMessage_itemAdded, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.snackBarButton_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // do nothing
                        }
                    });

                    snackbar.show();
                }

                if(result.getResultCode() == DetailsActivity.UPDATE_RESULT_CODE && result.getData() != null){

                    Snackbar snackbar = Snackbar.make(addPlant_fab, R.string.snackBarMessage_itemModified, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.snackBarButton_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            database.open();
                            // snackBar undo : get extra (Plant object)
                            Plant plant = (Plant) result.getData().getSerializableExtra(DetailsActivity.PLANT_EXTRA_KEY);
                            // update Plant item
                            database.updatePlant(plant);
                            database.close();

                            resetRVAdapter();
                        }
                    });

                    snackbar.show();
                }

                if(result.getResultCode() == DetailsActivity.DELETE_RESULT_CODE && result.getData() != null){

                    // start snackBar
                    Snackbar snackbar = Snackbar.make(addPlant_fab, R.string.snackBarMessage_itemDeleted, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.snackBarButton_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            database.open();
                            //get extra (Plant object)
                            Plant plant = (Plant) result.getData().getSerializableExtra(DetailsActivity.PLANT_EXTRA_KEY);
                            // add deleted Plant item
                            database.insertPlant(plant);
                            database.close();

                            resetRVAdapter();
                        }
                    });

                    snackbar.show();
                }

            }
        });

        // set recycler view
        adapter = new PlantsRVAdapter(new ArrayList<>(), new OnRVItemClickListener() {
            @Override
            public void onItemClick(int plantId) {
                Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
                intent.putExtra(PLANT_ID_KEY, plantId);
                launcher.launch(intent);
            }
        });

        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setHasFixedSize(true);
        resetRVAdapter();

        // set floating action button click listener
        addPlant_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                launcher.launch(intent);
            }
        });

        // list empty toast
        showEmptyToast();

    }

    private void showEmptyToast() {

        database.open();
        if(database.getAllPlants().size() == 0){
            Toast.makeText(getBaseContext(), R.string.toastMessage_listEmpty, Toast.LENGTH_LONG).show();
        }
        database.close();
    }

    private void resetRVAdapter() {

        database.open();
        adapter.setPlants(database.getAllPlants());
        database.close();

        adapter.notifyDataSetChanged();

    }

    // options menu
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.mainMenu_item_search).getActionView();
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                database.open();
                adapter.setPlants(database.getPlantsByName(query));
                database.close();

                adapter.notifyDataSetChanged();


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                database.open();
                adapter.setPlants(database.getPlantsByName(newText));
                database.close();

                adapter.notifyDataSetChanged();

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                database.open();
                adapter.setPlants(database.getAllPlants());
                database.close();

                adapter.notifyDataSetChanged();


                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // click back again
    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.toastMessage_clickBack, Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2200);
    }


}
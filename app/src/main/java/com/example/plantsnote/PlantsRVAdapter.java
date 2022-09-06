package com.example.plantsnote;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantsnote.Utils.ImageHolder;
import com.example.plantsnote.Utils.Utils;

import java.util.ArrayList;

public class PlantsRVAdapter extends RecyclerView.Adapter<PlantsRVAdapter.PlantViewHolder> {

    private ArrayList<Plant> plants;
    private final OnRVItemClickListener listener;

    public PlantsRVAdapter(ArrayList<Plant> plants, OnRVItemClickListener listener) {
        this.plants = plants;
        this.listener = listener;
    }

    public void setPlants(ArrayList<Plant> plants) {
        this.plants = plants;
    }

    @NonNull
    @Override
    public PlantsRVAdapter.PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plants.get(position);
        holder.plantId = plant.getId();
        holder.plantName_tv.setText(plant.getName());
        holder.plantLocation_tv.setText(plant.getLocation());


        if(plant.getImageName() != null && plant.getImagePath() != null){
            Bitmap bitmap = Utils.load(holder.itemView.getContext(), new ImageHolder(plant.getImageName(), plant.getImagePath() ) );
            holder.plantImage_iv.setImageBitmap(bitmap );

        } else{
            holder.plantImage_iv.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }


    class PlantViewHolder extends RecyclerView.ViewHolder{

        int plantId;
        TextView plantName_tv;
        TextView plantLocation_tv;
        ImageView plantImage_iv;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);

            plantName_tv = itemView.findViewById(R.id.itemLayout_tv_plantName);
            plantLocation_tv = itemView.findViewById(R.id.itemLayout_tv_plantLocation);
            plantImage_iv = itemView.findViewById(R.id.itemLayout_iv_plantImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(plantId);
                }
            });
        }
    }
}

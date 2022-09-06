package com.example.plantsnote;

import java.io.Serializable;

public class Plant implements Serializable {
    private int id;
    private String name;
    private String location;
    private String description;
    private String imageName;
    private String imagePath;

    public Plant(int id, String name, String location, String description, String imageName, String imagePath) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageName = imageName;
        this.imagePath = imagePath;
    }

    public Plant(String name, String location, String description, String imageName, String imagePath) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageName = imageName;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

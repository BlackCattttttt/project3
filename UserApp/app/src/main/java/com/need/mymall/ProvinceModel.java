package com.need.mymall;

import java.util.ArrayList;

public class ProvinceModel {

    private int Id;
    private String name;
    private ArrayList<String> districts;

    public ProvinceModel() {
    }

    public ProvinceModel(int id, String name, ArrayList<String> districts) {
        Id = id;
        this.name = name;
        this.districts = districts;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDistricts() {
        return districts;
    }

    public void setDistricts(ArrayList<String> districts) {
        this.districts = districts;
    }
}

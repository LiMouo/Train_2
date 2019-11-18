package com.ziker.train.Utils.Info;

import java.util.ArrayList;
import java.util.List;

public class SubwayQueryInfo {
    int id;
    String name;
    String image;
    List<String> sites = new ArrayList<>();
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

}

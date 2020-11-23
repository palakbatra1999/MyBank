package com.example.mybank.Models;

public class item {

    private String name,image_url,description;
    int _id;

    public item(String name, String image_url, String description, int _id) {
        this.name = name;
        this.image_url = image_url;
        this.description = description;
        this._id = _id;
    }

    public item() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "item{" +
                "name='" + name + '\'' +
                ", image_url='" + image_url + '\'' +
                ", description='" + description + '\'' +
                ", _id=" + _id +
                '}';
    }
}

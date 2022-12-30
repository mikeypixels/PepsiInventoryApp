package com.example.michael.pepsiinventory;

public class Store {

    String store_id,store_name,location, store_type;

    public Store(String store_id, String store_name, String location, String store_type) {
        this.store_id = store_id;
        this.store_name = store_name;
        this.location = location;
        this.store_type = store_type;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStore_type() {
        return store_type;
    }

    public void setStore_type(String store_type) {
        this.store_type = store_type;
    }
}

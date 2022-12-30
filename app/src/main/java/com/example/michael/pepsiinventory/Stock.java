package com.example.michael.pepsiinventory;

public class Stock {

    String id, product_name, product_id, store_name, store_id, store_type, location, available_quantity;

    public Stock(String id, String product_name, String product_id, String store_name, String store_id, String store_type, String location, String available_quantity) {
        this.id = id;
        this.product_name = product_name;
        this.product_id = product_id;
        this.store_name = store_name;
        this.store_id = store_id;
        this.available_quantity = available_quantity;
        this.store_type = store_type;
        this.location = location;
    }

    public String getStore_type() {
        return store_type;
    }

    public void setStore_type(String store_type) {
        this.store_type = store_type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getAvailable_quantity() {
        return available_quantity;
    }

    public void setAvailable_quantity(String available_quantity) {
        this.available_quantity = available_quantity;
    }
}

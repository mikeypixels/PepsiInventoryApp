package com.example.michael.pepsiinventory;

public class Stock {

    String id, store_id, product_id, available_quantity;

    public Stock(String id, String store_id, String product_id, String available_quantity) {
        this.store_id = store_id;
        this.product_id = product_id;
        this.available_quantity = available_quantity;
        this.id = id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getAvailable_quantity() {
        return available_quantity;
    }

    public void setAvailable_quantity(String available_quantity) {
        this.available_quantity = available_quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

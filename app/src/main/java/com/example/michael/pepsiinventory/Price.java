package com.example.michael.pepsiinventory;

public class Price {
    String id, product_id, price;

    public Price(String id, String product_id, String price) {
        this.id = id;
        this.product_id = product_id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

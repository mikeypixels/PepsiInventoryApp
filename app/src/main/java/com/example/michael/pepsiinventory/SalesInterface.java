package com.example.michael.pepsiinventory;

public interface SalesInterface {

    void getPosition(SalesRow salesRow);

    void showSnackBar(double total, String store_id, String store_name);

}

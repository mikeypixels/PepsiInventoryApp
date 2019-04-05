package com.example.michael.pepsiinventory;

public interface ExpenseInterface {

    void getPosition(ExpenseRow expenseRow);

    void showSnackBar(double total, String store_id, String store_name);
}

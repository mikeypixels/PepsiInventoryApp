package com.example.michael.pepsiinventory;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;

public class SalesRow {

    String sn, product_name, quantity, amount, date;

    public SalesRow(String sn, String product_name, String quantity, String amount, String date) {
        this.sn = sn;
        this.product_name = product_name;
        this.quantity = quantity;
        this.amount = amount;
        this.date = date;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

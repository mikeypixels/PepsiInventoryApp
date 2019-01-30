package com.example.michael.pepsiinventory;

public class ExpenseRow {
    String no,expense_name,description,amount,date,store_id;

    public ExpenseRow(String no, String expense_name, String amount, String description, String date, String store_id) {
        this.no = no;
        this.expense_name = expense_name;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.store_id = store_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getExpense_name() {
        return expense_name;
    }

    public void setExpense_name(String expense_name) {
        this.expense_name = expense_name;
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

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
}

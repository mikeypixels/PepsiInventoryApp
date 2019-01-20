package com.example.michael.pepsiinventory;

public class IntChecker {
    private boolean checker = false;
    public boolean Checker(String string)

    {
        try {
            Integer.parseInt(string);
            checker = true;
        }catch (Exception e){
            checker = false;
            e.printStackTrace();
        }
        return checker;
    }
}

package com.example.michael.pepsiinventory;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

//    private static final String TAG = "Expenses";

    private static final String TAG = ExpenseFragment.class.getSimpleName();

    EditText name,amount,description,datepicker;
    TextView action_bar;
    Button send1;
    IntChecker intChecker;
    final Calendar myCalendar = Calendar.getInstance();

    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        name = view.findViewById(R.id.name);
        amount = view.findViewById(R.id.amount);
        description = view.findViewById(R.id.description);
        datepicker = view.findViewById(R.id.datepicker);
        send1 = view.findViewById(R.id.send1);
        action_bar = view.findViewById(R.id.action_bar);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(container.getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        intChecker = new IntChecker();

        send1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onReceiving: " + datepicker.getText().toString());
                    if (amount.getText().toString().isEmpty() || name.getText().toString().isEmpty() || description.getText().toString().isEmpty() || datepicker.getText().toString().isEmpty()) {
                        action_bar.setText("please fill all fields");
                    } else {
                        if (intChecker.Checker(amount.getText().toString())) {
                            action_bar.setText("");
                        } else {
                            action_bar.setText("amount should be in number format");
                        }
                    }

                }

        });

        return view;
    }

    private void updateLabel(){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        datepicker.setText(sdf.format(myCalendar.getTime()));
    }

}

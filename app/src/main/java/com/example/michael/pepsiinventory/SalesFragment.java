package com.example.michael.pepsiinventory;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "Sales";

    DatePicker datePicker;
    IntChecker intChecker;
    EditText datepicker,quantity_txt;
    TextView textView;
    Button send1;
    final Calendar myCalendar = Calendar.getInstance();

    private static final String TAG_HOME = "Sales";

    public SalesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        datepicker = view.findViewById(R.id.datepicker);
        quantity_txt = view.findViewById(R.id.quantity_txt);
        send1 = view.findViewById(R.id.send1);
        textView = view.findViewById(R.id.action0);
        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.stores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
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
                if(quantity_txt.getText().toString().isEmpty()||datepicker.getText().toString().isEmpty()){

                    textView.setText("please fill all fields");
                }else{
                    if(intChecker.Checker(quantity_txt.getText().toString())){
                        textView.setText("");
                    }else{
                        textView.setText("quantity should be in number format");
                    }
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void updateLabel(){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        datepicker.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

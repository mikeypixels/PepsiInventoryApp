package com.example.michael.pepsiinventory;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment {

    RadioButton malebtn,femalebtn;
    String gender;
    Button button;
    EditText f_name,l_name;
    TextView txt;
    Spinner spinner, store_spinner;

    public AddUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        malebtn = view.findViewById(R.id.male_rbtn);
        femalebtn = view.findViewById(R.id.female_rbtn);
        button = view.findViewById(R.id.send1);
        f_name = view.findViewById(R.id.first_name);
        l_name = view.findViewById(R.id.last_name);
        txt = view.findViewById(R.id.action_txt);
        spinner = view.findViewById(R.id.spinner);
        store_spinner = view.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.role, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);

        spinner.setAdapter(adapter);

        malebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                femalebtn.setChecked(false);
                gender = "male";
            }
        });

        femalebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                malebtn.setChecked(false);
                gender = "female";
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(f_name.getText().toString().isEmpty()||l_name.getText().toString().isEmpty()){
                    txt.setText("please fill all fields!");
                }
                else{
                    if(!malebtn.isChecked()&&!femalebtn.isChecked())
                        txt.setText("please choose gender!");
                    else{
                        txt.setText("");
                    }
                }
            }
        });

        return view;
    }

}

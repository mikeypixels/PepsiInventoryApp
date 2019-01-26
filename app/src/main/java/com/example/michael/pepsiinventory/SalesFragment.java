package com.example.michael.pepsiinventory;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
    String sales_url, quantity, date_txt;

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

        sales_url = getString(R.string.serve_url) + "add_sales.php";

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.stores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);

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
                    textView.setText("please fill all fields!");
                }else{
                    if(intChecker.Checker(quantity_txt.getText().toString())){
                        textView.setText("");
                        if (isOnline()) {
                            new SalesFragment.AddSalesTask(getContext()).execute(quantity, date_txt);
                        } else {
                            Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        textView.setText("quantity should be in number format!");
                    }
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public class AddSalesTask extends AsyncTask<String, Void, String>{

        ProgressDialog dialog;
        Context context;

        public AddSalesTask(Context ctx){
            this.context = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            String sale_quantity = strings[0];
            String sale_date = strings[1];

            try {
                URL url = new URL(sales_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("quantity", "UTF-8") + "=" + URLEncoder.encode(sale_quantity, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(sale_date, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response = response.concat(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);

            if (result != null)
            {
                if (result.contains("Successful")) {
                    String[] userDetails = result.split("-");
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                } else {
                    Toast.makeText(context, "Oops... Wrong username or password", Toast.LENGTH_LONG).show();
                }
            } else
            {
                Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
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

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}

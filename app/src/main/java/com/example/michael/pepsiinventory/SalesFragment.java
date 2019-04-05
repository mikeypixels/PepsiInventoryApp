package com.example.michael.pepsiinventory;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = SalesFragment.class.getSimpleName();

    IntChecker intChecker;
    EditText datepicker,quantity_txt;
    TextView textView;
    Button send1;
    final Calendar myCalendar = Calendar.getInstance();
    String sales_url,store_id,user_id;
    Spinner store_spinner;
    Spinner spinner;
    ArrayList<Stock> stockArrayList = new ArrayList<>();

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
        spinner = view.findViewById(R.id.spinner);


        sales_url = getString(R.string.serve_url) + "sale/add";

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.products, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);

        spinner.setAdapter(adapter);
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

            double cost;
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

            @Override
            public void onClick(View view) {

                if(!spinner.getSelectedItem().toString().equals("select product")) {
                    if (quantity_txt.getText().toString().isEmpty() || datepicker.getText().toString().isEmpty()) {
                        textView.setText("please fill all fields!");
                    } else {

                        String[] dateArray = datepicker.getText().toString().split("/");
                        String databaseDate = dateArray[2].concat("-" + dateArray[1] + "-" + dateArray[0]);

                        if (intChecker.Checker(quantity_txt.getText().toString())) {
                            textView.setText("");

                            String myFormat = "yyyy-mm-dd";
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                            Log.d(TAG, "OnReceiveSaleDate: " + sdf.format(myCalendar.getTime()));

                                if(spinner.getSelectedItem().toString().equals("Crate")) {
                                    Date date = new Date();
                                    Log.d(TAG,"OnReceive: " + user_id);
                                    cost = Integer.parseInt(quantity_txt.getText().toString())*9800;

                                    Log.d(TAG, "OnReceiveDate: " + databaseDate);
                                    if(isOnline())
                                        new AddSalesTask(getContext()).execute("1",preferences.getString("store_id", ""),quantity_txt.getText().toString(),String.valueOf(cost), databaseDate,preferences.getString("user_id",""));
                                    else
                                        Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                }else if(spinner.getSelectedItem().toString().equals("Full shell")){
                                    Log.d(TAG,"OnReceive: " + user_id);
                                    cost = Integer.parseInt(quantity_txt.getText().toString())*19800;
                                    Log.d(TAG, "OnReceiveDate: " + sdf.format(myCalendar.getTime()));
                                    if(isOnline())
                                        new AddSalesTask(getContext()).execute("2",preferences.getString("store_id", ""),quantity_txt.getText().toString(),String.valueOf(cost), databaseDate,preferences.getString("user_id",""));
                                    else
                                        Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                }
                                else if(spinner.getSelectedItem().toString().equals("Bottle")){

                                    cost = Integer.parseInt(quantity_txt.getText().toString())*300;
                                    Log.d(TAG, "OnReceiveDate: " + sdf.format(myCalendar.getTime()));
                                    if(isOnline())
                                        new AddSalesTask(getContext()).execute("3",preferences.getString("store_id", ""),quantity_txt.getText().toString(),String.valueOf(cost), databaseDate,preferences.getString("user_id",""));
                                    else
                                        Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                }
                                else if(spinner.getSelectedItem().toString().equals("Takeaway")){

                                    cost = Integer.parseInt(quantity_txt.getText().toString())*9000;
                                    if(isOnline())
                                        new AddSalesTask(getContext()).execute("4",preferences.getString("store_id", ""),quantity_txt.getText().toString(),String.valueOf(cost), databaseDate,preferences.getString("user_id",""));
                                    else
                                        Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                textView.setText("quantity should be a number!");
                            }
                    }
                }else{
                    textView.setText("please select product!");
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
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String sale_product_id = strings[0];
            String sale_store_id = strings[1];
            String sale_quantity = strings[2];
            String sale_cost = strings[3];
            String sale_date = strings[4];
            String sale_user_id = strings[5];

            Log.d(TAG,"doInBackground: " + sale_user_id);

            try {
                URL url = new URL(sales_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(sale_product_id, "UTF-8") + "&" +
                        URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(sale_store_id, "UTF-8")+ "&" +
                        URLEncoder.encode("quantity", "UTF-8") + "=" + URLEncoder.encode(sale_quantity, "UTF-8")+ "&" +
                        URLEncoder.encode("cost", "UTF-8") + "=" + URLEncoder.encode(sale_cost, "UTF-8")+ "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(sale_date, "UTF-8")+ "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(sale_user_id, "UTF-8");
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
                if (result.contains("Added")) {
                    String[] userDetails = result.split("-");
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    quantity_txt.setText("");
                    datepicker.setText("");
                    spinner.setSelection(0);
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                } else {
                    if(this.dialog != null)
                        dialog.dismiss();
                    Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else
            {
                if(this.dialog != null)
                    dialog.dismiss();
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

    public void getStoreUserId(String store_id,String user_id){
        String TAG = SalesFragment.class.getSimpleName();
        Log.d(TAG,"OnReceive: " + user_id);
        this.store_id = store_id;
        this.user_id = user_id;
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

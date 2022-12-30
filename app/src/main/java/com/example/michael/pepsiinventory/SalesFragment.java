package com.example.michael.pepsiinventory;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = SalesFragment.class.getSimpleName();

    IntChecker intChecker;
    EditText datepicker, quantity_txt;
    TextView textView;
    Button send1;
    final Calendar myCalendar = Calendar.getInstance();
    String sales_url, store_id, user_id, store_url;
    Spinner store_spinner;
    Spinner spinner;
    ArrayList<Price> priceRowArrayList = new ArrayList<>();
    ArrayList<Double> price = new ArrayList<>();
    ArrayList<Store> stores = new ArrayList<>();

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
        store_url = getString(R.string.serve_url) + "stores";

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

        if (isOnline()) {
            new StoreLoadingTask(getContext()).execute();
        } else
            Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "OnReceiveID: " + preferences.getString("store_id", ""));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(container.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        intChecker = new IntChecker();

        if (isOnline())
            new PriceLoadingTask(getContext()).execute();
        else
            Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

        send1.setOnClickListener(new View.OnClickListener() {

            double cost;

            @Override
            public void onClick(View view) {

                if (!spinner.getSelectedItem().toString().equals("select product")) {
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

                            for (int i = 0; i < priceRowArrayList.size(); i++) {
                                price.add(Double.parseDouble(priceRowArrayList.get(i).getPrice()));
                            }

                            if (spinner.getSelectedItem().toString().equals("Crate")) {
                                Date date = new Date();
                                Log.d(TAG, "OnReceive: " + user_id);
                                cost = Integer.parseInt(quantity_txt.getText().toString()) * price.get(0);

                                Log.d(TAG, "OnReceiveDate: " + databaseDate);
                                if (isOnline())
                                    new AddSalesTask(getContext()).execute("1", preferences.getString("store_id", ""), quantity_txt.getText().toString(), String.valueOf(cost), databaseDate, preferences.getString("user_id", ""));
                                else
                                    Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            } else if (spinner.getSelectedItem().toString().equals("Full shell")) {
                                Log.d(TAG, "OnReceive: " + user_id);
                                cost = Integer.parseInt(quantity_txt.getText().toString()) * price.get(1);
                                Log.d(TAG, "OnReceiveDate: " + sdf.format(myCalendar.getTime()));
                                if (isOnline())
                                    new AddSalesTask(getContext()).execute("2", preferences.getString("store_id", ""), quantity_txt.getText().toString(), String.valueOf(cost), databaseDate, preferences.getString("user_id", ""));
                                else
                                    Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            } else if (spinner.getSelectedItem().toString().equals("Bottle")) {

                                cost = Integer.parseInt(quantity_txt.getText().toString()) * price.get(2);
                                Log.d(TAG, "OnReceiveDate: " + sdf.format(myCalendar.getTime()));
                                if (isOnline())
                                    new AddSalesTask(getContext()).execute("3", preferences.getString("store_id", ""), quantity_txt.getText().toString(), String.valueOf(cost), databaseDate, preferences.getString("user_id", ""));
                                else
                                    Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            } else if (spinner.getSelectedItem().toString().equals("Takeaway")) {

                                cost = Integer.parseInt(quantity_txt.getText().toString()) * price.get(3);
                                if (isOnline())
                                    new AddSalesTask(getContext()).execute("4", preferences.getString("store_id", ""), quantity_txt.getText().toString(), String.valueOf(cost), databaseDate, preferences.getString("user_id", ""));
                                else
                                    Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

                            } else if (spinner.getSelectedItem().toString().equals("Maji makubwa")) {

                                cost = Integer.parseInt(quantity_txt.getText().toString()) * price.get(4);
                                if (isOnline())
                                    new AddSalesTask(getContext()).execute("5", preferences.getString("store_id", ""), quantity_txt.getText().toString(), String.valueOf(cost), databaseDate, preferences.getString("user_id", ""));
                                else
                                    Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

                            } else if (spinner.getSelectedItem().toString().equals("Maji madogo")) {

                                cost = Integer.parseInt(quantity_txt.getText().toString()) * price.get(5);
                                if (isOnline()) {
                                    new AddSalesTask(getContext()).execute("6", preferences.getString("store_id", ""), quantity_txt.getText().toString(), String.valueOf(cost), databaseDate, preferences.getString("user_id", ""));
                                } else
                                    Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

                            } else if (spinner.getSelectedItem().toString().equals("Soda")) {

                                cost = Integer.parseInt(quantity_txt.getText().toString()) * price.get(6);
                                if (isOnline()) {
                                    new AddSalesTask(getContext()).execute("7", preferences.getString("store_id", ""), quantity_txt.getText().toString(), String.valueOf(cost), databaseDate, preferences.getString("user_id", ""));
                                } else
                                    Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            textView.setText("quantity should be a number!");
                        }
                    }
                } else {
                    textView.setText("please select product!");
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public class AddSalesTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;

        public AddSalesTask(Context ctx) {
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

            Log.d(TAG, "doInBackground: " + sale_user_id);

            try {
                URL url = new URL(sales_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(sale_product_id, "UTF-8") + "&" +
                        URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(sale_store_id, "UTF-8") + "&" +
                        URLEncoder.encode("quantity", "UTF-8") + "=" + URLEncoder.encode(sale_quantity, "UTF-8") + "&" +
                        URLEncoder.encode("cost", "UTF-8") + "=" + URLEncoder.encode(sale_cost, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(sale_date, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(sale_user_id, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                String response = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response = response.concat(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {
                if (result.contains("Added")) {
                    quantity_txt.setText("");
                    datepicker.setText("");
                    spinner.setSelection(0);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Calendar calendar = Calendar.getInstance();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("sale_date",String.valueOf(calendar.getTimeInMillis()));
                    editor.apply();
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                }
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
            }
            if (this.dialog != null)
                dialog.dismiss();
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        datepicker.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getStoreUserId(String store_id, String user_id) {
        String TAG = SalesFragment.class.getSimpleName();
        Log.d(TAG, "OnReceive: " + user_id);
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

    public class StoreLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public StoreLoadingTask(Context ctx) {
            context = ctx;
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
        protected String doInBackground(Void... voids) {
            HttpHandler httpHandler = new HttpHandler();
            return httpHandler.makeServiceCall(store_url);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stores.add(new Store(jsonArray.getJSONObject(i).getString("store_id"),
                                    jsonArray.getJSONObject(i).getString("store_name"),
                                    jsonArray.getJSONObject(i).getString("location"),
                                    jsonArray.getJSONObject(i).getString("store_type")));
                            Log.d(TAG, "OnReceiveStore: " + stores.get(i).getStore_name());
                        }
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();

                        for (Store store : stores) {
                            if (store.getStore_id().equals(preferences.getString("store_id", "")) && store.getStore_type().equals("soda")) {
                                Log.d(TAG, "OnReceiveName: " + store.getStore_name());
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.products, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
                                spinner.setAdapter(adapter);
                                editor.putString("store_type", store.getStore_type());
                                editor.apply();
                                break;
                            } else if (store.getStore_id().equals(preferences.getString("store_id", "")) && store.getStore_type().equals("water")) {
                                Log.d(TAG, "OnReceiveName: " + store.getStore_name());
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.water_products, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
                                spinner.setAdapter(adapter);
                                editor.putString("store_type", store.getStore_type());
                                editor.apply();
                                break;
                            }
                        }

                    } else {
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }
                        Toast.makeText(context, "Oops... No stores found!", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    if (this.dialog != null)
                        dialog.dismiss();
                    Toast.makeText(context, "Oops... Something went wrong!", Toast.LENGTH_LONG).show();
                }

            } else {
                if (this.dialog != null) {
                    this.dialog.dismiss();
                }
                Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    }

    public class PriceLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public PriceLoadingTask(Context ctx) {
            context = ctx;
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
        protected String doInBackground(Void... voids) {
            String price_url = this.context.getResources().getString(R.string.serve_url) + "/prices";
            HttpHandler httpHandler = new HttpHandler();
            return httpHandler.makeServiceCall(price_url);
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            priceRowArrayList.add(new Price(jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("product_id"),
                                    jsonArray.getJSONObject(i).getString("price")));
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                        }

                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                    } else {
                        if (this.dialog != null)
                            this.dialog.dismiss();

                        Toast.makeText(context, "Prices not found!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Oops... Something went wrong!", Toast.LENGTH_LONG).show();
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                }

            } else {
                if (this.dialog != null) {
                    this.dialog.dismiss();
                }
                Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    }

}

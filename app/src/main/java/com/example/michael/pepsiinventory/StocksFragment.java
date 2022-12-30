package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StocksFragment extends Fragment {

    IntChecker intChecker;
    EditText quantity_txt;
    TextView textView, quantity;
    Button send1;
    Spinner store_spinner;
    ArrayList<String> storesString = new ArrayList<>();
    ArrayList<Store> stores = new ArrayList<>();
    ArrayList<Stock> stocksDetails;
    String stocks_url, add_stock_url,store_id,TAG = StocksFragment.class.getSimpleName();

    public StocksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks, container, false);
        quantity_txt = view.findViewById(R.id.quantity_txt);
        quantity_txt = view.findViewById(R.id.quantity_txt);
        send1 = view.findViewById(R.id.send1);
        textView = view.findViewById(R.id.action0);
        quantity = view.findViewById(R.id.quantity_txt_view);
        final Spinner spinner = view.findViewById(R.id.product_spinner);
        store_spinner = view.findViewById(R.id.store_spinner);

        stocks_url = getString(R.string.serve_url) + "stocks";
        add_stock_url = getString(R.string.serve_url) + "stock/add";

        if (isOnline()) {
            new StocksFragment.StocksLoadingTask(getContext()).execute();
        } else {
            Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        store_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(store_spinner.getSelectedItem().toString().equals("select store")){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.no_product, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
                    spinner.setAdapter(adapter);
                }else{
                    for(Store store: stores){
                        if(store.getStore_name().equals(store_spinner.getSelectedItem())&&store.getStore_type().equals("soda")){
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.products, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
                            spinner.setAdapter(adapter);
                            break;
                        }else if(store.getStore_name().equals(store_spinner.getSelectedItem())&&store.getStore_type().equals("water")){
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.water_products, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
                            spinner.setAdapter(adapter);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!spinner.getSelectedItem().toString().equals("select product")){
                    for(Stock stock: stocksDetails){
                        if(stock.getStore_name().equals(store_spinner.getSelectedItem())&&stock.getProduct_name().equals(spinner.getSelectedItem())){
                            quantity.setText("Item quantity in stock: " + stock.getAvailable_quantity());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        intChecker = new IntChecker();

        send1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(!store_spinner.getSelectedItem().toString().equals("select store")) {
                    if (quantity_txt.getText().toString().isEmpty()) {
                        textView.setText("please fill all fields!");
                    } else {
                        if(!spinner.getSelectedItem().toString().equals("select product")) {
                            if (intChecker.Checker(quantity_txt.getText().toString())) {

                                for (Store store : stores) {
                                    if (store_spinner.getSelectedItem().toString().equals(store.getStore_name()))
                                        store_id = store.getStore_id();
                                }

                                textView.setText("");

                                String myFormat = "yyyy-mm-dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                                if (isOnline()) {
                                    if (spinner.getSelectedItem().toString().equals("Crate")) {
                                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + quantity_txt.getText().toString());
                                        new StocksFragment.UpdateStocksTask(getContext()).execute("1", store_id, quantity_txt.getText().toString());
                                    } else if (spinner.getSelectedItem().toString().equals("Full shell")) {
                                        new StocksFragment.UpdateStocksTask(getContext()).execute("2", store_id, quantity_txt.getText().toString());
                                    } else if (spinner.getSelectedItem().toString().equals("Bottle")) {
                                        new StocksFragment.UpdateStocksTask(getContext()).execute("3", store_id, quantity_txt.getText().toString());
                                    }else if (spinner.getSelectedItem().toString().equals("Takeaway")){
                                        new StocksFragment.UpdateStocksTask(getContext()).execute("4", store_id, quantity_txt.getText().toString());
                                    }else if (spinner.getSelectedItem().toString().equals("Maji makubwa")){
                                        new StocksFragment.UpdateStocksTask(getContext()).execute("5", store_id, quantity_txt.getText().toString());
                                    }else if (spinner.getSelectedItem().toString().equals("Maji madogo")){
                                        new StocksFragment.UpdateStocksTask(getContext()).execute("6", store_id, quantity_txt.getText().toString());
                                    }else if (spinner.getSelectedItem().toString().equals("Soda")){
                                        new StocksFragment.UpdateStocksTask(getContext()).execute("7", store_id, quantity_txt.getText().toString());
                                    }

                                    new StocksFragment.StocksLoadingTask(getContext()).execute();

                                    quantity_txt.setText("");
                                    textView.setText("");
                                    store_spinner.setSelection(0);
                                    spinner.setSelection(0);
                                } else {
                                    Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                textView.setText("quantity should be in currency format!");
                            }
                        }else{
                            textView.setText("please select product!");
                        }
                    }
                }else{
                    textView.setText("please select store!");
                }

            }
        });

        return view;
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

    public class UpdateStocksTask extends AsyncTask<String, Void, String>{

        ProgressDialog dialog;
        Context context;

        public UpdateStocksTask(Context ctx){
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

            String store_id = strings[1];
            String product_id = strings[0];
            String available_quantity = strings[2];


            try {
                URL url = new URL(add_stock_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(product_id, "UTF-8") + "&" +
                        URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(store_id, "UTF-8")+ "&" +
                        URLEncoder.encode("available_quantity", "UTF-8") + "=" + URLEncoder.encode(available_quantity, "UTF-8");
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
                    quantity_txt.setText("");
                    store_spinner.setSelection(0);
                    quantity.setText("Item quantity in stock: -");
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                }
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            } else
            {
                Toast.makeText(context, "Oops... something went wrong!", Toast.LENGTH_LONG).show();
            }
            if (this.dialog != null) {
                this.dialog.dismiss();
            }
        }
    }

    public class StocksLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public StocksLoadingTask(Context ctx) {
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
            return httpHandler.makeServiceCall(stocks_url);
        }

        @Override
        protected void onPostExecute(String result) {

            storesString.add("select store");

            stocksDetails = new ArrayList<>();

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if(!storesString.contains(jsonArray.getJSONObject(i).getString("store_name"))){
                                storesString.add(jsonArray.getJSONObject(i).getString("store_name"));
                                stores.add(new Store(jsonArray.getJSONObject(i).getString("store_id"),
                                        jsonArray.getJSONObject(i).getString("store_name"),
                                        jsonArray.getJSONObject(i).getString("location"),
                                        jsonArray.getJSONObject(i).getString("store_type")));
                            }

                            stocksDetails.add(new Stock(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("product_name"),jsonArray.getJSONObject(i).getString("product_id"), jsonArray.getJSONObject(i).getString("store_name"), jsonArray.getJSONObject(i).getString("store_id"), jsonArray.getJSONObject(i).getString("store_type"), jsonArray.getJSONObject(i).getString("location"), jsonArray.getJSONObject(i).getString("available_quantity")));
                        }
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, storesString);
                        store_spinner.setAdapter(adapter);

                        store_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);

                    } else {
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }
                        Toast.makeText(context, "Oops... No stores found!", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    if(this.dialog != null)
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
}
package com.example.michael.pepsiinventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class StocksListTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView textView;
    ImageView imageView;
    androidx.appcompat.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Spinner spinner;
    CoordinatorLayout coordinatorLayout;
    String intentFragment, stocks_url, store_id;
    ArrayList<String> storesString = new ArrayList<>();
    ArrayList<Stock> stocksDetails = new ArrayList<>();
    ArrayList<Stock> stocksRowDetails = new ArrayList<>();
    ArrayList<Stock> stocksSearchList = new ArrayList<>();
    ArrayList<Store> stores = new ArrayList<>();

    StocksTableAdapter stocksTableAdapter;

    String TAG = StocksListTableActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks_list_table);
        intentFragment = getIntent().getStringExtra("frgToLoad");

        stocks_url = getString(R.string.serve_url) + "stocks";

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        spinner = findViewById(R.id.store_spinner);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

        if(isOnline()){
            new StocksListTableActivity.StocksLoadingTask(StocksListTableActivity.this).execute();
        }else
            Toast.makeText(this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                ((TextView) view).setTextColor(Color.WHITE);

                for (int i = 0; i < stores.size(); i++) {
                    if (spinner.getItemAtPosition(position).toString().equals(stores.get(i).getStore_name())) {
                        store_id = stores.get(i).getStore_id();
                        break;
                    }
                }

                if(store_id != null){
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);

                    if(!stocksDetails.isEmpty())
                        stocksDetails.clear();

                    for (int i = 0; i < stocksRowDetails.size(); i++) {

                        if (store_id.equals(stocksRowDetails.get(i).getStore_id())) {
                            stocksDetails.add(stocksRowDetails.get(i));
                            Log.d(TAG, "onPostReceive: " + store_id);
                        }

                    }

                    stocksTableAdapter = new StocksTableAdapter(StocksListTableActivity.this, stocksDetails, store_id);
                    recyclerView.setAdapter(stocksTableAdapter);
                    stocksTableAdapter.notifyDataSetChanged();
                }else{
                    imageView.setVisibility(View.VISIBLE);
                    textView.setText("Please select store!");
                    textView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_items_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!stocksSearchList.isEmpty())
                    stocksSearchList.clear();

                for(int i = 0; i < stocksDetails.size(); i++){
                    if(stocksDetails.get(i).getProduct_name().toLowerCase().contains(query.toLowerCase())||stocksDetails.get(i).getStore_name().toLowerCase().contains(query.toLowerCase())||
                            stocksDetails.get(i).getAvailable_quantity().toLowerCase().contains(query.toLowerCase())){
                        stocksSearchList.add(stocksDetails.get(i));
                    }
                }

                stocksTableAdapter = new StocksTableAdapter(StocksListTableActivity.this, stocksSearchList, store_id);
                recyclerView.setAdapter(stocksTableAdapter);
                stocksTableAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!stocksSearchList.isEmpty())
                    stocksSearchList.clear();

                for(int i = 0; i < stocksDetails.size(); i++){
                    if(stocksDetails.get(i).getProduct_name().toLowerCase().contains(newText.toLowerCase())||stocksDetails.get(i).getStore_name().toLowerCase().contains(newText.toLowerCase())||
                            stocksDetails.get(i).getAvailable_quantity().toLowerCase().contains(newText.toLowerCase())){
                        stocksSearchList.add(stocksDetails.get(i));
                    }
                }

                stocksTableAdapter = new StocksTableAdapter(StocksListTableActivity.this, stocksSearchList, store_id);
                recyclerView.setAdapter(stocksTableAdapter);
                stocksTableAdapter.notifyDataSetChanged();

                return false;
            }
        });

        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StocksListTableActivity.this, AdminActivity.class);
        intent.putExtra("frgToLoad", intentFragment);
        startActivity(intent);
    }

    protected boolean isOnline() {
        String TAG = LoginActivity.class.getSimpleName();
        ConnectivityManager cm = (ConnectivityManager) StocksListTableActivity.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        Log.d(TAG, "OnReceiveNetInfo: " + netInfo.getExtraInfo());
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
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

                            stocksRowDetails.add(new Stock(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("product_name"),jsonArray.getJSONObject(i).getString("product_id"), jsonArray.getJSONObject(i).getString("store_name"), jsonArray.getJSONObject(i).getString("store_id"), jsonArray.getJSONObject(i).getString("store_type"), jsonArray.getJSONObject(i).getString("location"), jsonArray.getJSONObject(i).getString("available_quantity")));
                        }
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(StocksListTableActivity.this, android.R.layout.simple_spinner_dropdown_item, storesString);
                        spinner.setAdapter(adapter);

                        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

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
package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.ArrayList;

public class ExpenseTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ExpenseTableAdapter expenseTableAdapter;
    TableLayout tableLayout;
    TableRow tableRow;
    TextView sn, product_name, quantity, amount, date, textView;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment, expense_url, expense_update_url, user_id, store_id;
    ArrayList<ExpenseRow> expenseRowArrayList = new ArrayList<>();
    ArrayList<ExpenseRow> expenseRows = new ArrayList<>();
    ArrayList<ExpenseRow> expenseArrayList = new ArrayList<>();
    String TAG = ExpenseTableActivity.class.getSimpleName();
    Snackbar snackbar;
    double total = 0;
    CoordinatorLayout coordinatorLayout;

    public ExpenseTableActivity() {
        //required empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_table);
        intentFragment = getIntent().getStringExtra("frgToLoad");

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        textView = findViewById(R.id.textView);

        expense_url = getString(R.string.serve_url) + "expenses.php";

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

        new ExpenseLoadingTask(ExpenseTableActivity.this).execute();

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

                if(!expenseRows.isEmpty())
                    expenseRows.clear();

                for(int i = 0; i < expenseArrayList.size(); i++){
                    if(expenseArrayList.get(i).getNo().toLowerCase().contains(query.toLowerCase())||expenseArrayList.get(i).getExpense_name().toLowerCase().contains(query.toLowerCase())||
                            expenseArrayList.get(i).getAmount().toLowerCase().contains(query.toLowerCase())||expenseArrayList.get(i).getDate().toLowerCase().contains(query.toLowerCase())){
                        expenseRows.add(expenseArrayList.get(i));
                        total = total + Double.parseDouble(expenseArrayList.get(i).getAmount());
                    }
                }

                expenseTableAdapter = new ExpenseTableAdapter(ExpenseTableActivity.this,expenseRows);
                recyclerView.setAdapter(expenseTableAdapter);
                expenseTableAdapter.notifyDataSetChanged();

                NumberFormat formatter = new DecimalFormat("#,###");
                String formattedNumber = formatter.format(total);
                snackbar = Snackbar
                        .make(coordinatorLayout, "Total expenses: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                total = 0;

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!expenseRows.isEmpty())
                    expenseRows.clear();

                for(int i = 0; i < expenseArrayList.size(); i++){
                    if(expenseArrayList.get(i).getNo().toLowerCase().contains(newText.toLowerCase())||expenseArrayList.get(i).getExpense_name().toLowerCase().contains(newText.toLowerCase())||
                            expenseArrayList.get(i).getAmount().toLowerCase().contains(newText.toLowerCase())||expenseArrayList.get(i).getDate().toLowerCase().contains(newText.toLowerCase())){
                        expenseRows.add(expenseArrayList.get(i));
                        total = total + Double.parseDouble(expenseArrayList.get(i).getAmount());
                    }
                }

                expenseTableAdapter = new ExpenseTableAdapter(ExpenseTableActivity.this,expenseRows);
                recyclerView.setAdapter(expenseTableAdapter);
                expenseTableAdapter.notifyDataSetChanged();

                NumberFormat formatter = new DecimalFormat("#,###");
                String formattedNumber = formatter.format(total);
                snackbar = Snackbar
                        .make(coordinatorLayout, "Total expenses: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                total = 0;

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

                return false;

            }


        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();

        if (item.getItemId() == R.id.action_logout) {
            Intent intent = new Intent(ExpenseTableActivity.this,LoginActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "succesfully logged out!", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ExpenseTableActivity.this, MainActivity.class);
        intent.putExtra("frgToLoad", intentFragment);
        startActivity(intent);
    }

    public class ExpenseLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = ExpenseTableActivity.class.getSimpleName();
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public ExpenseLoadingTask(Context ctx) {
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
            return httpHandler.makeServiceCall(expense_url);
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("expenses");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            expenseRowArrayList.add(new ExpenseRow(jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("name"),
                                    jsonArray.getJSONObject(i).getString("amount"),
                                    jsonArray.getJSONObject(i).getString("description"),
                                    jsonArray.getJSONObject(i).getString("date"),
                                    jsonArray.getJSONObject(i).getString("store_id")));
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                        }

                        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                        for (int i = 0; i < expenseRowArrayList.size(); i++) {
                            if (myPrefs.getString("store_id", "").equals(expenseRowArrayList.get(i).getStore_id())) {
                                expenseArrayList.add(expenseRowArrayList.get(i));
                                total = total + Double.parseDouble(expenseRowArrayList.get(i).getAmount());
                            }
                        }

                        if (expenseArrayList.isEmpty())
                            textView.setVisibility(View.VISIBLE);
                        else
                            textView.setVisibility(View.INVISIBLE);

                        expenseTableAdapter = new ExpenseTableAdapter(ExpenseTableActivity.this,expenseArrayList);
                        recyclerView.setAdapter(expenseTableAdapter);

                        NumberFormat formatter = new DecimalFormat("#,###");
                        String formattedNumber = formatter.format(total);
                        snackbar = Snackbar
                                .make(coordinatorLayout, "Total expenses: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snackbar.dismiss();
                                    }
                                });
                        total = 0;

                        // Changing message text color
                        snackbar.setActionTextColor(Color.RED);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();

                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                    } else {
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }
                        Toast.makeText(context, "Oops... No stores found!", Toast.LENGTH_LONG).show();
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

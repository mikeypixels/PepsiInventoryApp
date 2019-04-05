package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminExpenseTableActivity extends AppCompatActivity implements ExpenseInterface {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdminExpenseTableAdapter adminExpenseTableAdapter;
    TableRow tableRow;
    TextView sn, product_name, quantity, amount, date, textView;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment, expense_url, store_url;
    ArrayList<ExpenseRow> expenseRowArrayList = new ArrayList<>();
    ArrayList<ExpenseRow> expenseArrayList = new ArrayList<>();
    ArrayList<ExpenseRow> expenseRows = new ArrayList<>();
    ArrayList<Store> storeRowArrayList = new ArrayList<>();
    Spinner spinner;
    ArrayList<String> storeString = new ArrayList<>();
    String store_id;
    String TAG = AdminExpenseTableActivity.class.getSimpleName();
    double total = 0, overall_total = 0;
    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;
    SharedPreferences preferences;
    ImageView imageView;

    public AdminExpenseTableActivity() {
        //required empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_expense_table_activity);
        intentFragment = getIntent().getStringExtra("frgToLoad");

        expense_url = getString(R.string.serve_url) + "expenses";
        store_url = getString(R.string.serve_url) + "stores";

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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

        if(isOnline()){
            new StoreLoadingTask(AdminExpenseTableActivity.this).execute();
            new ExpenseLoadingTask(AdminExpenseTableActivity.this).execute();
        }else
            Toast.makeText(this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                preferences = PreferenceManager.getDefaultSharedPreferences(AdminExpenseTableActivity.this);

                ((TextView) view).setTextColor(Color.WHITE);

                for (int i = 0; i < storeRowArrayList.size(); i++) {
                    if (spinner.getItemAtPosition(position).toString().equals(storeRowArrayList.get(i).getStore_name())) {
                        store_id = storeRowArrayList.get(i).getStore_id();
                        break;
                    } else {
                        store_id = "0";
                    }
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("id_store",store_id);
                editor.putString("store_name",spinner.getSelectedItem().toString());
                editor.apply();

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                if(!expenseArrayList.isEmpty())
                    expenseArrayList.clear();

                for (int i = 0; i < expenseRowArrayList.size(); i++) {

                    if (store_id.equals(expenseRowArrayList.get(i).getStore_id())) {
                        expenseArrayList.add(expenseRowArrayList.get(i));
                        Log.d(TAG, "onPostReceive: " + store_id);
                        if(expenseRowArrayList.get(i).getDate().equals(getDateTime()))
                            total = total + Double.parseDouble(expenseRowArrayList.get(i).getAmount());

                    }

                }

                    Log.d(TAG, "OnReceiveTotal: " + total);

                adminExpenseTableAdapter = new AdminExpenseTableAdapter(AdminExpenseTableActivity.this, expenseArrayList,AdminExpenseTableActivity.this);
                recyclerView.setAdapter(adminExpenseTableAdapter);
                adminExpenseTableAdapter.notifyDataSetChanged();

                if(store_id.equals("0")) {
                    NumberFormat formatter = new DecimalFormat("#,###");
                    String formattedNumber = formatter.format(overall_total);
                    snackbar = Snackbar
                            .make(coordinatorLayout, "Today's total Expenses: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                    imageView.setVisibility(View.VISIBLE);
                    textView.setText("Select Store!");
                    textView.setVisibility(View.VISIBLE);

                }else{
                    NumberFormat formatter = new DecimalFormat("#,###");
                    String formattedNumber = formatter.format(total);
                    snackbar = Snackbar
                            .make(coordinatorLayout, spinner.getSelectedItem().toString() + " Expenses: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                    total = 0;

                    if (expenseArrayList.isEmpty()) {
                        imageView.setVisibility(View.VISIBLE);
                        textView.setText("Oops... No Expenses Found!");
                        textView.setVisibility(View.VISIBLE);
                    }
                    else {
                        imageView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                    }
                }

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public void showSnackBar(double total, String store_id, String store_name){
            NumberFormat formatter = new DecimalFormat("#,###");
            String formattedNumber = formatter.format(total);

            if(store_id.equals("0")) {
                snackbar = Snackbar
                        .make(coordinatorLayout, "Today's total Expenses: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
            }else{
                snackbar = Snackbar
                        .make(coordinatorLayout, store_name + " Expenses: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
            }
        total = 0;

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public class ExpenseLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;

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
            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {
                Log.d(TAG, "onPostExecute: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    Log.d(TAG, "onPostExecute: " + result);

                    for (int i = 0; i < storeRowArrayList.size(); i++) {
                        if (spinner.getSelectedItem().toString().equals(storeRowArrayList.get(i).getStore_name())) {
                            store_id = storeRowArrayList.get(i).getStore_id();
                            break;
                        } else {
                            store_id = "0";
                        }
                    }

                    if (jsonArray.length() > 0) {

                        Log.d(TAG, "onPostExecuteID: " + store_id);

                        for (int i = 0; i < jsonArray.length(); i++) {

//                            if(store_id.equals(jsonArray.getJSONObject(i).getString("store_id"))) {
                            expenseRowArrayList.add(new ExpenseRow(jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("expense_name"),
                                    jsonArray.getJSONObject(i).getString("cost"),
                                    jsonArray.getJSONObject(i).getString("description"),
                                    jsonArray.getJSONObject(i).getString("date"),
                                    jsonArray.getJSONObject(i).getString("store_id")));
                            if(expenseRowArrayList.get(i).getDate().equals(getDateTime()))
                                overall_total = overall_total + Double.parseDouble(jsonArray.getJSONObject(i).getString("cost"));
                            Log.d(TAG, "onPostExecuteExp: " + expenseRowArrayList.get(i).getExpense_name());

                        }

                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                    } else {

                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                        imageView.setVisibility(View.VISIBLE);
                        textView.setText("Oops... No Expenses Found!");
                        textView.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
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

    @Override
    public void getPosition(ExpenseRow expenseRow) {
        int position = 0;
        for(int i = 0; i < expenseRowArrayList.size(); i++){
            if(expenseRowArrayList.get(i).equals(expenseRow)) {
                expenseRowArrayList.remove(expenseRow);
                position = i;
            }
        }

        adminExpenseTableAdapter.notifyItemRemoved(position+1);
        adminExpenseTableAdapter.notifyItemRangeChanged(position+1, expenseRowArrayList.size());
    }

    public class StoreLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;

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
//            Log.d(TAG, "onPostExecute: " + result);

            storeString.add("select store");

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            storeRowArrayList.add(new Store(jsonArray.getJSONObject(i).getString("store_id"),
                                    jsonArray.getJSONObject(i).getString("store_name"),
                                    jsonArray.getJSONObject(i).getString("location")));
                            storeString.add(jsonArray.getJSONObject(i).getString("store_name"));
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminExpenseTableActivity.this, android.R.layout.simple_spinner_dropdown_item, storeString);
                        spinner.setAdapter(adapter);


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

    private String getDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
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
                        expenseRows.add(new ExpenseRow(expenseArrayList.get(i).getNo(),expenseArrayList.get(i).getExpense_name(),expenseArrayList.get(i).getAmount(),expenseArrayList.get(i).getDescription(),
                                expenseArrayList.get(i).getDate(),expenseArrayList.get(i).getStore_id()));
                        total = total + Double.parseDouble(expenseArrayList.get(i).getAmount());
                    }
                }

                adminExpenseTableAdapter = new AdminExpenseTableAdapter(AdminExpenseTableActivity.this,expenseRows,AdminExpenseTableActivity.this);
                recyclerView.setAdapter(adminExpenseTableAdapter);
                adminExpenseTableAdapter.notifyDataSetChanged();

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
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
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
                        expenseRows.add(new ExpenseRow(expenseArrayList.get(i).getNo(),expenseArrayList.get(i).getExpense_name(),expenseArrayList.get(i).getAmount(),expenseArrayList.get(i).getDescription(),
                                expenseArrayList.get(i).getDate(),expenseArrayList.get(i).getStore_id()));
                        total = total + Double.parseDouble(expenseArrayList.get(i).getAmount());
                    }
                }

                adminExpenseTableAdapter = new AdminExpenseTableAdapter(AdminExpenseTableActivity.this,expenseRows,AdminExpenseTableActivity.this);
                recyclerView.setAdapter(adminExpenseTableAdapter);
                adminExpenseTableAdapter.notifyDataSetChanged();

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
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        if (item.getItemId() == R.id.action_logout) {
            Intent intent = new Intent(AdminExpenseTableActivity.this,LoginActivity.class);
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
        Intent intent = new Intent(AdminExpenseTableActivity.this, MainActivity.class);
        intent.putExtra("frgToLoad", intentFragment);
        startActivity(intent);
    }

    protected boolean isOnline() {
        String TAG = LoginActivity.class.getSimpleName();
        ConnectivityManager cm = (ConnectivityManager) AdminExpenseTableActivity.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        Log.d(TAG, "OnReceiveNetInfo: " + netInfo.getExtraInfo());
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}

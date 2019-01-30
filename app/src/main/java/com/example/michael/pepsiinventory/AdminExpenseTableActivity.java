package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.michael.pepsiinventory.MainActivity.navItemIndex;

public class AdminExpenseTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdminExpenseTableAdapter adminExpenseTableAdapter;
    TableLayout tableLayout;
    TableRow tableRow;
    TextView sn, product_name, quantity, amount, date;
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

    public AdminExpenseTableActivity() {
        //required empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_expense_table_activity);
        intentFragment = getIntent().getStringExtra("frgToLoad");

        expense_url = getString(R.string.serve_url) + "expenses.php";
        store_url = getString(R.string.serve_url) + "stores.php";

        spinner = findViewById(R.id.store_spinner);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
//        collapsingToolbarLayout.setTitle("Sales Table");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ActionBar actionBar = getSupportActionBar();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

        new StoreLoadingTask(AdminExpenseTableActivity.this).execute();
        new ExpenseLoadingTask(AdminExpenseTableActivity.this).execute();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                ((TextView) view).setTextColor(Color.WHITE);

                for (int i = 0; i < storeRowArrayList.size(); i++) {
                    if (spinner.getItemAtPosition(position).toString().equals(storeRowArrayList.get(i).getStore_name())) {
                        store_id = storeRowArrayList.get(i).getStore_id();
                        break;
                    } else {
                        store_id = "0";
                    }
                }

                Toast.makeText(AdminExpenseTableActivity.this, "onPostReceive: " + store_id, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                if(!expenseArrayList.isEmpty())
                    expenseArrayList.clear();

                for (int i = 0; i < expenseRowArrayList.size(); i++)
                    if (store_id.equals(expenseRowArrayList.get(i).getStore_id())) {
                        expenseArrayList.add(expenseRowArrayList.get(i));
                        Log.d(TAG, "onPostReceive: " + store_id);
                    }

                adminExpenseTableAdapter = new AdminExpenseTableAdapter(AdminExpenseTableActivity.this, expenseArrayList);
                recyclerView.setAdapter(adminExpenseTableAdapter);
                adminExpenseTableAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public class ExpenseLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
        ExpenseTableAdapter storeListTableAdapter;
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
            Log.d(TAG, "onPostExecute: " + result);
//            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            if (result != null) {
                Log.d(TAG, "onPostExecute: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("expenses");
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
                                    jsonArray.getJSONObject(i).getString("name"),
                                    jsonArray.getJSONObject(i).getString("amount"),
                                    jsonArray.getJSONObject(i).getString("description"),
                                    jsonArray.getJSONObject(i).getString("date"),
                                    jsonArray.getJSONObject(i).getString("store_id")));
                            Log.d(TAG, "onPostExecuteExp: " + expenseRowArrayList.get(i).getExpense_name());
                            Log.d(TAG, "onPostExecuteExp: " + storeRowArrayList.get(i).getStore_id());
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
//                            }
                        }

//                        adminExpenseTableAdapter = new AdminExpenseTableAdapter(AdminExpenseTableActivity.this,expenseRowArrayList);
//                        recyclerView.setAdapter(adminExpenseTableAdapter);

                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                    } else {
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }
                        Toast.makeText(context, "Oops... No expenses found!", Toast.LENGTH_LONG).show();
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

    public class StoreLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
        StoreListTableAdapter storeListTableAdapter;
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

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
                    JSONArray jsonArray = jsonObject.getJSONArray("stores");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            storeRowArrayList.add(new Store(jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("name"),
                                    jsonArray.getJSONObject(i).getString("location")));
                            storeString.add(jsonArray.getJSONObject(i).getString("name"));
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminExpenseTableActivity.this, android.R.layout.simple_spinner_dropdown_item, storeString);
                        spinner.setAdapter(adapter);

                        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                ((TextView) view).setTextColor(Color.WHITE);
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });


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
                    }
                }

                adminExpenseTableAdapter = new AdminExpenseTableAdapter(AdminExpenseTableActivity.this,expenseRows);
                recyclerView.setAdapter(adminExpenseTableAdapter);
                adminExpenseTableAdapter.notifyDataSetChanged();

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
                    }
                }

                adminExpenseTableAdapter = new AdminExpenseTableAdapter(AdminExpenseTableActivity.this,expenseRows);
                recyclerView.setAdapter(adminExpenseTableAdapter);
                adminExpenseTableAdapter.notifyDataSetChanged();

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminExpenseTableActivity.this, MainActivity.class);
        intent.putExtra("frgToLoad", intentFragment);
        startActivity(intent);
    }
}

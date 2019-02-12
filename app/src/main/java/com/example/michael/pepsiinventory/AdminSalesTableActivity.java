package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class AdminSalesTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdminSalesTableAdapter adminSalesTableAdapter;
    TableLayout tableLayout;
    LinearLayout tableRow;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment, sales_url, store_url;
    MenuItem menuItem;
    MainActivity mainActivity;
    ArrayList<SalesRow> salesRows = new ArrayList<>();
    EditText sn, product_name, quantity, amount, date;
    Spinner spinner;
    ArrayList<SalesRow> salesRowArrayList = new ArrayList<>();
    ArrayList<Store> storeRowArrayList = new ArrayList<>();
    ArrayList<String> storeString = new ArrayList<>();
    ArrayList<SalesRow> salesArrayList = new ArrayList<>();
    String store_id;
    Snackbar snackbar;
    CoordinatorLayout coordinatorLayout;
    double total = 0, overall_total = 0;
    TextView textView;

    private final static String TAG = AdminSalesTableActivity.class.getSimpleName();

    public AdminSalesTableActivity() {
        //required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_sales_table);

        intentFragment = getIntent().getStringExtra("frgToLoad");
        tableRow = findViewById(R.id.tableRow1);
        spinner = findViewById(R.id.store_spinner);

        sales_url = getString(R.string.serve_url) + "sales.php";
        store_url = getString(R.string.serve_url) + "stores.php";
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        textView = findViewById(R.id.textView);

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
//        if(getSupportActionBar()!=null) {
        ActionBar actionBar = getSupportActionBar();
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        recyclerView = findViewById(R.id.recyclerView1);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

//        salesTableAdapter.setOnItemClickListener(new SalesTableAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                salesRows.get(position);
//                Intent intent = new Intent(SalesTableActivity.this,PopUpActivity.class);
//                startActivity(intent);
//            }
//        });

        new SalesLoadingTask(AdminSalesTableActivity.this).execute();
        new StoreLoadingTask(AdminSalesTableActivity.this).execute();

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

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                if (!salesArrayList.isEmpty())
                    salesArrayList.clear();

                for (int i = 0; i < salesRowArrayList.size(); i++)
                    if (store_id.equals(salesRowArrayList.get(i).getStore_id())) {
                        salesArrayList.add(salesRowArrayList.get(i));
//                        Log.d(TAG, "onPostReceiveValue: " + salesArrayList.get(i).getProduct_name());
                        total = total + Double.parseDouble(salesRowArrayList.get(i).getAmount());
                    }

                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                adminSalesTableAdapter = new AdminSalesTableAdapter(AdminSalesTableActivity.this, salesArrayList);
                recyclerView.setAdapter(adminSalesTableAdapter);
                adminSalesTableAdapter.notifyDataSetChanged();

                if (store_id.equals("0")) {
                    NumberFormat formatter = new DecimalFormat("#,###");
                    String formattedNumber = formatter.format(overall_total);
                    snackbar = Snackbar
                            .make(coordinatorLayout, "Total sales: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                    textView.setText("SELECT STORE!");
                    textView.setVisibility(View.VISIBLE);
                } else {
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

                    if(salesArrayList.isEmpty()) {
                        textView.setText("NO SALES TO SHOW!");
                        textView.setVisibility(View.VISIBLE);
                    }
                    else
                        textView.setVisibility(View.INVISIBLE);
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
    }

    public class SalesLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public SalesLoadingTask(Context ctx) {
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
            return httpHandler.makeServiceCall(sales_url);
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("sales");

                    for (int i = 0; i < storeRowArrayList.size(); i++) {
                        if (spinner.getSelectedItem().toString().equals(storeRowArrayList.get(i).getStore_name())) {
                            store_id = storeRowArrayList.get(i).getStore_id();
                            break;
                        } else {
                            store_id = "0";
                        }
                    }

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            salesRowArrayList.add(new SalesRow(jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("product"),
                                    jsonArray.getJSONObject(i).getString("quantity"),
                                    jsonArray.getJSONObject(i).getString("amount"),
                                    jsonArray.getJSONObject(i).getString("date"),
                                    jsonArray.getJSONObject(i).getString("store_id")));
                            overall_total = overall_total + Double.parseDouble(jsonArray.getJSONObject(i).getString("amount"));
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));

                            Log.d(TAG, "onPostExecuteNewValue: " + salesRowArrayList.get(i).getStore_id());
                        }

//                        adminSalesTableAdapter = new AdminSalesTableAdapter(AdminSalesTableActivity.this,salesRowArrayList);
//                        recyclerView.setAdapter(adminSalesTableAdapter);

                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                    } else {
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }
                        Toast.makeText(context, "Oops... No sales found!", Toast.LENGTH_LONG).show();
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminSalesTableActivity.this, android.R.layout.simple_list_item_1, storeString);
                        spinner.setAdapter(adapter);

                        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_items_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!salesRows.isEmpty())
                    salesRows.clear();

                for (int i = 0; i < salesArrayList.size(); i++) {
                    if (salesArrayList.get(i).getSn().toLowerCase().contains(query.toLowerCase()) || salesArrayList.get(i).getProduct_name().toLowerCase().contains(query.toLowerCase()) ||
                            salesArrayList.get(i).getQuantity().toLowerCase().contains(query.toLowerCase()) || salesArrayList.get(i).getAmount().toLowerCase().contains(query.toLowerCase()) ||
                            salesArrayList.get(i).getDate().toLowerCase().contains(query.toLowerCase())) {
                        salesRows.add(new SalesRow(salesArrayList.get(i).getSn(), salesArrayList.get(i).getProduct_name(), salesArrayList.get(i).getQuantity(), salesArrayList.get(i).getAmount(),
                                salesArrayList.get(i).getDate(), salesArrayList.get(i).getStore_id()));
                        total = total + Double.parseDouble(salesArrayList.get(i).getAmount());
                    }
                }

                adminSalesTableAdapter = new AdminSalesTableAdapter(AdminSalesTableActivity.this, salesRows);
                recyclerView.setAdapter(adminSalesTableAdapter);
                adminSalesTableAdapter.notifyDataSetChanged();

                NumberFormat formatter = new DecimalFormat("#,###");
                String formattedNumber = formatter.format(total);
                snackbar = Snackbar
                        .make(coordinatorLayout, "Total sales: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
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

                if (!salesRows.isEmpty())
                    salesRows.clear();

                for (int i = 0; i < salesArrayList.size(); i++) {
                    if (salesArrayList.get(i).getSn().toLowerCase().contains(newText.toLowerCase()) || salesArrayList.get(i).getProduct_name().toLowerCase().contains(newText.toLowerCase()) ||
                            salesArrayList.get(i).getQuantity().toLowerCase().contains(newText.toLowerCase()) || salesArrayList.get(i).getAmount().toLowerCase().contains(newText.toLowerCase()) ||
                            salesArrayList.get(i).getDate().toLowerCase().contains(newText.toLowerCase())) {
                        salesRows.add(new SalesRow(salesArrayList.get(i).getSn(), salesArrayList.get(i).getProduct_name(), salesArrayList.get(i).getQuantity(), salesArrayList.get(i).getAmount(),
                                salesArrayList.get(i).getDate(), salesArrayList.get(i).getStore_id()));
                        total = total + Double.parseDouble(salesArrayList.get(i).getAmount());
                    }
                }

                adminSalesTableAdapter = new AdminSalesTableAdapter(AdminSalesTableActivity.this, salesRows);
                recyclerView.setAdapter(adminSalesTableAdapter);
                adminSalesTableAdapter.notifyDataSetChanged();

                NumberFormat formatter = new DecimalFormat("#,###");
                String formattedNumber = formatter.format(total);
                snackbar = Snackbar
                        .make(coordinatorLayout, "Total sales: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
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

//                adminSalesTableAdapter.filter(newText);

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
        Intent intent = new Intent(AdminSalesTableActivity.this, MainActivity.class);
        intent.putExtra("frgToLoad", intentFragment);
        startActivity(intent);
    }
}

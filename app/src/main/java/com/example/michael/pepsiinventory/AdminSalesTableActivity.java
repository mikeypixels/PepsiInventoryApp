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

import com.example.michael.pepsiinventory.firebaseNotification.MyFirebaseMessagingService;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import java.util.Objects;

public class AdminSalesTableActivity extends AppCompatActivity implements SalesInterface{

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdminSalesTableAdapter adminSalesTableAdapter;
    LinearLayout tableRow;
    androidx.appcompat.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment, sales_url, store_url;
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
    ImageView imageView;
    SharedPreferences preferences;

    private final static String TAG = AdminSalesTableActivity.class.getSimpleName();

    public AdminSalesTableActivity() {
        //required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_sales_table);

        intentFragment = getIntent().getStringExtra("frgToLoad");

        spinner = findViewById(R.id.store_spinner);

        sales_url = getString(R.string.serve_url) + "sales";
        store_url = getString(R.string.serve_url) + "stores";
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
//        collapsingToolbarLayout.setTitle("Sales Table");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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

        if(isOnline()){
            new SalesLoadingTask(AdminSalesTableActivity.this).execute();
            new StoreLoadingTask(AdminSalesTableActivity.this).execute();
        }else
            Toast.makeText(this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                ((TextView) view).setTextColor(Color.WHITE);

                preferences = PreferenceManager.getDefaultSharedPreferences(AdminSalesTableActivity.this);

                SharedPreferences.Editor editor = preferences.edit();

                for (int i = 0; i < storeRowArrayList.size(); i++) {
                    if (spinner.getItemAtPosition(position).toString().equals(storeRowArrayList.get(i).getStore_name())) {
                        store_id = storeRowArrayList.get(i).getStore_id();
//                        typeInterface.getStoreType(storeRowArrayList.get(i).getStore_type());
                        editor.putString("store_type", storeRowArrayList.get(i).getStore_type());
                        break;
                    } else {
                        store_id = "0";
                    }
                }

                editor.putString("id_store",store_id);
                editor.putString("store_name",spinner.getSelectedItem().toString());
                editor.apply();

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                if (!salesArrayList.isEmpty())
                    salesArrayList.clear();

                for (int i = 0; i < salesRowArrayList.size(); i++)
                    if (store_id.equals(salesRowArrayList.get(i).getStore_id())) {
                        salesArrayList.add(salesRowArrayList.get(i));
//                        Log.d(TAG, "onPostReceiveValue: " + salesArrayList.get(i).getProduct_name());
                        if(salesRowArrayList.get(i).getDate().equals(getDateTime()))
                            total = total + Double.parseDouble(salesRowArrayList.get(i).getAmount());
                    }

                Log.d(TAG, "onPostReceive: " + spinner.getSelectedItem().toString());

                adminSalesTableAdapter = new AdminSalesTableAdapter(AdminSalesTableActivity.this, salesArrayList, AdminSalesTableActivity.this);
                recyclerView.setAdapter(adminSalesTableAdapter);

                if (store_id.equals("0")) {
                    NumberFormat formatter = new DecimalFormat("#,###");
                    String formattedNumber = formatter.format(overall_total);
                    snackbar = Snackbar
                            .make(coordinatorLayout, "Today's total sales: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                    imageView.setVisibility(View.VISIBLE);
                    textView.setText("Select Store!");
                    textView.setVisibility(View.VISIBLE);
                } else {
                    NumberFormat formatter = new DecimalFormat("#,###");
                    String formattedNumber = formatter.format(total);
                    snackbar = Snackbar
                            .make(coordinatorLayout, spinner.getSelectedItem().toString() + " Sales: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                    total = 0;

                    if (salesArrayList.isEmpty()) {
                        imageView.setVisibility(View.VISIBLE);
                        textView.setText("Oops... No Sales Found!");
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
                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showSnackBar(double total, String store_id, String store_name) {
        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedNumber = formatter.format(total);

        if(store_id.equals("0")) {
            snackbar = Snackbar
                    .make(coordinatorLayout, "Today's total Sales: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });

        }else{
            snackbar = Snackbar
                    .make(coordinatorLayout, store_name + " Sales: " + formattedNumber + " Tshs", Snackbar.LENGTH_INDEFINITE)
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
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void getPosition(SalesRow salesRow) {
        int position = 0;
        for(int i = 0; i < salesRowArrayList.size(); i++){
            if(salesRowArrayList.get(i).equals(salesRow)) {
                salesRowArrayList.remove(salesRow);
                position = i;
            }
        }
        adminSalesTableAdapter.notifyItemRemoved(position+1);
        adminSalesTableAdapter.notifyItemRangeChanged(position+1, salesRowArrayList.size());
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
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

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
                                    jsonArray.getJSONObject(i).getString("product_id"),
                                    jsonArray.getJSONObject(i).getString("quantity"),
                                    jsonArray.getJSONObject(i).getString("cost"),
                                    jsonArray.getJSONObject(i).getString("date"),
                                    jsonArray.getJSONObject(i).getString("store_id")));
                            if(salesRowArrayList.get(i).getDate().equals(getDateTime()))
                                overall_total = overall_total + Double.parseDouble(jsonArray.getJSONObject(i).getString("cost"));
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

                        imageView.setVisibility(View.VISIBLE);
                        textView.setText("Oops... No Sales Found!");
                        textView.setVisibility(View.VISIBLE);
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
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            storeRowArrayList.add(new Store(jsonArray.getJSONObject(i).getString("store_id"),
                                    jsonArray.getJSONObject(i).getString("store_name"),
                                    jsonArray.getJSONObject(i).getString("location"),
                                    jsonArray.getJSONObject(i).getString("store_type")));
                            storeString.add(jsonArray.getJSONObject(i).getString("store_name"));
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                        }

                        spinner.setPrompt("select store");

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminSalesTableActivity.this, android.R.layout.simple_list_item_1, storeString);
                        spinner.setAdapter(adapter);

                        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

                        String storeName = MyFirebaseMessagingService.Companion.getMsg();

                        Log.d(TAG, "======================================================" + storeName);

                        int i = 0;
                        if(!storeName.equals("")){
                            String[] store_names = storeName.split(" ");
                            for(String store: storeString){
                                if(store.contains(store_names[4])){
                                    Log.d(TAG, "-------------------------------------------" + store_names[4]);
                                    spinner.setSelection(i);
                                    break;
                                }
                                i++;
                            }
                        }

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

                adminSalesTableAdapter = new AdminSalesTableAdapter(AdminSalesTableActivity.this, salesRows, AdminSalesTableActivity.this);
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
                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
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

                adminSalesTableAdapter = new AdminSalesTableAdapter(AdminSalesTableActivity.this, salesRows, AdminSalesTableActivity.this);
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
                TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

//                adminSalesTableAdapter.filter(newText);

                return false;
            }
        });

        return true;
    }

    private String getDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return sdf.format(date);
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

    protected boolean isOnline() {
        String TAG = LoginActivity.class.getSimpleName();
        ConnectivityManager cm = (ConnectivityManager) AdminSalesTableActivity.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        Log.d(TAG, "OnReceiveNetInfo: " + netInfo.getExtraInfo());
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

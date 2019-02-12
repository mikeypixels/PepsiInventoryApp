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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
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

import static com.example.michael.pepsiinventory.MainActivity.navItemIndex;

public class SalesTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SalesTableAdapter salesTableAdapter;
    LinearLayout tableRow;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment,sales_url;
    ArrayList<SalesRow> salesRowArrayList = new ArrayList<>();
    ArrayList<SalesRow> salesArrayList = new ArrayList<>();
    ArrayList<SalesRow> salesRows = new ArrayList<>();
    EditText sn,product_name,quantity,amount,date;
    Snackbar snackbar;
    CoordinatorLayout coordinatorLayout;
    double total = 0;
    TextView textView;

    private final static String TAG = SalesTableActivity.class.getSimpleName();

    public SalesTableActivity(){
        //required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_table);

        intentFragment = getIntent().getStringExtra("frgToLoad");
        tableRow = findViewById(R.id.tableRow1);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        textView = findViewById(R.id.textView);

        sales_url = getString(R.string.serve_url) + "sales.php";

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

        new SalesLoadingTask(SalesTableActivity.this).execute();

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

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            salesRowArrayList.add(new SalesRow(jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("product"),
                                    jsonArray.getJSONObject(i).getString("quantity"),
                                    jsonArray.getJSONObject(i).getString("amount"),
                                    jsonArray.getJSONObject(i).getString("date"),
                                    jsonArray.getJSONObject(i).getString("store_id")));
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                            Log.d(TAG,"OnReceiveSale: " + salesRowArrayList.get(i).getStore_id());
                        }

                        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                        Log.d(TAG,"OnReceivingID: " + myPrefs.getString("store_id",""));

                        for(int i=0; i<salesRowArrayList.size() ; i++){
                            if(myPrefs.getString("store_id","").equals(salesRowArrayList.get(i).getStore_id())){
                                salesArrayList.add(salesRowArrayList.get(i));
                                Log.d(TAG,"OnReceivingIDs: " + myPrefs.getString("store_id","") + " " + salesRowArrayList.get(i).getStore_id());
                                total = total + Double.parseDouble(salesRowArrayList.get(i).getAmount());
                            }
                        }

                        if (salesArrayList.isEmpty())
                            textView.setText("SELECT STORE!");
                        else
                            textView.setVisibility(View.INVISIBLE);

                        salesTableAdapter = new SalesTableAdapter(SalesTableActivity.this,salesArrayList);
                        recyclerView.setAdapter(salesTableAdapter);

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
                    if (salesArrayList.get(i).getSn().toLowerCase().contains(query.toLowerCase())||salesArrayList.get(i).getProduct_name().toLowerCase().contains(query.toLowerCase())||
                            salesArrayList.get(i).getQuantity().toLowerCase().contains(query.toLowerCase())||salesArrayList.get(i).getAmount().toLowerCase().contains(query.toLowerCase())||
                            salesArrayList.get(i).getDate().toLowerCase().contains(query.toLowerCase())) {
                        salesRows.add(salesArrayList.get(i));
                        total = total + Double.parseDouble(salesArrayList.get(i).getAmount());
                    }
                }

                salesTableAdapter = new SalesTableAdapter(SalesTableActivity.this, salesRows);
                recyclerView.setAdapter(salesTableAdapter);
                salesTableAdapter.notifyDataSetChanged();

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
                    if (salesArrayList.get(i).getSn().toLowerCase().contains(newText.toLowerCase())||salesArrayList.get(i).getProduct_name().toLowerCase().contains(newText.toLowerCase())||
                            salesArrayList.get(i).getQuantity().toLowerCase().contains(newText.toLowerCase())||salesArrayList.get(i).getAmount().toLowerCase().contains(newText.toLowerCase())||
                            salesArrayList.get(i).getDate().toLowerCase().contains(newText.toLowerCase())) {
                        salesRows.add(salesArrayList.get(i));
                        total = total + Double.parseDouble(salesArrayList.get(i).getAmount());
                    }
                }

                salesTableAdapter = new SalesTableAdapter(SalesTableActivity.this, salesRows);
                recyclerView.setAdapter(salesTableAdapter);
                salesTableAdapter.notifyDataSetChanged();

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
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();

        if (item.getItemId() == R.id.action_logout) {
            Intent intent = new Intent(SalesTableActivity.this,LoginActivity.class);
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
        Intent intent = new Intent(SalesTableActivity.this,MainActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
    }
}



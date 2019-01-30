package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
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
    ArrayList<SalesRow> salesRows = new ArrayList<>();
    EditText sn,product_name,quantity,amount,date;

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
//        salesRows.add(salesTableAdapter.)

        salesTableAdapter = new SalesTableAdapter(SalesTableActivity.this,salesRowArrayList);
        recyclerView.setAdapter(salesTableAdapter);

//        salesTableAdapter.setOnItemClickListener(new SalesTableAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                salesRows.get(position);
//                Intent intent = new Intent(SalesTableActivity.this,PopUpActivity.class);
//                startActivity(intent);
//            }
//        });

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
                        }

                        salesTableAdapter = new SalesTableAdapter(SalesTableActivity.this,salesRowArrayList);
                        recyclerView.setAdapter(salesTableAdapter);

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

                for (int i = 0; i < salesRowArrayList.size(); i++) {
                    if (salesRowArrayList.get(i).getSn().toLowerCase().contains(query.toLowerCase())||salesRowArrayList.get(i).getProduct_name().toLowerCase().contains(query.toLowerCase())||
                            salesRowArrayList.get(i).getQuantity().toLowerCase().contains(query.toLowerCase())||salesRowArrayList.get(i).getAmount().toLowerCase().contains(query.toLowerCase())||
                            salesRowArrayList.get(i).getDate().toLowerCase().contains(query.toLowerCase())) {
                        salesRows.add(new SalesRow(salesRowArrayList.get(i).getSn(),salesRowArrayList.get(i).getProduct_name(), salesRowArrayList.get(i).getQuantity(),salesRowArrayList.get(i).getAmount(),
                                salesRowArrayList.get(i).getDate(),salesRowArrayList.get(i).getStore_id()));
                    }
                }

                salesTableAdapter = new SalesTableAdapter(SalesTableActivity.this, salesRows);
                recyclerView.setAdapter(salesTableAdapter);
                salesTableAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!salesRows.isEmpty())
                    salesRows.clear();

                for (int i = 0; i < salesRowArrayList.size(); i++) {
                    if (salesRowArrayList.get(i).getSn().toLowerCase().contains(newText.toLowerCase())||salesRowArrayList.get(i).getProduct_name().toLowerCase().contains(newText.toLowerCase())||
                            salesRowArrayList.get(i).getQuantity().toLowerCase().contains(newText.toLowerCase())||salesRowArrayList.get(i).getAmount().toLowerCase().contains(newText.toLowerCase())||
                            salesRowArrayList.get(i).getDate().toLowerCase().contains(newText.toLowerCase())) {
                        salesRows.add(new SalesRow(salesRowArrayList.get(i).getSn(),salesRowArrayList.get(i).getProduct_name(), salesRowArrayList.get(i).getQuantity(),salesRowArrayList.get(i).getAmount(),
                                salesRowArrayList.get(i).getDate(),salesRowArrayList.get(i).getStore_id()));
                    }
                }

                salesTableAdapter = new SalesTableAdapter(SalesTableActivity.this, salesRows);
                recyclerView.setAdapter(salesTableAdapter);
                salesTableAdapter.notifyDataSetChanged();

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
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



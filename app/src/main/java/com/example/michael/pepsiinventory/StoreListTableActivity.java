package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreListTableActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    StoreListTableAdapter storeListTableAdapter;
    LinearLayout tableRow;
    androidx.appcompat.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment,store_url;
    ArrayList<Store> storeRowArrayList = new ArrayList<>();
    ArrayList<Store> storeRows = new ArrayList<>();
    ImageView imageView;
    TextView textView;
    String TAG = StoreListTableActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list_table);

        intentFragment = getIntent().getStringExtra("frgToLoad");

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        toolbar.setTitle("Stores");

        store_url = getString(R.string.serve_url) + "stores";

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

        if(isOnline())
            new StoreLoadingTask(StoreListTableActivity.this).execute();
        else
            Toast.makeText(this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_items_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                storeRows.clear();

                for(int i = 0; i < storeRowArrayList.size(); i++){
                    if(storeRowArrayList.get(i).getStore_id().toLowerCase().contains(query.toLowerCase())||storeRowArrayList.get(i).getStore_name().toLowerCase().contains(query.toLowerCase())||
                            storeRowArrayList.get(i).getLocation().toLowerCase().contains(query.toLowerCase())){
                        storeRows.add(new Store(storeRowArrayList.get(i).getStore_id(),storeRowArrayList.get(i).getStore_name(),storeRowArrayList.get(i).getLocation(), storeRowArrayList.get(i).getStore_type()));
                    }
                }

                storeListTableAdapter = new StoreListTableAdapter(StoreListTableActivity.this, storeRows);
                recyclerView.setAdapter(storeListTableAdapter);
                storeListTableAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!storeRows.isEmpty())
                    storeRows.clear();

                for(int i = 0; i < storeRowArrayList.size(); i++){
                    if(storeRowArrayList.get(i).getStore_id().toLowerCase().contains(newText.toLowerCase())||storeRowArrayList.get(i).getStore_name().toLowerCase().contains(newText.toLowerCase())||
                            storeRowArrayList.get(i).getLocation().toLowerCase().contains(newText.toLowerCase())){
                        storeRows.add(new Store(storeRowArrayList.get(i).getStore_id(),storeRowArrayList.get(i).getStore_name(),storeRowArrayList.get(i).getLocation(), storeRowArrayList.get(i).getStore_type()));
                    }
                }

                storeListTableAdapter = new StoreListTableAdapter(StoreListTableActivity.this, storeRows);
                recyclerView.setAdapter(storeListTableAdapter);
                storeListTableAdapter.notifyDataSetChanged();

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
            Intent intent = new Intent(StoreListTableActivity.this,LoginActivity.class);
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
        Intent intent = new Intent(StoreListTableActivity.this,AdminActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
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
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                        }

                        storeListTableAdapter = new StoreListTableAdapter(StoreListTableActivity.this, storeRowArrayList);
                        recyclerView.setAdapter(storeListTableAdapter);

                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                    } else {
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                        imageView.setVisibility(View.VISIBLE);
                        textView.setText("Oops... No Stores Found!");
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

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) StoreListTableActivity.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        Log.d(TAG, "OnReceiveNetInfo: " + netInfo.getExtraInfo());
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}

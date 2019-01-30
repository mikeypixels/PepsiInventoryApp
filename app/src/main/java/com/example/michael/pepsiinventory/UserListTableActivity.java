package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserListTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    UserListTableAdapter userListTableAdapter;
    LinearLayout tableRow;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment,user_url,store_url;
    ArrayList<String> storeString = new ArrayList<>();
    ArrayList<User> userRowArrayList = new ArrayList<>();
    ArrayList<User> userArrayList = new ArrayList<>();
    ArrayList<User> userRows = new ArrayList<>();
    Spinner store_spinner;
    ArrayList<Store> storeRowArrayList = new ArrayList<>();
    String TAG = UserListTableActivity.class.getSimpleName();
    String store_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_table);

        intentFragment = getIntent().getStringExtra("frgToLoad");

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        store_spinner = findViewById(R.id.store_spinner);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        toolbar.setTitle("Users");

        user_url = getString(R.string.serve_url) + "users.php";
        store_url = getString(R.string.serve_url) + "stores.php";

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

        new UserLoadingTask(UserListTableActivity.this).execute();
        new StoreLoadingTask(UserListTableActivity.this).execute();

        store_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + store_spinner.getSelectedItem().toString());

                ((TextView) view).setTextColor(Color.WHITE);

                for (int i = 0; i < storeRowArrayList.size(); i++) {
                    if (store_spinner.getItemAtPosition(position).toString().equals(storeRowArrayList.get(i).getStore_name())) {
                        store_id = storeRowArrayList.get(i).getStore_id();
                        break;
                    } else {
                        store_id = "0";
                    }
                }

                Toast.makeText(UserListTableActivity.this, "onPostReceive: " + store_id, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "onPostReceive: " + store_id);
                Log.d(TAG, "onPostReceive: " + store_spinner.getSelectedItem().toString());

                if(!userArrayList.isEmpty())
                    userArrayList.clear();

                for (int i = 0; i < userRowArrayList.size(); i++)
                    if (store_id.equals(userRowArrayList.get(i).getStore_id())) {
                        userArrayList.add(userRowArrayList.get(i));
//                        Log.d(TAG, "onPostReceiveValue: " + salesArrayList.get(i).getProduct_name());
                    }

                Log.d(TAG, "onPostReceiveSize: " + userArrayList.size());

                userListTableAdapter = new UserListTableAdapter(UserListTableActivity.this, userArrayList);
                recyclerView.setAdapter(userListTableAdapter);
                userListTableAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

//    public ArrayList<User> getList(){
//        ArrayList<User> arrayList = new ArrayList<>();
//
//        for(int i=0; i<45 ; i++){
//            arrayList.add(i,new User("US/1","John","Ndugu","worker","active"));
//        }
//
//        return arrayList;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_items_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!userRows.isEmpty())
                    userRows.clear();

                for(int i = 0; i < userArrayList.size(); i++){
                    if(userArrayList.get(i).getUser_id().toLowerCase().contains(query.toLowerCase())||userArrayList.get(i).getF_name().toLowerCase().contains(query.toLowerCase())||userArrayList.get(i).getL_name().toLowerCase().contains(query.toLowerCase())||
                            userArrayList.get(i).getRole().toLowerCase().contains(query.toLowerCase())||userArrayList.get(i).getStatus().toLowerCase().contains(query.toLowerCase())){
                        userRows.add(new User(userArrayList.get(i).getUser_id(),userArrayList.get(i).getF_name(),userArrayList.get(i).getL_name(),userArrayList.get(i).getRole(),userArrayList.get(i).getStatus(),userArrayList.get(i).getStore_id()));
                    }
                }

                userListTableAdapter = new UserListTableAdapter(UserListTableActivity.this, userRows);
                recyclerView.setAdapter(userListTableAdapter);
                userListTableAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!userRows.isEmpty())
                    userRows.clear();

                for(int i = 0; i < userArrayList.size(); i++){
                    if(userArrayList.get(i).getUser_id().toLowerCase().contains(newText.toLowerCase())||userArrayList.get(i).getF_name().toLowerCase().contains(newText.toLowerCase())||userArrayList.get(i).getL_name().toLowerCase().contains(newText.toLowerCase())||
                        userArrayList.get(i).getRole().toLowerCase().contains(newText.toLowerCase())||userArrayList.get(i).getStatus().toLowerCase().contains(newText.toLowerCase())){
                        userRows.add(new User(userArrayList.get(i).getUser_id(),userArrayList.get(i).getF_name(),userArrayList.get(i).getL_name(),userArrayList.get(i).getRole(),userArrayList.get(i).getStatus(),userArrayList.get(i).getStore_id()));
                    }
                }

                userListTableAdapter = new UserListTableAdapter(UserListTableActivity.this, userRows);
                recyclerView.setAdapter(userListTableAdapter);
                userListTableAdapter.notifyDataSetChanged();

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
        Intent intent = new Intent(UserListTableActivity.this,AdminActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
    }

    public class UserLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
        StoreListTableAdapter storeListTableAdapter;
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public UserLoadingTask(Context ctx) {
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
            return httpHandler.makeServiceCall(user_url);
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("users");

                    for (int i = 0; i < storeRowArrayList.size(); i++) {
                        if (store_spinner.getSelectedItem().toString().equals(storeRowArrayList.get(i).getStore_name())) {
                            store_id = storeRowArrayList.get(i).getStore_id();
                            break;
                        } else {
                            store_id = "0";
                        }
                    }

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            userRowArrayList.add(new User(jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("f_name"),
                                    jsonArray.getJSONObject(i).getString("l_name"),
                                    jsonArray.getJSONObject(i).getString("u_role"),
                                    jsonArray.getJSONObject(i).getString("status"),
                                    jsonArray.getJSONObject(i).getString("store_id")));
//                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                        }

//                        userListTableAdapter = new UserListTableAdapter(UserListTableActivity.this,userRowArrayList);
//                        recyclerView.setAdapter(userListTableAdapter);

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

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserListTableActivity.this,android.R.layout.simple_spinner_dropdown_item,storeString);
                        store_spinner.setAdapter(adapter);

                        store_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//                        store_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
}

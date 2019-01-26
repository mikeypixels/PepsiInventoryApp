package com.example.michael.pepsiinventory;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class StoreListTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    StoreListTableAdapter storeListTableAdapter;
    LinearLayout tableRow;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment;
    ArrayList<Store> storeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list_table);

        intentFragment = getIntent().getStringExtra("frgToLoad");

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        toolbar.setTitle("Stores");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storeList = getList();

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

        storeListTableAdapter = new StoreListTableAdapter(StoreListTableActivity.this,storeList);
        recyclerView.setAdapter(storeListTableAdapter);
    }

    public ArrayList<Store> getList(){
        ArrayList<Store> arrayList = new ArrayList<>();
        ArrayList<Store> newArrayList = new ArrayList<>();

        newArrayList.add(new Store("ST/1","Tanga Store","Tanga"));
        newArrayList.add(new Store("ST/2","Dodoma Store","Dodoma"));
        newArrayList.add(new Store("ST/3","Dar Store","Dar"));

        arrayList.addAll(newArrayList);

        return arrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
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
        Intent intent = new Intent(StoreListTableActivity.this,AdminActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
    }
}

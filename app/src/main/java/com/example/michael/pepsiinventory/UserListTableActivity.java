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

public class UserListTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    UserListTableAdapter userListTableAdapter;
    LinearLayout tableRow;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment;
    ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_table);

        intentFragment = getIntent().getStringExtra("frgToLoad");

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        toolbar.setTitle("Users");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userList = getList();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);
//        salesRows.add(salesTableAdapter.)

        userListTableAdapter = new UserListTableAdapter(UserListTableActivity.this,userList);
        recyclerView.setAdapter(userListTableAdapter);
    }

    public ArrayList<User> getList(){
        ArrayList<User> arrayList = new ArrayList<>();

        for(int i=0; i<45 ; i++){
            arrayList.add(i,new User("US/1","John","Ndugu","worker","active"));
        }

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
        Intent intent = new Intent(UserListTableActivity.this,AdminActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
    }
}

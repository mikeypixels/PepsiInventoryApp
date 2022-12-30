package com.example.michael.pepsiinventory;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class AdminActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Toolbar toolbar;
    String intentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        intentFragment = getIntent().getStringExtra("frgToLoad");

        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        toolbar.setTitle("Admin Panel");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddUserFragment(), "User");
        adapter.addFragment(new AddStoreFragment(), "Store");
        adapter.addFragment(new StocksFragment(), "Stocks");
        adapter.addFragment(new ChangePriceFragment(), "Price");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
        intent.putExtra("frgToLoad", intentFragment);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
//        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        SalesFragment homeFragment = new SalesFragment();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_users) {
            Intent intent = new Intent(AdminActivity.this, UserListTableActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_view_stores) {
            Intent intent = new Intent(AdminActivity.this, StoreListTableActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_view_stocks) {
            Intent intent = new Intent(AdminActivity.this, StocksListTableActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

package com.example.michael.pepsiinventory;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.michael.pepsiinventory.MainActivity.navItemIndex;

public class ExpenseTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ExpenseTableAdapter expenseTableAdapter;
    TableLayout tableLayout;
    TableRow tableRow;
    TextView sn,product_name,quantity,amount,date;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment;
    ArrayList<ExpenseRow> expenseRows = new ArrayList<>();

    public ExpenseTableActivity() {
        //required empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_table);
        intentFragment = getIntent().getStringExtra("frgToLoad");

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
//        collapsingToolbarLayout.setTitle("Sales Table");
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        expenseRows = getList();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

        expenseTableAdapter = new ExpenseTableAdapter(ExpenseTableActivity.this,expenseRows);
        recyclerView.setAdapter(expenseTableAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 2) {
            getMenuInflater().inflate(R.menu.notification, menu);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ExpenseTableActivity.this,MainActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
    }

    public ArrayList<ExpenseRow> getList(){
        ArrayList<ExpenseRow> arrayList = new ArrayList<>();

        for(int i=0; i<45 ; i++){
            arrayList.add(i,new ExpenseRow("1","transport","185,000","","10/01/2019"));
        }

        return arrayList;
    }
}

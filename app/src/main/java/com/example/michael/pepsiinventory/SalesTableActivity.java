package com.example.michael.pepsiinventory;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.michael.pepsiinventory.MainActivity.navItemIndex;

public class SalesTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SalesTableAdapter salesTableAdapter;
    TableLayout tableLayout;
    LinearLayout tableRow;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment;
    MenuItem menuItem;
    MainActivity mainActivity;
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

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        salesRows = getList();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);
//        salesRows.add(salesTableAdapter.)

        salesTableAdapter = new SalesTableAdapter(SalesTableActivity.this,salesRows);
        recyclerView.setAdapter(salesTableAdapter);

//        salesTableAdapter.setOnItemClickListener(new SalesTableAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                salesRows.get(position);
//                Intent intent = new Intent(SalesTableActivity.this,PopUpActivity.class);
//                startActivity(intent);
//            }
//        });

        Log.d(TAG,"OnReceive : " + salesRows.get(0).getAmount());
    }

    public ArrayList<SalesRow> getList(){
        ArrayList<SalesRow> arrayList = new ArrayList<>();

        for(int i=0; i<45 ; i++){
            arrayList.add(i,new SalesRow("1","Crate","15","185,000","10/01/2019"));
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
        Intent intent = new Intent(SalesTableActivity.this,MainActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
    }
}



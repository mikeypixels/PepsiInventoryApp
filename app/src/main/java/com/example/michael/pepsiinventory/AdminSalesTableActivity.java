package com.example.michael.pepsiinventory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class AdminSalesTableActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdminSalesTableAdapter adminSalesTableAdapter;
    TableLayout tableLayout;
    LinearLayout tableRow;
    android.support.v7.widget.Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String intentFragment;
    MenuItem menuItem;
    MainActivity mainActivity;
    ArrayList<SalesRow> salesRows = new ArrayList<>();
    EditText sn,product_name,quantity,amount,date;
    Spinner spinner;

    private final static String TAG = AdminSalesTableActivity.class.getSimpleName();

    public AdminSalesTableActivity(){
        //required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_sales_table);

        intentFragment = getIntent().getStringExtra("frgToLoad");
        tableRow = findViewById(R.id.tableRow1);
        spinner = findViewById(R.id.spinner2);


//        tableLayout = new TableLayout(this);
//        tableRow = new TableRow(this);
//        sn = new TextView(this);
//        product_name = new TextView(this);
//        quantity = new TextView(this);
//        amount = new TextView(this);
//        date = new TextView(this);
//        sn.setText("S/N");
//        product_name.setText("product name");
//        quantity.setText("quantity");
//        amount.setText("amount");
//        date.setText("date");
//        tableRow.addView(sn);
//        tableRow.addView(product_name);
//        tableRow.addView(quantity);
//        tableRow.addView(amount);
//        tableRow.addView(date);
//        tableRow.setPadding(10,10,10,10);
//        tableRow.setBackgroundColor(5);
//        tableLayout.addView(tableRow);

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
//        collapsingToolbarLayout.setTitle("Sales Table");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        ArrayList<String> list = new ArrayList<>();
        list.add("Tanga store");
        list.add("Dodoma store");
        list.add("Dar store");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner.setOnItemSelectedListener(listener);

        salesRows = getList();

        recyclerView = findViewById(R.id.recyclerView1);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);
//        salesRows.add(salesTableAdapter.)

        adminSalesTableAdapter = new AdminSalesTableAdapter(AdminSalesTableActivity.this,salesRows);
        recyclerView.setAdapter(adminSalesTableAdapter);

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
        Intent intent = new Intent(AdminSalesTableActivity.this,MainActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
    }
}

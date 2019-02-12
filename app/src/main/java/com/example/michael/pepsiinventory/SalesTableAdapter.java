package com.example.michael.pepsiinventory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.michael.pepsiinventory.ExpenseTableAdapter.TAG;

public class SalesTableAdapter extends RecyclerView.Adapter<SalesTableAdapter.SalesViewHolder> {

    Context context;
    private ArrayList<SalesRow> salesRowArrayList;
    private final static String TAG = SalesTableAdapter.class.getSimpleName();
    TextView sn, product, quant, amnt, datepick;
    Button editButton, deleteButton, saveButton, cancelButton;
    EditText qtty, dateV;
    TextView amt;
    Spinner product_spinner;
    TextView sNo;
    String sale_update_url = "http://192.168.43.174/pepsi/sale_update.php";
    String sale_delete_url = "http://192.168.43.174/pepsi/delete_sale.php";
    String product_id;
    String cost;
    ArrayList<String> productString = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public SalesTableAdapter(Context context, ArrayList<SalesRow> salesRows) {
        this.context = context;
        salesRowArrayList = salesRows;
    }

    public static class SalesViewHolder extends RecyclerView.ViewHolder {

        TextView no, product_name, quantity, amount, date;
        LinearLayout tableRow;

        public SalesViewHolder(View itemView) {
            super(itemView);
            no = itemView.findViewById(R.id.no);
            product_name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            tableRow = itemView.findViewById(R.id.tableRow1);

        }
    }

    @NonNull
    @Override
    public SalesTableAdapter.SalesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.sales_table_layout, null);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SalesTableAdapter.SalesViewHolder salesViewHolder, int i) {

        String myFormat = "yyyy-mm-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());


        if (i == 0) {
            salesViewHolder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            salesViewHolder.no.setText("S/N");
            salesViewHolder.no.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.product_name.setText("product");
            salesViewHolder.product_name.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.quantity.setText("quantity");
            salesViewHolder.quantity.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.amount.setText("amount");
            salesViewHolder.amount.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.date.setText("sales date");
            salesViewHolder.date.setTextColor(Color.parseColor("#ffffff"));

        } else {

            Log.d(TAG, "OnReceive : " + salesRowArrayList.get(0).getAmount());

            salesViewHolder.tableRow.setBackgroundColor(Color.parseColor("#efefef"));
            salesViewHolder.no.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.product_name.setPadding(4,0,0,0);
            salesViewHolder.product_name.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.quantity.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.amount.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.date.setTextColor(Color.parseColor("#000000"));

            if(salesRowArrayList.get(i-1).getProduct_name().equals("1")||salesRowArrayList.get(i-1).getProduct_name().equals("Crate"))
                salesRowArrayList.get(i-1).setProduct_name("Crate");
            else if(salesRowArrayList.get(i-1).getProduct_name().equals("2")||salesRowArrayList.get(i-1).getProduct_name().equals("Full shell"))
                salesRowArrayList.get(i-1).setProduct_name("Full shell");
            else if(salesRowArrayList.get(i-1).getProduct_name().equals("3")||salesRowArrayList.get(i-1).getProduct_name().equals("Bottle"))
                salesRowArrayList.get(i-1).setProduct_name("Bottle");

            salesViewHolder.no.setText(salesRowArrayList.get(i-1).getSn());
            salesViewHolder.product_name.setText(salesRowArrayList.get(i-1).getProduct_name());
            salesViewHolder.quantity.setText(salesRowArrayList.get(i-1).getQuantity());
            NumberFormat formatter = new DecimalFormat("#,###");
            String formattedNumber = formatter.format(Double.parseDouble(salesRowArrayList.get(i - 1).getAmount()));
            salesViewHolder.amount.setText(formattedNumber);
            salesViewHolder.date.setText(salesRowArrayList.get(i-1).getDate());

            final SalesRow salesRow = salesRowArrayList.get(i-1);

            salesViewHolder.tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                            Intent intent = new Intent(context,PopUpActivity.class);
//                            intent.putExtra("message","animation beauty");
//                            intent.putExtra("sale",new Gson().toJson(salesRow));
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);

                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            myCalendar.set(Calendar.YEAR,year);
                            myCalendar.set(Calendar.MONTH,month);
                            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                            updateLabel();
                        }
                    };

                    productString.add("Crate");
                    productString.add("Full shell");
                    productString.add("Bottle");

                    final Dialog dialognew = new Dialog(context);
                    DisplayMetrics dm = context.getResources().getDisplayMetrics();
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    dialognew.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialognew.setContentView(R.layout.popup_window);
                    dialognew.getWindow().setLayout((int) (width * .9), (int) (height * .6));
                    dialognew.setCancelable(true);

                    sn = dialognew.findViewById(R.id.sn);
                    product = dialognew.findViewById(R.id.product_name);
                    quant = dialognew.findViewById(R.id.quantity);
                    amnt = dialognew.findViewById(R.id.amount);
                    datepick = dialognew.findViewById(R.id.date);
                    editButton = dialognew.findViewById(R.id.edit);
                    deleteButton = dialognew.findViewById(R.id.delete);

                    sn.setText(salesRow.getSn());
                    product.setText(salesRow.getProduct_name());
                    quant.setText(salesRow.getQuantity());
                    NumberFormat formatter = new DecimalFormat("#,###");
                    String formattedNumber = formatter.format(Double.parseDouble(salesRow.getAmount()));
                    amnt.setText(formattedNumber);
                    datepick.setText(salesRow.getDate());

                    dialognew.show();

                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialognew.setContentView(R.layout.edit_layout);
                            product_spinner = new Spinner(context);

                            sNo = dialognew.findViewById(R.id.sn);
                            sNo.setText(salesRow.getSn());
                            product_spinner = dialognew.findViewById(R.id.product_name_spinner);

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, productString);
                            product_spinner.setAdapter(adapter);
                            product_spinner.getBackground().setColorFilter(context.getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

                            if(salesRow.getProduct_name().equals("Crate"))
                                product_spinner.setSelection(0);
                            else if(salesRow.getProduct_name().equals("Full shell"))
                                product_spinner.setSelection(1);
                            else if(salesRow.getProduct_name().equals("Bottle"))
                                product_spinner.setSelection(2);
                            qtty = dialognew.findViewById(R.id.quantity);
                            qtty.setText(salesRow.getQuantity());
                            amt = dialognew.findViewById(R.id.amount);
                            dateV = dialognew.findViewById(R.id.date);
                            dateV.setText(salesRow.getDate());

                            dateV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new DatePickerDialog(v.getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                }
                            });

                            saveButton = dialognew.findViewById(R.id.save);
                            cancelButton = dialognew.findViewById(R.id.cancel);

                            Log.d(TAG, "OnReceiveDetails: " + sNo + " " + qtty+ " " + amt);

                            product_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    ((TextView) view).setTextColor(Color.WHITE);

                                    if(product_spinner.getItemAtPosition(position).toString().equals("Crate")) {

                                        if(qtty.getText().toString().isEmpty()){
                                            amt.setText("");
                                        }else {
                                            cost = String.valueOf(9800 * Integer.parseInt(qtty.getText().toString()));
                                            NumberFormat formatter = new DecimalFormat("#,###");
                                            String formattedNumber = formatter.format(Double.parseDouble(cost));
                                            amt.setText(formattedNumber);
                                        }

                                        qtty.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if(s.toString().isEmpty()){
                                                    amt.setText("");
                                                }else {
                                                    cost = String.valueOf(9800 * Integer.parseInt(String.valueOf(s)));
                                                    NumberFormat formatter = new DecimalFormat("#,###");
                                                    String formattedNumber = formatter.format(Double.parseDouble(cost));
                                                    amt.setText(formattedNumber);
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                if(s.toString().isEmpty()){
                                                    amt.setText("");
                                                }else {
                                                    cost = String.valueOf(9800 * Integer.parseInt(s.toString()));
                                                    NumberFormat formatter = new DecimalFormat("#,###");
                                                    String formattedNumber = formatter.format(Double.parseDouble(cost));
                                                    amt.setText(formattedNumber);
                                                }
                                            }
                                        });

                                    }
                                    else if(product_spinner.getItemAtPosition(position).toString().equals("Full shell")) {

                                        if(qtty.getText().toString().isEmpty()){
                                            amt.setText("");
                                        }else {
                                            cost = String.valueOf(19800 * Integer.parseInt(qtty.getText().toString()));
                                            NumberFormat formatter = new DecimalFormat("#,###");
                                            String formattedNumber = formatter.format(Double.parseDouble(cost));
                                            amt.setText(formattedNumber);
                                        }

                                        qtty.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if(s.toString().isEmpty()){
                                                    amt.setText("");
                                                }else {
                                                    cost = String.valueOf(19800*Integer.parseInt(s.toString()));
                                                    NumberFormat formatter = new DecimalFormat("#,###");
                                                    String formattedNumber = formatter.format(Double.parseDouble(cost));
                                                    amt.setText(formattedNumber);
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                if(s.toString().isEmpty()){
                                                    amt.setText("");
                                                }else {
                                                    cost = String.valueOf(19800 * Integer.parseInt(s.toString()));
                                                    NumberFormat formatter = new DecimalFormat("#,###");
                                                    String formattedNumber = formatter.format(Double.parseDouble(cost));
                                                    amt.setText(formattedNumber);
                                                }
                                            }
                                        });
                                    }
                                    else if(product_spinner.getItemAtPosition(position).toString().equals("Bottle")) {

                                        if(qtty.getText().toString().isEmpty()){
                                            amt.setText("");
                                        }else {
                                            cost = String.valueOf(300 * Integer.parseInt(qtty.getText().toString()));
                                            NumberFormat formatter = new DecimalFormat("#,###");
                                            String formattedNumber = formatter.format(Double.parseDouble(cost));
                                            amt.setText(formattedNumber);
                                        }

                                        qtty.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if(s.toString().isEmpty()){
                                                    amt.setText("");
                                                }else {
                                                    cost = String.valueOf(300 * Integer.parseInt(s.toString()));
                                                    NumberFormat formatter = new DecimalFormat("#,###");
                                                    String formattedNumber = formatter.format(Double.parseDouble(cost));
                                                    amt.setText(formattedNumber);
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                if(s.toString().isEmpty()){
                                                    amt.setText("");
                                                }else {
                                                    cost = String.valueOf(300 * Integer.parseInt(s.toString()));
                                                    NumberFormat formatter = new DecimalFormat("#,###");
                                                    String formattedNumber = formatter.format(Double.parseDouble(cost));
                                                    amt.setText(formattedNumber);
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            saveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(product_spinner.getSelectedItem().toString().equals("Crate")) {
                                        product_id = "1";
                                    }
                                    else if(product_spinner.getSelectedItem().toString().equals("Full shell")) {
                                        product_id = "2";
                                    }
                                    else if(product_spinner.getSelectedItem().toString().equals("Bottle")) {
                                        product_id = "3";
                                    }

                                    String[] dateArray = dateV.getText().toString().split("-");
                                    String databaseDate = dateArray[2].concat("-" + dateArray[1] + "-" + dateArray[0]);

                                    if(!qtty.getText().toString().isEmpty()) {
                                        new UpdateSaleTask(context).execute(product_id, qtty.getText().toString(), amt.getText().toString().replaceAll(",",""), databaseDate, sNo.getText().toString());
                                        dialognew.dismiss();
                                    }
                                    else
                                        Toast.makeText(context, "please fill the quantity field!", Toast.LENGTH_SHORT).show();
//                                  new SalesTableActivity.SalesLoadingTask(context).execute();
                                }
                            });

                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialognew.dismiss();
                                }
                            });

                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Log.d(TAG,"It reaches here for some good reason");

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder.setTitle("Alert");
                            builder.setMessage("Are you sure you want to delete the sale?");

                            Log.d(TAG,"OnSalesReceived: " + sn.getText().toString());

                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new SalesDeleteTask(context).execute(sn.getText().toString());
                                    dialognew.dismiss();
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    });


                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return salesRowArrayList.size()+1;
    }

    private void updateLabel(){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        dateV.setText(sdf.format(myCalendar.getTime()));
    }

    public class UpdateSaleTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;

        public UpdateSaleTask(Context ctx) {
            this.context = ctx;
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
        protected String doInBackground(String... strings) {

            String sale_name = strings[0];
            String sale_quantity = strings[1];
            String sale_amount = strings[2];
            String sale_date = strings[3];
            String sale_id = strings[4];

            try {
                URL url = new URL(sale_update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(sale_name, "UTF-8") + "&" +
                        URLEncoder.encode("quantity", "UTF-8") + "=" + URLEncoder.encode(sale_quantity, "UTF-8") + "&" +
                        URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(sale_amount, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(sale_date, "UTF-8") + "&" +
                        URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(sale_id, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response = response.concat(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            if (result != null) {
                if (result.contains("Successful")) {
                    String[] userDetails = result.split("-");
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                } else {
                    Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                }
            } else {
                Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
                if (this.dialog != null) {
                    this.dialog.dismiss();
                }
            }
        }
    }

    public class SalesDeleteTask extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = ExpenseTableActivity.class.getSimpleName();
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public SalesDeleteTask(Context ctx) {
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
        protected String doInBackground(String... strings) {

            String sale_id = strings[0];

            try {
                URL url = new URL(sale_delete_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(sale_id, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response = response.concat(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

            HttpHandler httpHandler = new HttpHandler();
            return httpHandler.makeServiceDelete(sale_delete_url);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {
                if(result.contains("Successful")){
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }

                    Toast.makeText(context, "successfully deleted", Toast.LENGTH_SHORT).show();
                }else{
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                    Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_SHORT).show();
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

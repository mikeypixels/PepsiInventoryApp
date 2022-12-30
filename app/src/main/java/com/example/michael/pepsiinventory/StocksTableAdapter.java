package com.example.michael.pepsiinventory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.Calendar;

public class StocksTableAdapter extends RecyclerView.Adapter<StocksTableAdapter.StocksViewHolder> {
    Context context;
    private ArrayList<Stock> stocksRowDetails;
    private ArrayList<Stock> stocksDetails;

    IntChecker intChecker = new IntChecker();
    String update_stocks_url, store_id, TAG = StocksTableAdapter.class.getSimpleName();
    int position;

    public StocksTableAdapter(Context context, ArrayList<Stock> stocksRows, String store_id) {
        this.context = context;
        stocksRowDetails = stocksRows;
        this.store_id = store_id;
    }

    public class StocksViewHolder extends RecyclerView.ViewHolder {

        TextView id, product_name, store_name, available_quantity;
        ConstraintLayout tableRow;

        public StocksViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.no);
            product_name = itemView.findViewById(R.id.product_name);
            store_name = itemView.findViewById(R.id.store_name);
            available_quantity = itemView.findViewById(R.id.available_quantity);
            tableRow = itemView.findViewById(R.id.tableRow1);
        }
    }

    @NonNull
    @Override
    public StocksTableAdapter.StocksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StocksViewHolder(LayoutInflater.from(context).inflate(R.layout.stocks_table_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull StocksViewHolder holder, int i) {
        if (i == 0) {
            holder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            holder.tableRow.setPadding(13, 13, 13, 13);
            holder.id.setText("id");
            holder.id.setTextColor(Color.parseColor("#ffffff"));
            holder.product_name.setText("product");
            holder.product_name.setTextColor(Color.parseColor("#ffffff"));
            holder.available_quantity.setText("store");
            holder.available_quantity.setTextColor(Color.parseColor("#ffffff"));
            holder.store_name.setText("quantity");
            holder.store_name.setTextColor(Color.parseColor("#ffffff"));
        }else{
            holder.tableRow.setBackgroundColor(Color.parseColor("#efefef"));
            holder.tableRow.setPadding(13, 13, 13, 13);
            holder.id.setTextColor(Color.parseColor("#000000"));
            holder.product_name.setTextColor(Color.parseColor("#000000"));
            holder.store_name.setTextColor(Color.parseColor("#000000"));
            holder.available_quantity.setTextColor(Color.parseColor("#000000"));

            holder.id.setText(stocksRowDetails.get(i - 1).getId());
            holder.product_name.setText(stocksRowDetails.get(i - 1).getProduct_name());
            holder.store_name.setText(stocksRowDetails.get(i - 1).getStore_name());
            holder.available_quantity.setText(stocksRowDetails.get(i - 1).getAvailable_quantity());


        final Stock stockRow = stocksRowDetails.get(i - 1);

        holder.tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialognew = new Dialog(context);
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                int width = dm.widthPixels;
                int height = dm.heightPixels;
                dialognew.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialognew.setContentView(R.layout.stock_edit_dialog_layout);
                dialognew.getWindow().setLayout((int) (width * .9), (int) (height * .6));
                dialognew.setCancelable(true);

                TextView sn = dialognew.findViewById(R.id.sn);
                TextView product = dialognew.findViewById(R.id.product_name);
                TextView store = dialognew.findViewById(R.id.store_name);
                EditText available_quantity = dialognew.findViewById(R.id.available_quantity);
                Button saveButton = dialognew.findViewById(R.id.save);
                Button cancelButton = dialognew.findViewById(R.id.cancel);

                sn.setText(stockRow.getId());
                product.setText(stockRow.getProduct_name());
                store.setText(stockRow.getStore_name());
                available_quantity.setText(stockRow.getAvailable_quantity());

                dialognew.show();

                position = holder.getAbsoluteAdapterPosition();

                saveButton.setOnClickListener(v1 -> {
                    final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                    int position = holder.getAdapterPosition()-1;
                    String[] dateArray;
                    String databaseDate;
                    if(!available_quantity.getText().toString().isEmpty()) {
                        if(intChecker.Checker(available_quantity.getText().toString().replaceAll(",",""))) {
//                                            expenseRowArrayList.remove(position);
//                                            notifyItemRemoved(position);
//                                            expenseRowArrayList.add(position, new ExpenseRow(sNo.getText().toString(), pName.getText().toString(), description0.getText().toString(), amt.getText().toString(), databaseDate, myPrefs.getString("user_id","")));
//                                            notifyItemInserted(position);
                            if(isOnline()) {
                                stocksRowDetails.get(position).setAvailable_quantity(available_quantity.getText().toString().replaceAll(",",""));
                                new StocksTableAdapter.UpdateStocksTask(context).execute(store_id, stocksRowDetails.get(position).getProduct_id(), available_quantity.getText().toString());
                            }
                            else
                                Toast.makeText(context, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            dialognew.dismiss();
                        }else
                            Toast.makeText(context, "quantity should be in number format!", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(context, "please fill available quantity field!", Toast.LENGTH_LONG).show();
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialognew.dismiss();
                    }
                });

            }
        });
        }

    }

    @Override
    public int getItemCount() {
        return stocksRowDetails.size() + 1;
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public class UpdateStocksTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;

        public UpdateStocksTask(Context ctx){
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

            String store_id = strings[0];
            String product_id = strings[1];
            String available_quantity = strings[2];

            update_stocks_url = this.context.getResources().getString(R.string.serve_url) + "stock/edit";

            try {
                URL url = new URL(update_stocks_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(product_id, "UTF-8") + "&" +
                        URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(store_id, "UTF-8")+ "&" +
                        URLEncoder.encode("available_quantity", "UTF-8") + "=" + URLEncoder.encode(available_quantity, "UTF-8");
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

            if (result != null)
            {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                stocksDetails = stocksRowDetails;
                stocksRowDetails = new ArrayList<>();
                stocksRowDetails.addAll(stocksDetails);
                notifyItemRangeChanged(position,getItemCount());
            } else
            {
                Toast.makeText(context, "Oops... something went wrong!", Toast.LENGTH_LONG).show();
            }
            if (this.dialog != null) {
                this.dialog.dismiss();
            }
        }
    }
}

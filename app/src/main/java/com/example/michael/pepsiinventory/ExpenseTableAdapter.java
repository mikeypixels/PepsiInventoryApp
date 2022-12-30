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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpenseTableAdapter extends RecyclerView.Adapter<ExpenseTableAdapter.ExpenseViewHolder> {

    Context context;
    private ArrayList<ExpenseRow> expenseRowArrayList;
    private ArrayList<ExpenseRow> expenseArrayList;

    TextView sn, product, description, amnt, datepick;
    Button editButton, deleteButton, saveButton, cancelButton;
    EditText pName, description0, amt, dateV;
    TextView sNo;
    String expense_update_url;
    String expense_delete_url;
    final Calendar myCalendar = Calendar.getInstance();
    IntChecker intChecker = new IntChecker();
    ExpenseInterface expenseInterface;
    int position;

    private String expDate;

    final static public String TAG = ExpenseTableAdapter.class.getSimpleName();

    public ExpenseTableAdapter(Context context, ArrayList<ExpenseRow> expenseRows, ExpenseInterface expenseInterface) {
        this.context = context;
        this.expenseInterface = expenseInterface;
        expenseRowArrayList = expenseRows;
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView no, expense_name, amount, date;
        ConstraintLayout tableRow;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            no = itemView.findViewById(R.id.no);
            expense_name = itemView.findViewById(R.id.expense_name);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            tableRow = itemView.findViewById(R.id.tableRow1);
        }
    }

    @NonNull
    @Override
    public ExpenseTableAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ExpenseViewHolder(LayoutInflater.from(context).inflate(R.layout.expense_table_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final ExpenseTableAdapter.ExpenseViewHolder expenseViewHolder, int i) {
        if (i == 0) {
            expenseViewHolder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            expenseViewHolder.tableRow.setPadding(13, 13, 13, 13);
            expenseViewHolder.no.setText("S/N");
            expenseViewHolder.no.setTextColor(Color.parseColor("#ffffff"));
            expenseViewHolder.expense_name.setText("expense");
            expenseViewHolder.expense_name.setTextColor(Color.parseColor("#ffffff"));
            expenseViewHolder.amount.setText("amount");
            expenseViewHolder.amount.setTextColor(Color.parseColor("#ffffff"));
            expenseViewHolder.date.setText("expense date");
            expenseViewHolder.date.setTextColor(Color.parseColor("#ffffff"));
        } else {

            expenseViewHolder.tableRow.setBackgroundColor(Color.parseColor("#efefef"));
            expenseViewHolder.tableRow.setPadding(13, 13, 13, 13);
            expenseViewHolder.no.setTextColor(Color.parseColor("#000000"));
            expenseViewHolder.expense_name.setTextColor(Color.parseColor("#000000"));
            expenseViewHolder.amount.setTextColor(Color.parseColor("#000000"));
            expenseViewHolder.date.setTextColor(Color.parseColor("#000000"));

            Log.d(TAG, "value" + i);
            expenseViewHolder.no.setText(expenseRowArrayList.get(i - 1).getNo());
            expenseViewHolder.expense_name.setText(expenseRowArrayList.get(i - 1).getExpense_name());
            NumberFormat formatter = new DecimalFormat("#,###");
            String formattedNumber = formatter.format(Double.parseDouble(expenseRowArrayList.get(i - 1).getAmount()));
            expenseViewHolder.amount.setText(formattedNumber);
            expenseViewHolder.date.setText(expenseRowArrayList.get(i - 1).getDate());

            final ExpenseRow expenseRow = expenseRowArrayList.get(i - 1);

            expenseViewHolder.tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Intent intent = new Intent(context,PopUpActivity2.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);

                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, month);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateLabel();
                        }
                    };

                    final Dialog dialognew = new Dialog(context);
                    DisplayMetrics dm = context.getResources().getDisplayMetrics();
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    dialognew.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialognew.setContentView(R.layout.activity_pop_up2);
                    dialognew.getWindow().setLayout((int) (width * .9), (int) (height * .6));
                    dialognew.setCancelable(true);

                    sn = dialognew.findViewById(R.id.sn);
                    product = dialognew.findViewById(R.id.product_name);
                    description = dialognew.findViewById(R.id.description);
                    amnt = dialognew.findViewById(R.id.amount);
                    datepick = dialognew.findViewById(R.id.date);
                    editButton = dialognew.findViewById(R.id.edit);
                    deleteButton = dialognew.findViewById(R.id.delete);

//                    expenseRow.setDescription("");

                    sn.setText(expenseRow.getNo());
                    product.setText(expenseRow.getExpense_name());
                    NumberFormat formatter = new DecimalFormat("#,###");
                    String formattedNumber = formatter.format(Double.parseDouble(expenseRow.getAmount()));
                    amnt.setText(formattedNumber);
                    description.setText(expenseRow.getDescription());
                    datepick.setText(expenseRow.getDate());

                    dialognew.show();

                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            Calendar calendar = Calendar.getInstance();
                            expDate = expenseRow.getDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            long millis = 0;
                            try {
                                Date sDate = null;
                                sDate = sdf.parse(expDate);
                                millis = sDate.getTime();
                                if (millis + 86400000 >= calendar.getTimeInMillis()) {
                                    dialognew.setContentView(R.layout.expense_edit_layout);

                                    sNo = dialognew.findViewById(R.id.sn);
                                    sNo.setText(expenseRow.getNo());
                                    pName = dialognew.findViewById(R.id.product_name);
                                    pName.setText(expenseRow.getExpense_name());
                                    amt = dialognew.findViewById(R.id.amount);
                                    NumberFormat formatter = new DecimalFormat("#,###");
                                    String formattedNumber = formatter.format(Double.parseDouble(expenseRow.getAmount()));
                                    amt.setText(formattedNumber);
                                    description0 = dialognew.findViewById(R.id.description0);
                                    description0.setText(expenseRow.getDescription());
                                    dateV = dialognew.findViewById(R.id.date);
                                    dateV.setText(expenseRow.getDate());

                                    dateV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new DatePickerDialog(v.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                        }
                                    });

                                    saveButton = dialognew.findViewById(R.id.save);
                                    cancelButton = dialognew.findViewById(R.id.cancel);

                                    saveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String[] dateArray;
                                            String databaseDate;
                                            if (dateV.getText().toString().contains("/")) {
                                                dateArray = dateV.getText().toString().split("/");
                                                databaseDate = "20" + dateArray[2].concat("-" + dateArray[1] + "-" + dateArray[0]);
                                            } else {
                                                databaseDate = dateV.getText().toString();
                                            }

                                            final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                                            Log.d(TAG, "OnExpenseTabel: " + amt.getText().toString());
                                            Log.d(TAG, "OnExpenseTabel: " + pName.getText().toString());
                                            if (!pName.getText().toString().isEmpty() && !amt.getText().toString().isEmpty()) {
                                                Log.d(TAG, "OnExpenseTabel: " + amt.getText().toString());
                                                if (intChecker.Checker(amt.getText().toString().replaceAll(",", ""))) {
                                                    position = expenseViewHolder.getAdapterPosition() - 1;
                                                    if (isOnline()) {
                                                        expenseRowArrayList.get(expenseViewHolder.getAdapterPosition() - 1).setDescription(description0.getText().toString());
                                                        expenseRowArrayList.get(expenseViewHolder.getAdapterPosition() - 1).setExpense_name(pName.getText().toString());
                                                        expenseRowArrayList.get(expenseViewHolder.getAdapterPosition() - 1).setDate(databaseDate);
                                                        expenseRowArrayList.get(expenseViewHolder.getAdapterPosition() - 1).setAmount(amt.getText().toString().replaceAll(",", ""));
                                                        new UpdateExpenseTask(context).execute(myPrefs.getString("store_id", ""), databaseDate, pName.getText().toString(), description0.getText().toString(), amt.getText().toString().replaceAll(",", ""), myPrefs.getString("user_id", ""), sNo.getText().toString());
                                                        Calendar calendar = Calendar.getInstance();
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.putString("sale_date", String.valueOf(calendar.getTimeInMillis()));
                                                        editor.apply();
                                                    } else
                                                        Toast.makeText(context, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                                    dialognew.dismiss();
                                                } else
                                                    Toast.makeText(context, "amount should be in number format!", Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(context, "please fill all fields except description which is optional!", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialognew.dismiss();
                                        }
                                    });

                                } else {
                                    Toast.makeText(context, "You can't edit this expense anymore", Toast.LENGTH_SHORT).show();
                                }
                            }catch(ParseException e){
                                e.printStackTrace();
                            }

                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar calendar = Calendar.getInstance();
                            expDate = expenseRow.getDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = null;
                            long millis = 0;
                            try {
                                date = sdf.parse(expDate);
                                assert date != null;
                                millis = date.getTime();

                                if (millis + 86400000 >= calendar.getTimeInMillis()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setTitle("Alert");
                                    builder.setMessage("Are you sure you want to delete the expense?");

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            position = expenseViewHolder.getAdapterPosition() - 1;
                                            if (isOnline())
                                                new ExpenseDeleteTask(context).execute(sn.getText().toString());
                                            else
                                                Toast.makeText(context, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
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
                                }else{
                                    Toast.makeText(context, "You can't delete this expense anymore!", Toast.LENGTH_SHORT).show();
                                }

                            }catch (ParseException e){
                                e.printStackTrace();
                            }

                        }
                    });

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return expenseRowArrayList.size() + 1;
    }

    protected boolean isOnline() {
        String TAG = LoginActivity.class.getSimpleName();
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        Log.d(TAG, "OnReceiveNetInfo: " + netInfo.getExtraInfo());
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        dateV.setText(sdf.format(myCalendar.getTime()));
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public class UpdateExpenseTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;

        public UpdateExpenseTask(Context ctx) {
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

            String exp_store_id = strings[0];
            String exp_date = strings[1];
            String exp_name = strings[2];
            String exp_description = strings[3];
            String exp_cost = strings[4];
            String exp_user_id = strings[5];
            String exp_id = strings[6];

            expense_update_url = this.context.getResources().getString(R.string.serve_url) + "expense/edit/" + exp_id;

            Log.d(TAG, "doInBackground: " + exp_user_id);

            try {
                URL url = new URL(expense_update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(exp_store_id, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(exp_date, "UTF-8") + "&" +
                        URLEncoder.encode("expense_name", "UTF-8") + "=" + URLEncoder.encode(exp_name, "UTF-8") + "&" +
                        URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(exp_description, "UTF-8") + "&" +
                        URLEncoder.encode("cost", "UTF-8") + "=" + URLEncoder.encode(exp_cost, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(exp_user_id, "UTF-8") + "&" +
                        URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(exp_id, "UTF-8");
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
                if (result.contains("Updated")) {
                    String[] userDetails = result.split("-");
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    expenseArrayList = expenseRowArrayList;
                    expenseRowArrayList = new ArrayList<>();
                    expenseRowArrayList.addAll(expenseArrayList);
                    notifyItemRangeChanged(position, getItemCount());

                    double total_amount = 0;

                    for (int i = 0; i < expenseRowArrayList.size(); i++) {
                        if (expenseRowArrayList.get(i).getDate().equals(getDateTime()))
                            total_amount = total_amount + Double.parseDouble(expenseRowArrayList.get(i).getAmount().replaceAll(",", ""));
                    }

                    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

                    expenseInterface.showSnackBar(total_amount, preferences.getString("store_id", ""), preferences.getString("store_name", ""));

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

    public class ExpenseDeleteTask extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = ExpenseTableActivity.class.getSimpleName();
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public ExpenseDeleteTask(Context ctx) {
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

            String exp_id = strings[0];

            expense_delete_url = this.context.getResources().getString(R.string.serve_url) + "expense/delete/" + exp_id;

            try {
                URL url = new URL(expense_delete_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(exp_id, "UTF-8");
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

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            HttpHandler httpHandler = new HttpHandler();
            return httpHandler.makeServiceDelete(expense_delete_url);
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {
                if (result.contains("Deleted")) {
                    expenseInterface.getPosition(expenseRowArrayList.get(position));
                    expenseRowArrayList.remove(expenseRowArrayList.get(position));
                    notifyItemRemoved(position);

                    double total_amount = 0;

                    for (int i = 0; i < expenseRowArrayList.size(); i++) {
                        if (expenseRowArrayList.get(i).getDate().equals(getDateTime()))
                            total_amount = total_amount + Double.parseDouble(expenseRowArrayList.get(i).getAmount().replaceAll(",", ""));
                    }

                    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

                    expenseInterface.showSnackBar(total_amount, preferences.getString("store_id", ""), preferences.getString("store_name", ""));

                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }

                    Toast.makeText(context, "successfully deleted", Toast.LENGTH_SHORT).show();
                } else {
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

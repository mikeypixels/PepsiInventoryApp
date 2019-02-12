package com.example.michael.pepsiinventory;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

//    private static final String TAG = "Expenses";

    private static final String TAG = ExpenseFragment.class.getSimpleName();

    EditText name,amount,description,datepicker;
    TextView action_bar;
    Button send1;
    IntChecker intChecker;
    final Calendar myCalendar = Calendar.getInstance();
    String expense_url,user_id,store_id;

    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        name = view.findViewById(R.id.name);
        amount = view.findViewById(R.id.amount);
        description = view.findViewById(R.id.description);
        datepicker = view.findViewById(R.id.datepicker);
        send1 = view.findViewById(R.id.send1);
        action_bar = view.findViewById(R.id.action_bar);

        expense_url = getString(R.string.serve_url) + "add_expense.php";

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(container.getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        intChecker = new IntChecker();

        send1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

                String myFormat = "yyyy-mm-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                Log.d(TAG, "onReceiving: " + datepicker.getText().toString());
                    if (amount.getText().toString().isEmpty() || name.getText().toString().isEmpty() || datepicker.getText().toString().isEmpty()) {
                        action_bar.setText("please fill all fields!");
                    } else {
                        String[] dateArray = datepicker.getText().toString().split("/");
                        String databaseDate = dateArray[2].concat("-" + dateArray[1] + "-" + dateArray[0]);
                        if (intChecker.Checker(amount.getText().toString().replaceAll(",",""))) {
                            action_bar.setText("");
                            new AddExpenseTask(getContext()).execute(preferences.getString("store_id", ""),databaseDate,name.getText().toString(),description.getText().toString(),amount.getText().toString().replaceAll(",",""),user_id);
                        } else {
                            action_bar.setText("amount should be in currency format!");
                        }
                    }

                }

        });

        return view;
    }

    public class AddExpenseTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;

        public AddExpenseTask(Context ctx){
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

            Log.d(TAG,"doInBackground: " + exp_user_id);

            try {
                URL url = new URL(expense_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(exp_store_id, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(exp_date, "UTF-8")+ "&" +
                        URLEncoder.encode("expense_name", "UTF-8") + "=" + URLEncoder.encode(exp_name, "UTF-8")+ "&" +
                        URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(exp_description, "UTF-8")+ "&" +
                        URLEncoder.encode("cost", "UTF-8") + "=" + URLEncoder.encode(exp_cost, "UTF-8")+ "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(exp_user_id, "UTF-8");
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

            if (result != null)
            {
                if (result.contains("Successful")) {
                    String[] userDetails = result.split("-");
                    name.setText("");
                    amount.setText("");
                    description.setText("");
                    datepicker.setText("");
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                } else {
                    Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
                    if(this.dialog != null)
                        dialog.dismiss();
                }
            } else
            {
                Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
                if(this.dialog != null)
                    dialog.dismiss();
            }
        }
    }

    public void getStoreUserId(String store_id,String user_id){
        this.store_id = store_id;
        this.user_id = user_id;
    }

    private void updateLabel(){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        datepicker.setText(sdf.format(myCalendar.getTime()));
    }

}

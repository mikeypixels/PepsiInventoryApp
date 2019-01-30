package com.example.michael.pepsiinventory;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminExpenseFragment extends Fragment {

//    private static final String TAG = "Expenses";

    private static final String TAG = ExpenseFragment.class.getSimpleName();

    EditText name,amount,description,datepicker;
    TextView action_bar;
    Button send1;
    IntChecker intChecker;
    final Calendar myCalendar = Calendar.getInstance();
    Spinner store_spinner;
    String store_url,expense_url,user_id,store_id;
    ArrayList<Store> stores = new ArrayList<>();
    ArrayList<String> storesSting = new ArrayList<>();
    ArrayList<Store> storeDetails;

    public AdminExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_expense, container, false);
        name = view.findViewById(R.id.name);
        amount = view.findViewById(R.id.amount);
        description = view.findViewById(R.id.description);
        datepicker = view.findViewById(R.id.datepicker);
        send1 = view.findViewById(R.id.send1);
        action_bar = view.findViewById(R.id.action_bar);
        store_spinner = view.findViewById(R.id.store_spinner);

        store_url = getString(R.string.serve_url) + "stores.php";
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

                Log.d(TAG, "onReceiving: " + datepicker.getText().toString());
                if (!store_spinner.getSelectedItem().toString().equals("select store")) {
                    if (amount.getText().toString().isEmpty() || name.getText().toString().isEmpty() || datepicker.getText().toString().isEmpty()) {
                        action_bar.setText("please fill all fields!");
                    } else {
                        if (intChecker.Checker(amount.getText().toString())) {

                            for (Store store : storeDetails) {
                                if (store_spinner.getSelectedItem().toString().equals(store.getStore_name()))
                                    store_id = store.getStore_id();
                            }

                            action_bar.setText("");
                            if(isOnline()){
                                String myFormat = "yyyy-mm-dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                                new AddExpenseTask(getContext()).execute(store_id,sdf.format(myCalendar.getTime()),name.getText().toString(),description.getText().toString(),amount.getText().toString(),user_id);
                            }
                        } else {
                            action_bar.setText("amount should be in number format!");
                        }
                    }

                }else {
                    action_bar.setText("please select store!");
                }
            }

        });

        if(isOnline())
            new StoreLoadingTask(getContext()).execute();
        else
            Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_SHORT).show();

        return view;
    }

    public class AddExpenseTask extends AsyncTask<String, Void, String>{

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
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                }
            } else
            {
                Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
                if (this.dialog != null) {
                    this.dialog.dismiss();
                }
            }
        }
    }

    public class StoreLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public StoreLoadingTask(Context ctx) {
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
        protected String doInBackground(Void... voids) {
            HttpHandler httpHandler = new HttpHandler();
            return httpHandler.makeServiceCall(store_url);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);

            storesSting.add("select store");
            storeDetails = new ArrayList<>();

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("stores");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stores.add(new Store(jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("name"),
                                    jsonArray.getJSONObject(i).getString("location")));
                            storesSting.add(jsonArray.getJSONObject(i).getString("name"));
                            storeDetails.add(new Store(jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("location")));
                        }
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, storesSting);
                        store_spinner.setAdapter(adapter);

                        store_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);

                    } else {
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }
                        Toast.makeText(context, "Oops... No stores found!", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    if(this.dialog != null)
                        dialog.dismiss();
                    Toast.makeText(context, "Oops... Something went wrong!", Toast.LENGTH_LONG).show();
                }

            } else {
                if (this.dialog != null) {
                    this.dialog.dismiss();
                }
                Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void updateLabel(){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        datepicker.setText(sdf.format(myCalendar.getTime()));
    }

    public void getStoreUserId(String store_id,String user_id){
        this.store_id = store_id;
        this.user_id = user_id;
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}

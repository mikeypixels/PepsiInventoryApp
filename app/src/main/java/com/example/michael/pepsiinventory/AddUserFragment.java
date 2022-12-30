package com.example.michael.pepsiinventory;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment {

    RadioButton malebtn, femalebtn;
    String gender;
    Button button;
    EditText f_name, l_name,username;
    TextView txt;
    Spinner spinner, store_spinner;
    String store_url,user_url,store_name,store_id;
    ArrayList<Store> stores = new ArrayList<>();
    ArrayList<String> storesSting = new ArrayList<>();
    final String TAG = AddUserFragment.class.getSimpleName();

    public AddUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        malebtn = view.findViewById(R.id.male_rbtn);
        femalebtn = view.findViewById(R.id.female_rbtn);
        button = view.findViewById(R.id.send1);
        f_name = view.findViewById(R.id.first_name);
        l_name = view.findViewById(R.id.last_name);
        username = view.findViewById(R.id.username);
        txt = view.findViewById(R.id.action_txt);
        spinner = view.findViewById(R.id.spinner);
        store_spinner = view.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(), R.array.role, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);

        spinner.setAdapter(adapter);

        store_url = getString(R.string.serve_url) + "stores";
        user_url = getString(R.string.serve_url) + "add/user";

        malebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                femalebtn.setChecked(false);
                gender = "male";
            }
        });

        femalebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                malebtn.setChecked(false);
                gender = "female";
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (f_name.getText().toString().isEmpty() || l_name.getText().toString().isEmpty()|| username.getText().toString().isEmpty()) {
                    txt.setText("please fill all fields!");
                } else {
                    if (!malebtn.isChecked() && !femalebtn.isChecked())
                        txt.setText("please choose gender!");
                    else {
                        if(spinner.getSelectedItem().toString().equals("select role")||store_spinner.getSelectedItem().toString().equals("select store")){
                            txt.setText("please choose both store and role");
                        }
                        else{
                            if(isOnline()){
                                store_name = store_spinner.getSelectedItem().toString();

                                Log.d(TAG,"OnClick: " + store_name);

                                for(Store store: stores){
                                    if(store_name.equals(store.getStore_name()))
                                        store_id = store.getStore_id();
                                }

                                Log.d(TAG,"OnClick: " + store_id);

                                if(spinner.getSelectedItem().toString().equals("Admin")){
                                    store_id = "0";
                                }

                                if(malebtn.isChecked())
                                    new AddUserTask(getContext()).execute(f_name.getText().toString(),l_name.getText().toString(),username.getText().toString(),gender,store_id,spinner.getSelectedItem().toString());
                                else
                                    new AddUserTask(getContext()).execute(f_name.getText().toString(),l_name.getText().toString(),username.getText().toString(),gender,store_id,spinner.getSelectedItem().toString());

                            }else{
                                Toast.makeText(getContext(), "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                            txt.setText("");
                        }

                    }
                }
            }
        });

        if (isOnline()) {
            new StoreLoadingTask(getContext()).execute();
        } else {
            Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public class AddUserTask extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = AddUserTask.class.getSimpleName();

        public AddUserTask(Context ctx) {
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
            String first_name = strings[0];
            String last_name = strings[1];
            String username = strings[2];
            String gender = strings[3];
            String store_id = strings[4];
            String role = strings[5];
            String password = username + "123";
            String status = "active";

            try {
                URL url = new URL(user_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("f_name", "UTF-8") + "=" + URLEncoder.encode(first_name, "UTF-8") + "&" +
                        URLEncoder.encode("l_name", "UTF-8") + "=" + URLEncoder.encode(last_name, "UTF-8") + "&" +
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8")+ "&" +
                        URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(store_id, "UTF-8")+ "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")+ "&" +
                        URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8")+ "&" +
                        URLEncoder.encode("role", "UTF-8") + "=" + URLEncoder.encode(role, "UTF-8");
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
                if (result.contains("Created")) {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    f_name.setText("");
                    l_name.setText("");
                    username.setText("");
                    malebtn.setChecked(false);
                    femalebtn.setChecked(false);
                    store_spinner.setSelection(0);
                    spinner.setSelection(0);
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                } else {
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                    Toast.makeText(context, "Oops... Something went wrong!", Toast.LENGTH_LONG).show();
                }
            } else

            {
                if (this.dialog != null) {
                    this.dialog.dismiss();
                }
                Toast.makeText(context, "Oops... Something went wrong!", Toast.LENGTH_LONG).show();
            }
        }

    }

    public class StoreLoadingTask extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = LoginActivity.LoginTask.class.getSimpleName();

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

            storesSting.clear();

            storesSting.add("select store");

            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stores.add(new Store(jsonArray.getJSONObject(i).getString("store_id"),
                                    jsonArray.getJSONObject(i).getString("store_name"),
                                    jsonArray.getJSONObject(i).getString("location"),
                                    jsonArray.getJSONObject(i).getString("store_type")));
                            storesSting.add(jsonArray.getJSONObject(i).getString("store_name"));
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

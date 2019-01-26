package com.example.michael.pepsiinventory;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class AddStoreFragment extends Fragment {

    EditText storeName,location;
    Button button;
    TextView textView;
    String store_url,storename,locationname;

    public AddStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_store, container, false);

        storeName = view.findViewById(R.id.store_name);
        location = view.findViewById(R.id.location);

        button = view.findViewById(R.id.send2);
        textView = view.findViewById(R.id.action_store);

        store_url = getString(R.string.serve_url) + "add_store.php";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storename = storeName.getText().toString();
                locationname = location.getText().toString();
                if(storename.isEmpty()||locationname.isEmpty())
                    textView.setText("please fill the field!");
                else {
                    textView.setText("");
                    if (isOnline()) {
                        new AddStoreTask(getContext()).execute(storename, locationname);
                    } else {
                        Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    public class AddStoreTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;
        String TAG = AddStoreFragment.class.getSimpleName();

        public AddStoreTask(Context ctx){
            this.context = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            String store_name = strings[0];
            String location_name = strings[1];

            try {
                URL url = new URL(store_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("storename", "UTF-8") + "=" + URLEncoder.encode(store_name, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location_name, "UTF-8");
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
                if (result.contains("Successful")) {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                } else {
                    Toast.makeText(context, "adding was unsuccessful", Toast.LENGTH_LONG).show();
                }
            } else
            {
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

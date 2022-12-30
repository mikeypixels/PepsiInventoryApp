package com.example.michael.pepsiinventory;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
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

    EditText storeName, location;
    Button button;
    TextView textView;
    String store_url, storename, locationname;

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

        store_url = getString(R.string.serve_url) + "store/add";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Alert");
                builder.setMessage("Choose store category!");

                storename = storeName.getText().toString();
                locationname = location.getText().toString();

                if (storename.isEmpty() || locationname.isEmpty())
                    textView.setText("please fill the field!");
                else {

                    builder.setPositiveButton("Soda store", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            textView.setText("");
                            if (isOnline()) {
                                new AddStoreTask(getContext()).execute(storename, locationname, "soda");
                            } else {
                                Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    builder.setNegativeButton("Water store", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            textView.setText("");
                            if (isOnline()) {
                                new AddStoreTask(getContext()).execute(storename, locationname, "water");
                            } else {
                                Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });

        return view;
    }

    public class AddStoreTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;
        String TAG = AddStoreFragment.class.getSimpleName();

        public AddStoreTask(Context ctx) {
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

            String store_name = strings[0];
            String location_name = strings[1];
            String store_type = strings[2];

            try {
                URL url = new URL(store_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("store_name", "UTF-8") + "=" + URLEncoder.encode(store_name, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location_name, "UTF-8") + "&" +
                        URLEncoder.encode("store_type", "UTF-8") + "=" + URLEncoder.encode(store_type, "UTF-8");
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

            if (result != null) {
                if (result.contains("Added")) {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    storeName.setText("");
                    location.setText("");
                    dialog.dismiss();
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                } else {
                    if (this.dialog != null)
                        dialog.dismiss();
                    Toast.makeText(context, "adding was unsuccessful", Toast.LENGTH_LONG).show();
                }
            } else {
                if (this.dialog != null)
                    dialog.dismiss();
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

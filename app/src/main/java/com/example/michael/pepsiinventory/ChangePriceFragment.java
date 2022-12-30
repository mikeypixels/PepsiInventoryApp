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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
public class ChangePriceFragment extends Fragment {

    Spinner spinner;
    EditText editText;
    Button button;
    TextView textView;
    String product_id, price_change_url;

    public ChangePriceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_price, container, false);

        spinner = view.findViewById(R.id.product_spinner);
        editText = view.findViewById(R.id.price);
        button = view.findViewById(R.id.submit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.all_products, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinner.getSelectedItem().toString().equals("Crate")){
                    product_id = "1";
                }else if(spinner.getSelectedItem().toString().equals("Full shell")){
                    product_id = "2";
                }else if(spinner.getSelectedItem().toString().equals("Bottle")){
                    product_id = "3";
                }else if(spinner.getSelectedItem().toString().equals("Takeaway")){
                    product_id = "4";
                }else if(spinner.getSelectedItem().toString().equals("Maji makubwa"))
                    product_id = "5";
                else if(spinner.getSelectedItem().toString().equals("Maji madogo"))
                    product_id = "6";
                else if(spinner.getSelectedItem().toString().equals("Soda"))
                    product_id = "7";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(spinner.getSelectedItem().toString().equals("select product")){
                textView.setText("please select product!");
            }else {
                if(editText.getText().toString().isEmpty()){
                    textView.setText("please the price field!");
                }else{
                    if (isOnline()) {
                        new ChangePriceTask(getContext()).execute(product_id, editText.getText().toString().replaceAll(",",""));
                    } else
                        Toast.makeText(getContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
            }
        });

        return view;
    }

    public class ChangePriceTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;
        String TAG = ChangePriceFragment.class.getSimpleName();

        public ChangePriceTask(Context ctx){
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

            String product_id = strings[0];
            String price = strings[1];

            price_change_url = getString(R.string.serve_url) + "price/edit/" + product_id;

            try {
                URL url = new URL(price_change_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(product_id, "UTF-8") + "&" +
                        URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(price, "UTF-8");
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
                if (result.contains("Updated")) {
                    String[] userDetails = result.split("-");
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    spinner.setSelection(0);
                    editText.setText("");
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                } else {
                    if(this.dialog != null)
                        dialog.dismiss();
                    Toast.makeText(context, "Oops... Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else
            {
                if(this.dialog != null)
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

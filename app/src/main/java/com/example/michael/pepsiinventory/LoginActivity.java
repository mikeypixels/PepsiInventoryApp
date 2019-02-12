package com.example.michael.pepsiinventory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
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

public class LoginActivity extends AppCompatActivity {

    Button button;
    String login_url, username, password;
    EditText usernameEdit, passwordEdit;
    SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.submit);
        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.password);

        login_url = getString(R.string.serve_url) + "login.php";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if (username.isEmpty() || password.isEmpty())
                    Toast.makeText(LoginActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                else {
//                    if (isOnline()) {
                        new LoginTask(LoginActivity.this).execute(username, password);
//                    } else {
//                        Toast.makeText(LoginActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
    }

    public class LoginTask extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = LoginTask.class.getSimpleName();

        public LoginTask(Context ctx) {
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
            String login_name = strings[0];
            String login_pass = strings[1];

            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(login_name, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(login_pass, "UTF-8");
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
                    String[] userDetails = result.split("-");

                    Log.d(TAG, "OnLoginReceive: " + userDetails[4]);
                    if(userDetails[4].equals("active")) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("f_name", userDetails[1]);
                        intent.putExtra("l_name", userDetails[2]);
                        intent.putExtra("role", userDetails[3]);
                        intent.putExtra("status", userDetails[4]);
                        intent.putExtra("user_id", userDetails[5]);
                        intent.putExtra("store_id", userDetails[6]);
                        startActivity(intent);
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                        finish();
                    }else{
                        if (this.dialog != null) {
                            this.dialog.dismiss();
                        }
                        Toast.makeText(context, "Access denied!", Toast.LENGTH_SHORT).show();
                    }

                    myPrefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString("first_name",userDetails[1]);
                    editor.putString("last_name",userDetails[2]);
                    editor.putString("role",userDetails[3]);
                    editor.putString("store_id",userDetails[6]);
                    editor.putString("user_id",userDetails[5]);

                    editor.apply();

                } else {
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                    Toast.makeText(context, "Oops... Wrong username or password", Toast.LENGTH_LONG).show();
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
        String TAG = LoginActivity.class.getSimpleName();
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        Log.d(TAG, "OnReceiveNetInfo: " + netInfo.getExtraInfo());
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}

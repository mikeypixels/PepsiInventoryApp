package com.example.michael.pepsiinventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

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

public class LoginActivity extends AppCompatActivity {

    Button button;
    String login_url, username, password;
    EditText usernameEdit, passwordEdit;
    SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_login);
        setupWindowAnimations();

        button = findViewById(R.id.submit);
        usernameEdit = findViewById(R.id.username_login);
        passwordEdit = findViewById(R.id.password);

        login_url = getString(R.string.serve_url) + "login";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        String userString = preferences.getString("first_name", "");

        if(!userString.equals("")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

//        "http://192.168.43.174/pepsi/login.php"

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if (username.isEmpty() || password.isEmpty())
                    Toast.makeText(LoginActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                else {
                    if (isOnline()) {
                        new LoginTask(LoginActivity.this).execute(username, password);
                    } else {
//                        Toast.makeText(LoginActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(500);
        getWindow().setExitTransition(slide);
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

            Log.d(TAG,"OnReceiveValues: " + login_name + " " + login_pass);

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

            JSONObject jsonObject;
            try {

                if (result != null)
                {
                    jsonObject = new JSONObject(result);
                    JSONObject userDetails = jsonObject.getJSONObject("result");

                    if (jsonObject.getString("status").equals("true")) {
                        if(userDetails.getString("status").equals("active")) {
                            myPrefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.putString("first_name",userDetails.getString("f_name"));
                            editor.putString("last_name",userDetails.getString("l_name"));
                            editor.putString("role",userDetails.getString("role"));
                            editor.putString("store_id",userDetails.getString("store_id"));
                            editor.putString("user_id",userDetails.getString("id"));

                            editor.apply();

                            if(userDetails.getString("role").equals("Main Admin")||userDetails.getString("role").equals("Admin")){
                                FirebaseMessaging.getInstance().subscribeToTopic("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String msg = "Subscription is successful!";
                                        if(!task.isSuccessful()){
                                            msg = "Subscription is unsuccessful!";
                                        }
                                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("f_name", userDetails.getString("f_name"));
                            intent.putExtra("l_name", userDetails.getString("l_name"));
                            intent.putExtra("role", userDetails.getString("role"));
                            intent.putExtra("status", userDetails.getString("status"));
                            intent.putExtra("user_id", userDetails.getString("id"));
                            intent.putExtra("store_id", userDetails.getString("store_id"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                            finish();

                            if (this.dialog != null) {
                                this.dialog.dismiss();
                            }

                        }else{
                            if (this.dialog != null) {
                                this.dialog.dismiss();
                            }
                            Toast.makeText(context, "Access denied!", Toast.LENGTH_SHORT).show();
                        }

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
                    Toast.makeText(context, "Oops... Wrong username or password", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                if (this.dialog != null) {
                    this.dialog.dismiss();
                }
                Toast.makeText(context,"Oops... Wrong username or password", Toast.LENGTH_LONG).show();
//                return;
            }

        }

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        Log.d(TAG, "OnReceiveNetInfo: " + netInfo.getExtraInfo());
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

package com.example.michael.pepsiinventory;

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
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class UserListTableAdapter extends RecyclerView.Adapter<UserListTableAdapter.UserListViewHolder> {

    private ArrayList<User> userRowArrayList;
    private ArrayList<User> userArrayList;
    Context context;
    TextView user_no, f_name, l_name, role, status;
    Button edit_btn, status_btn, delete_btn, save_btn, cancel_btn;
    String user_update_url;
    String user_delete_url;
    TextView usr_id, ustatus;
    EditText fName, lName, urole;
    RadioButton adminbtn, workerbtn;
    String new_role, u_status;
    UserInterface userInterface;
    int position;

    public UserListTableAdapter(Context context, ArrayList<User> userList, UserInterface userInterface) {
        this.context = context;
        userRowArrayList = userList;
        this.userInterface = userInterface;
    }

    public static class UserListViewHolder extends RecyclerView.ViewHolder {

        TextView user_id, f_name, l_name, gender, status;
        ConstraintLayout tableRow;

        public UserListViewHolder(View itemView) {
            super(itemView);

            user_id = itemView.findViewById(R.id.no);
            f_name = itemView.findViewById(R.id.product_name);
            l_name = itemView.findViewById(R.id.quantity);
            gender = itemView.findViewById(R.id.amount);
            status = itemView.findViewById(R.id.date);
            tableRow = itemView.findViewById(R.id.tableRow1);

        }
    }

    @NonNull
    @Override
    public UserListTableAdapter.UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserListTableAdapter.UserListViewHolder(LayoutInflater.from(context).inflate(R.layout.user_list_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListTableAdapter.UserListViewHolder userListViewHolder, int i) {

        if (i == 0) {
            userListViewHolder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            userListViewHolder.tableRow.setPadding(13, 13, 13, 13);
//            expenseViewHolder.tableRow.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            userListViewHolder.user_id.setText("US/id");
            userListViewHolder.user_id.setTextColor(Color.parseColor("#ffffff"));
            userListViewHolder.f_name.setText("first name");
//            expenseViewHolder.expense_name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            userListViewHolder.f_name.setTextColor(Color.parseColor("#ffffff"));
            userListViewHolder.l_name.setText("last name");
            userListViewHolder.l_name.setTextColor(Color.parseColor("#ffffff"));
            userListViewHolder.gender.setText("user role");
            userListViewHolder.gender.setTextColor(Color.parseColor("#ffffff"));
            userListViewHolder.status.setText("status");
            userListViewHolder.status.setTextColor(Color.parseColor("#ffffff"));

        } else if (i > 0) {

            userListViewHolder.tableRow.setPadding(13, 13, 13, 13);
//            expenseViewHolder.tableRow.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            userListViewHolder.user_id.setTextColor(Color.parseColor("#000000"));
//            expenseViewHolder.expense_name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            userListViewHolder.f_name.setTextColor(Color.parseColor("#000000"));
            userListViewHolder.l_name.setTextColor(Color.parseColor("#000000"));
            userListViewHolder.gender.setTextColor(Color.parseColor("#000000"));
//            userListViewHolder.gender.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            userListViewHolder.status.setTextColor(Color.parseColor("#000000"));

//            Log.d(TAG, "value" + i);
            userListViewHolder.user_id.setText(userRowArrayList.get(i - 1).getUser_id());
            userListViewHolder.f_name.setText(userRowArrayList.get(i - 1).getF_name());
            userListViewHolder.l_name.setText(userRowArrayList.get(i - 1).getL_name());
            userListViewHolder.gender.setText(userRowArrayList.get(i - 1).getRole());
            userListViewHolder.status.setText(userRowArrayList.get(i - 1).getStatus());
//            userListViewHolder.status.setBackgroundColor(Color.parseColor("#228B22"));

            final User user = userRowArrayList.get(i - 1);

            userListViewHolder.tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                            Intent intent = new Intent(context,PopUpActivity.class);
//                            intent.putExtra("message","animation beauty");
//                            intent.putExtra("sale",new Gson().toJson(user));
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);
                    final Dialog dialognew = new Dialog(context);
                    DisplayMetrics dm = context.getResources().getDisplayMetrics();
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    dialognew.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialognew.setContentView(R.layout.user_layout);
                    dialognew.getWindow().setLayout((int) (width * .9), (int) (height * .6));
                    dialognew.setCancelable(true);

                    user_no = dialognew.findViewById(R.id.user_no);
                    f_name = dialognew.findViewById(R.id.f_name);
                    l_name = dialognew.findViewById(R.id.l_name);
                    role = dialognew.findViewById(R.id.u_role);
                    status = dialognew.findViewById(R.id.status);
                    edit_btn = dialognew.findViewById(R.id.edit);
                    delete_btn = dialognew.findViewById(R.id.delete);
                    status_btn = dialognew.findViewById(R.id.status_btn);

                    user_no.setText(user.getUser_id());
                    f_name.setText(user.getF_name());
                    l_name.setText(user.getL_name());
                    role.setText(user.getRole());
                    status.setText(user.getStatus());

                    if (status.getText().toString().toLowerCase().equals("active")) {
                        status_btn.setText("Deactivate");
                        status_btn.setBackgroundResource(R.drawable.deactive_bg);
                    } else {
                        status_btn.setText("Activate");
                        status_btn.setBackgroundResource(R.drawable.active_bg);
                        status.setText("deactive");
                    }

                    dialognew.show();

                    if (!user.getRole().equals("Main Admin")) {
                        status_btn.setVisibility(View.VISIBLE);
                    } else {
                        status_btn.setVisibility(View.GONE);
                    }

                    status_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (status_btn.getText().toString().toLowerCase().equals("activate")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setTitle("Alert");
                                builder.setMessage("Are you sure you want to activate this user?");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(isOnline()) {
                                            userRowArrayList.get(userListViewHolder.getAdapterPosition() - 1).setStatus("active");
                                            u_status = "active";
                                            new UpdateUserTask(context).execute(user_no.getText().toString(), f_name.getText().toString(), l_name.getText().toString(), role.getText().toString(), u_status);
                                        }else{
                                            Toast.makeText(context, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();

                                status_btn.setText("Deactivate");
                                status_btn.setBackgroundResource(R.drawable.deactive_bg);
                                status.setText("active");

                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setTitle("Alert");
                                builder.setMessage("Are you sure you want to deactivate this user?");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(isOnline()) {
                                            userRowArrayList.get(userListViewHolder.getAdapterPosition() - 1).setStatus("deactive");
                                            u_status = "deactive";
                                            new UpdateUserTask(context).execute(user_no.getText().toString(), f_name.getText().toString(), l_name.getText().toString(), role.getText().toString(), u_status);
                                        } else{
                                            Toast.makeText(context, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();

                                status_btn.setText("Activate");
                                status_btn.setBackgroundResource(R.drawable.active_bg);
                                status.setText("deactive");
                            }
                        }
                    });

                    edit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialognew.setContentView(R.layout.user_edit_layout);

                            usr_id = dialognew.findViewById(R.id.user_no);
                            usr_id.setText(user.getUser_id());
                            fName = dialognew.findViewById(R.id.f_name);
                            fName.setText(user.getF_name());
                            lName = dialognew.findViewById(R.id.l_name);
                            lName.setText(user.getL_name());
                            adminbtn = dialognew.findViewById(R.id.admin);
                            workerbtn = dialognew.findViewById(R.id.worker);
                            if (user.getRole().equals("Admin") || user.getRole().equals("Main Admin"))
                                adminbtn.setChecked(true);
                            else
                                workerbtn.setChecked(true);

                            adminbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    workerbtn.setChecked(false);
                                    new_role = "Admin";
                                }
                            });

                            workerbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    adminbtn.setChecked(false);
                                    new_role = "Worker";
                                }
                            });

                            ustatus = dialognew.findViewById(R.id.status_txt);
                            ustatus.setText(status.getText().toString());
                            save_btn = dialognew.findViewById(R.id.save);
                            cancel_btn = dialognew.findViewById(R.id.cancel);

                            save_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    position = userListViewHolder.getAdapterPosition() - 1;
                                    if (!fName.getText().toString().isEmpty() || !lName.getText().toString().isEmpty()) {
                                        if(isOnline()) {
                                            userRowArrayList.get(userListViewHolder.getAdapterPosition() - 1).setF_name(fName.getText().toString());
                                            userRowArrayList.get(userListViewHolder.getAdapterPosition() - 1).setL_name(lName.getText().toString());
                                            if(adminbtn.isChecked()) {
                                                userRowArrayList.get(userListViewHolder.getAdapterPosition() - 1).setRole(adminbtn.getText().toString());
                                                new UpdateUserTask(context).execute(usr_id.getText().toString(), fName.getText().toString(), lName.getText().toString(), adminbtn.getText().toString(), ustatus.getText().toString());
                                            }
                                            else if(workerbtn.isChecked()) {
                                                userRowArrayList.get(userListViewHolder.getAdapterPosition() - 1).setRole(workerbtn.getText().toString());
                                                new UpdateUserTask(context).execute(usr_id.getText().toString(), fName.getText().toString(), lName.getText().toString(), workerbtn.getText().toString(), ustatus.getText().toString());
                                            }
                                        } else
                                            Toast.makeText(context, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                        dialognew.dismiss();
                                    } else {
                                        Toast.makeText(context, "please fill all fields", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            cancel_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialognew.dismiss();
                                }
                            });

                        }
                    });

                    if (!user.getRole().equals("Main Admin")) {
                        delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setTitle("Alert");
                                builder.setMessage("Are you sure you want to delete the user?");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        position = userListViewHolder.getAdapterPosition() - 1;
                                        if (isOnline())
                                            new UserDeleteTask(context).execute(user_no.getText().toString());
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

                            }
                        });
                    } else {
                        delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Nothing happens
                            }
                        });
                    }


                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userRowArrayList.size() + 1;
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        Log.d(TAG, "OnReceiveNetInfo: " + netInfo.getExtraInfo());
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public class UpdateUserTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;
        String TAG = UserListTableAdapter.class.getSimpleName();

        public UpdateUserTask(Context ctx) {
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

            String user_id = strings[0];
            String user_fname = strings[1];
            String user_lname = strings[2];
            String user_role = strings[3];
            String user_status = strings[4];

            user_update_url = this.context.getString(R.string.serve_url) + "user/edit/" + user_id;

            try {
                URL url = new URL(user_update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                        URLEncoder.encode("f_name", "UTF-8") + "=" + URLEncoder.encode(user_fname, "UTF-8") + "&" +
                        URLEncoder.encode("l_name", "UTF-8") + "=" + URLEncoder.encode(user_lname, "UTF-8") + "&" +
                        URLEncoder.encode("role", "UTF-8") + "=" + URLEncoder.encode(user_role, "UTF-8") + "&" +
                        URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(user_status, "UTF-8");
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
                    userArrayList = userRowArrayList;
                    userRowArrayList = new ArrayList<>();
                    userRowArrayList.addAll(userArrayList);
                    notifyItemRangeChanged(position, getItemCount());
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

    public class UserDeleteTask extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog dialog;
        String TAG = UserListTableAdapter.class.getSimpleName();
//        String TAG = LoginActivity.LoginTask.class.getSimpleName();

        public UserDeleteTask(Context ctx) {
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

            String user_id = strings[0];
            user_delete_url = this.context.getString(R.string.serve_url) + "user/delete/" + user_id;

            try {
                URL url = new URL(user_delete_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
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
            return httpHandler.makeServiceDelete(user_delete_url);
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {
                if (result.contains("Deleted")) {
                    userInterface.getPosition(userRowArrayList.get(position));
                    userRowArrayList.remove(userRowArrayList.get(position));
                    notifyItemRemoved(position);
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }

                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
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

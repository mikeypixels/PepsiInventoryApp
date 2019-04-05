package com.example.michael.pepsiinventory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class StoreListTableAdapter extends RecyclerView.Adapter<StoreListTableAdapter.StoreListViewHolder>{

    Context context;
    ArrayList<Store> storeRowArrayList;
    ArrayList<Store> storeArrayList;
    TextView store_id,store_name,location;
    TextView st_id,st_name,loc;
    Button edit_btn,save_btn,cancel_btn;
    Spinner spinner;
    String store_update_url;
    String store_delete_url;
    int position;

    public StoreListTableAdapter(Context context,ArrayList<Store> arrayList) {
        this.context = context;
        this.storeRowArrayList = arrayList;
    }

    public static class StoreListViewHolder extends RecyclerView.ViewHolder{

        TextView store_id,store_name,location;
        ConstraintLayout tableRow;

        public StoreListViewHolder(View itemView){
            super(itemView);
            store_id = itemView.findViewById(R.id.store_id);
            store_name = itemView.findViewById(R.id.store_name);
            location = itemView.findViewById(R.id.location);
            tableRow = itemView.findViewById(R.id.tableRow1);

        }
    }

    @NonNull
    @Override
    public StoreListTableAdapter.StoreListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StoreListTableAdapter.StoreListViewHolder(LayoutInflater.from(context).inflate(R.layout.store_table_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull final StoreListTableAdapter.StoreListViewHolder storeListViewHolder, int i) {
        if(i==0){
            storeListViewHolder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            storeListViewHolder.tableRow.setPadding(13, 13, 13, 13);
            storeListViewHolder.store_id.setText("ST/#");
            storeListViewHolder.store_id.setTextColor(Color.parseColor("#ffffff"));
            storeListViewHolder.store_name.setText("store name");
            storeListViewHolder.store_name.setTextColor(Color.parseColor("#ffffff"));
            storeListViewHolder.location.setText("location");
            storeListViewHolder.location.setTextColor(Color.parseColor("#ffffff"));

        }else if(i>0){
            storeListViewHolder.tableRow.setBackgroundColor(Color.parseColor("#efefef"));
            storeListViewHolder.tableRow.setPadding(13, 13, 13, 13);
            storeListViewHolder.store_id.setTextColor(Color.parseColor("#000000"));
            storeListViewHolder.store_name.setTextColor(Color.parseColor("#000000"));
            storeListViewHolder.location.setTextColor(Color.parseColor("#000000"));

//            Log.d(TAG, "value" + i);
            storeListViewHolder.store_id.setText(storeRowArrayList.get(i-1).getStore_id());
            storeListViewHolder.store_name.setText(storeRowArrayList.get(i-1).getStore_name());
            storeListViewHolder.location.setText(storeRowArrayList.get(i-1).getLocation());
//            userListViewHolder.status.setBackgroundColor(Color.parseColor("#228B22"));

            final Store salesRow = storeRowArrayList.get(i-1);

            storeListViewHolder.tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                            Intent intent = new Intent(context,PopUpActivity.class);
//                            intent.putExtra("message","animation beauty");
//                            intent.putExtra("sale",new Gson().toJson(salesRow));
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);
                    final Dialog dialognew = new Dialog(context);
                    DisplayMetrics dm = context.getResources().getDisplayMetrics();
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    dialognew.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialognew.setContentView(R.layout.store_layout);
                    dialognew.getWindow().setLayout((int) (width * .9), (int) (height * .5));
                    dialognew.setCancelable(true);

                    store_id = dialognew.findViewById(R.id.store_id);
                    store_name = dialognew.findViewById(R.id.store_name);
                    location = dialognew.findViewById(R.id.location);
                    edit_btn = dialognew.findViewById(R.id.edit);
//                    delete_btn = dialognew.findViewById(R.id.delete);

                    store_id.setText(salesRow.getStore_id());
                    store_name.setText(salesRow.getStore_name());
                    location.setText(salesRow.getLocation());

                    dialognew.show();

                    edit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialognew.setContentView(R.layout.store_edit_layout);

                            st_id = dialognew.findViewById(R.id.store_id);
                            st_id.setText(salesRow.getStore_id());
                            st_name = dialognew.findViewById(R.id.store_name);
                            st_name.setText(salesRow.getStore_name());
                            loc = dialognew.findViewById(R.id.location);
                            loc.setText(salesRow.getLocation());
                            save_btn = dialognew.findViewById(R.id.save);
                            cancel_btn = dialognew.findViewById(R.id.cancel);

                            save_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    position = storeListViewHolder.getAdapterPosition()-1;
                                    if(isOnline()) {
                                        storeRowArrayList.get(storeListViewHolder.getAdapterPosition()-1).setStore_name(st_name.getText().toString());
                                        storeRowArrayList.get(storeListViewHolder.getAdapterPosition()-1).setLocation(loc.getText().toString());
                                        new UpdateStoreTask(context).execute(st_id.getText().toString(), st_name.getText().toString(), loc.getText().toString());
                                    } else
                                        Toast.makeText(context, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                                    dialognew.dismiss();
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

//                    delete_btn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.d(TAG,"It reaches here for some good reason");
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//                            builder.setTitle("Alert");
//                            builder.setMessage("Are you sure you want to delete the store?");
//
//                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    position = storeListViewHolder.getAdapterPosition()-1;
//                                    if(isOnline())
//                                        new StoreDeleteTask(context).execute(store_id.getText().toString());
//                                    else
//                                        Toast.makeText(context, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
//                                    dialognew.dismiss();
//                                }
//                            });
//
//                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            });
//
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//
//                        }
//                    });


                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return storeRowArrayList.size()+1;
    }

    public class UpdateStoreTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;

        public UpdateStoreTask(Context ctx) {
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

            String id = strings[0];
            String store_name = strings[1];
            String store_location = strings[2];

            store_update_url = this.context.getString(R.string.serve_url) + "store/edit/" + id;

            try {
                URL url = new URL(store_update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                        URLEncoder.encode("store_name", "UTF-8") + "=" + URLEncoder.encode(store_name, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(store_location, "UTF-8");
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

                    storeArrayList = storeRowArrayList;
                    storeRowArrayList = new ArrayList<>();
                    storeRowArrayList.addAll(storeArrayList);
                    notifyItemRangeChanged(position,getItemCount());

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

}

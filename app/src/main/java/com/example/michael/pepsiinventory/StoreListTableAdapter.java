package com.example.michael.pepsiinventory;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class StoreListTableAdapter extends RecyclerView.Adapter<StoreListTableAdapter.StoreListViewHolder>{

    Context context;
    ArrayList<Store> storeRowArrayList;
    TextView store_id,store_name,location;
    TextView st_id,st_name,loc;
    Button edit_btn,delete_btn,save_btn,cancel_btn;
    int j = 0;

    public StoreListTableAdapter(Context context, ArrayList<Store> storeList) {
        this.context = context;
        storeRowArrayList = storeList;
    }

    public static class StoreListViewHolder extends RecyclerView.ViewHolder{

        TextView store_id,store_name,location;
        LinearLayout tableRow;

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
    public void onBindViewHolder(@NonNull StoreListTableAdapter.StoreListViewHolder storeListViewHolder, int i) {
        if(i==0){
            storeListViewHolder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            storeListViewHolder.tableRow.setPadding(13, 13, 13, 13);
//            expenseViewHolder.tableRow.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            storeListViewHolder.store_id.setText("ST/#");
            storeListViewHolder.store_id.setTextColor(Color.parseColor("#ffffff"));
            storeListViewHolder.store_name.setText("store name");
            storeListViewHolder.store_name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            storeListViewHolder.store_name.setTextColor(Color.parseColor("#ffffff"));
            storeListViewHolder.location.setText("location");
            storeListViewHolder.location.setTextColor(Color.parseColor("#ffffff"));

        }else if(i>0){
            storeListViewHolder.tableRow.setBackgroundColor(Color.parseColor("#efefef"));
            storeListViewHolder.tableRow.setPadding(13, 13, 13, 13);
//            expenseViewHolder.tableRow.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            storeListViewHolder.store_id.setTextColor(Color.parseColor("#000000"));
            storeListViewHolder.store_name.setTextColor(Color.parseColor("#000000"));
            storeListViewHolder.store_name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
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
                    Dialog dialognew = new Dialog(context);
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
                    delete_btn = dialognew.findViewById(R.id.delete);

                    store_id.setText(salesRow.getStore_id());
                    store_name.setText(salesRow.getStore_name());
                    location.setText(salesRow.getLocation());

                    dialognew.show();

                    final Dialog editDialog = dialognew;

                    edit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.setContentView(R.layout.store_edit_layout);

                            st_id = editDialog.findViewById(R.id.store_id);
                            st_id.setText(salesRow.getStore_id());
                            st_name = editDialog.findViewById(R.id.store_name);
                            st_name.setText(salesRow.getStore_name());
                            loc = editDialog.findViewById(R.id.location);
                            loc.setText(salesRow.getLocation());
                            save_btn = editDialog.findViewById(R.id.save);
                            cancel_btn = editDialog.findViewById(R.id.cancel);

                            save_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            cancel_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editDialog.dismiss();
                                }
                            });

                        }
                    });

                    delete_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });


                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return storeRowArrayList.size()+1;
    }
}

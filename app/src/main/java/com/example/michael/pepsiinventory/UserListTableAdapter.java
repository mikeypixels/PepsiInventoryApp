package com.example.michael.pepsiinventory;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class UserListTableAdapter extends RecyclerView.Adapter<UserListTableAdapter.UserListViewHolder>{

    private ArrayList<User> userRowArrayList;
    Context context;
    TextView user_no,f_name,l_name,role,status;
    Button edit_btn,status_btn,delete_btn,save_btn,cancel_btn;
    int j = 0;

    public UserListTableAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        userRowArrayList = userList;
    }

    public static class UserListViewHolder extends RecyclerView.ViewHolder{

        TextView user_id,f_name,l_name,gender,status;
        LinearLayout tableRow;

        public UserListViewHolder(View itemView){
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
        return new UserListTableAdapter.UserListViewHolder(LayoutInflater.from(context).inflate(R.layout.sales_table_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull UserListTableAdapter.UserListViewHolder userListViewHolder, int i) {

        if (i == 0) {
            userListViewHolder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            userListViewHolder.tableRow.setPadding(13, 13, 13, 13);
//            expenseViewHolder.tableRow.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            userListViewHolder.user_id.setText("US/#");
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

            userListViewHolder.tableRow.setBackgroundColor(Color.parseColor("#efefef"));
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

            final User salesRow = userRowArrayList.get(i-1);

            userListViewHolder.tableRow.setOnClickListener(new View.OnClickListener() {
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

                    user_no.setText(salesRow.getUser_id());
                    f_name.setText(salesRow.getF_name());
                    l_name.setText(salesRow.getL_name());
                    role.setText(salesRow.getRole());
                    status.setText(salesRow.getStatus());

                    if(status.getText().toString().toLowerCase().equals("active")){
                        status_btn.setText("Deactivate");
                        status_btn.setBackgroundResource(R.drawable.deactive_bg);
                    }
                    else{
                        status_btn.setText("Activate");
                        status_btn.setBackgroundResource(R.drawable.active_bg);
                        status.setText("deactive");
                    }

                    dialognew.show();

                    final Dialog editDialog = dialognew;

                    status_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(status_btn.getText().toString().toLowerCase().equals("activate")){
                                status_btn.setText("Deactivate");
                                status_btn.setBackgroundResource(R.drawable.deactive_bg);
                                status.setText("active");
                            }
                            else{
                                status_btn.setText("Activate");
                                status_btn.setBackgroundResource(R.drawable.active_bg);
                                status.setText("deactive");
                            }
                        }
                    });

                    edit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.setContentView(R.layout.user_edit_layout);

                            user_no = editDialog.findViewById(R.id.user_no);
                            user_no.setText(salesRow.getUser_id());
                            f_name = editDialog.findViewById(R.id.f_name);
                            f_name.setText(salesRow.getF_name());
                            l_name = editDialog.findViewById(R.id.l_name);
                            l_name.setText(salesRow.getL_name());
                            role = editDialog.findViewById(R.id.role);
                            role.setText(salesRow.getRole());
                            status = editDialog.findViewById(R.id.status);
                            status.setText(salesRow.getStatus());
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


                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userRowArrayList.size()+1;
    }
}

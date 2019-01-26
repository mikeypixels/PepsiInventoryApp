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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AdminSalesTableAdapter extends RecyclerView.Adapter<AdminSalesTableAdapter.AdminSalesViewHolder>{

    Context context;
    private SalesTableAdapter.OnItemClickListener mListener;
    private ArrayList<SalesRow> salesRowArrayList;
    private final static String TAG = SalesTableAdapter.class.getSimpleName();
    TextView sn, product, quant, amnt, datepick;
    Button editButton, deleteButton, saveButton, cancelButton;
    EditText pName, qtty, amt, dateV;
    TextView sNo;
    int j = 0;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(SalesTableAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public AdminSalesTableAdapter(Context context, ArrayList<SalesRow> salesRows) {
        this.context = context;
        salesRowArrayList = salesRows;
    }

    public static class AdminSalesViewHolder extends RecyclerView.ViewHolder {

        TextView no, product_name, quantity, amount, date;
        LinearLayout tableRow;

        public AdminSalesViewHolder(View itemView, final SalesTableAdapter.OnItemClickListener listener) {
            super(itemView);
            no = itemView.findViewById(R.id.no);
            product_name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            tableRow = itemView.findViewById(R.id.tableRow1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public AdminSalesTableAdapter.AdminSalesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.sales_table_layout, null);
        return new AdminSalesTableAdapter.AdminSalesViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdminSalesTableAdapter.AdminSalesViewHolder salesViewHolder, int i) {


        if (i == 0) {
            salesViewHolder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            salesViewHolder.no.setText("S/N");
            salesViewHolder.no.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.product_name.setText("product");
            salesViewHolder.product_name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            salesViewHolder.product_name.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.quantity.setText("quantity");
            salesViewHolder.quantity.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.quantity.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            salesViewHolder.amount.setText("amount");
            salesViewHolder.amount.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.amount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            salesViewHolder.date.setText("sales date");
            salesViewHolder.date.setTextColor(Color.parseColor("#ffffff"));
            salesViewHolder.date.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        } else {

            Log.d(TAG, "OnReceive : " + i);

            salesViewHolder.tableRow.setBackgroundColor(Color.parseColor("#efefef"));
            salesViewHolder.no.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.product_name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            salesViewHolder.product_name.setPadding(4,0,0,0);
            salesViewHolder.product_name.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.quantity.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.quantity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            salesViewHolder.amount.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.amount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            salesViewHolder.date.setTextColor(Color.parseColor("#000000"));
            salesViewHolder.date.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            salesViewHolder.no.setText(salesRowArrayList.get(i-1).getSn());
            salesViewHolder.product_name.setText(salesRowArrayList.get(i-1).getProduct_name());
            salesViewHolder.quantity.setText(salesRowArrayList.get(i-1).getQuantity());
            salesViewHolder.amount.setText(salesRowArrayList.get(i-1).getAmount());
            salesViewHolder.amount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            salesViewHolder.date.setText(salesRowArrayList.get(i-1).getDate());
            salesViewHolder.date.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            final SalesRow salesRow = salesRowArrayList.get(i-1);

//            if(j<salesRowArrayList.size())
//                j++;

            salesViewHolder.tableRow.setOnClickListener(new View.OnClickListener() {
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
                    dialognew.setContentView(R.layout.popup_window);
                    dialognew.getWindow().setLayout((int) (width * .9), (int) (height * .6));
                    dialognew.setCancelable(true);

                    sn = dialognew.findViewById(R.id.sn);
                    product = dialognew.findViewById(R.id.product_name);
                    quant = dialognew.findViewById(R.id.quantity);
                    amnt = dialognew.findViewById(R.id.amount);
                    datepick = dialognew.findViewById(R.id.date);
                    editButton = dialognew.findViewById(R.id.edit);
                    deleteButton = dialognew.findViewById(R.id.delete);

                    sn.setText(salesRow.getSn());
                    product.setText(salesRow.getProduct_name());
                    quant.setText(salesRow.getQuantity());
                    amnt.setText(salesRow.getAmount());
                    datepick.setText(salesRow.getDate());

                    dialognew.show();

                    final Dialog editDialog = dialognew;

                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.setContentView(R.layout.edit_layout);

                            sNo = editDialog.findViewById(R.id.sn);
                            sNo.setText(salesRow.getSn());
                            pName = editDialog.findViewById(R.id.product_name);
                            pName.setText(salesRow.getProduct_name());
                            qtty = editDialog.findViewById(R.id.quantity);
                            qtty.setText(salesRow.getQuantity());
                            amt = editDialog.findViewById(R.id.amount);
                            amt.setText(salesRow.getAmount());
                            dateV = editDialog.findViewById(R.id.date);
                            dateV.setText(salesRow.getDate());
                            saveButton = editDialog.findViewById(R.id.save);
                            cancelButton = editDialog.findViewById(R.id.cancel);

                            editButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            cancelButton.setOnClickListener(new View.OnClickListener() {
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
        return salesRowArrayList.size()+1;
    }
}

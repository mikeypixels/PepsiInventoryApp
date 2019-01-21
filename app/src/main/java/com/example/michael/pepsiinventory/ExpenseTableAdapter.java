package com.example.michael.pepsiinventory;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpenseTableAdapter extends RecyclerView.Adapter<ExpenseTableAdapter.ExpenseViewHolder> {

    Context context;
    private ArrayList<ExpenseRow> expenseRowArrayList = new ArrayList<>();

    TextView sn, product, description, amnt, datepick;
    Button editButton, deleteButton, saveButton, cancelButton;
    EditText pName, description0, qtty, amt, dateV;
    TextView sNo;

    final static public String TAG = ExpenseTableAdapter.class.getSimpleName();

    public ExpenseTableAdapter(Context context, ArrayList<ExpenseRow> expenseRows) {
        this.context = context;
        expenseRowArrayList = expenseRows;
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView no, expense_name, amount, date;
        LinearLayout tableRow;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            no = itemView.findViewById(R.id.no);
            expense_name = itemView.findViewById(R.id.expense_name);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            tableRow = itemView.findViewById(R.id.tableRow1);
        }
    }

    @NonNull
    @Override
    public ExpenseTableAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ExpenseViewHolder(LayoutInflater.from(context).inflate(R.layout.expense_table_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseTableAdapter.ExpenseViewHolder expenseViewHolder, int i) {
        if (i == 0) {
            expenseViewHolder.tableRow.setBackgroundColor(Color.parseColor("#222F48"));
            expenseViewHolder.tableRow.setPadding(13, 13, 13, 13);
//            expenseViewHolder.tableRow.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            expenseViewHolder.no.setText("S/N");
            expenseViewHolder.no.setTextColor(Color.parseColor("#ffffff"));
            expenseViewHolder.expense_name.setText("expense");
//            expenseViewHolder.expense_name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            expenseViewHolder.expense_name.setTextColor(Color.parseColor("#ffffff"));
            expenseViewHolder.amount.setText("amount");
            expenseViewHolder.amount.setTextColor(Color.parseColor("#ffffff"));
            expenseViewHolder.date.setText("expense date");
            expenseViewHolder.date.setTextColor(Color.parseColor("#ffffff"));
        } else {

            expenseViewHolder.tableRow.setBackgroundColor(Color.parseColor("#efefef"));
            expenseViewHolder.tableRow.setPadding(13, 13, 13, 13);
//            expenseViewHolder.tableRow.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            expenseViewHolder.no.setTextColor(Color.parseColor("#000000"));
//            expenseViewHolder.expense_name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            expenseViewHolder.expense_name.setTextColor(Color.parseColor("#000000"));
            expenseViewHolder.amount.setTextColor(Color.parseColor("#000000"));
            expenseViewHolder.date.setTextColor(Color.parseColor("#000000"));

            Log.d(TAG, "value" + i);
            expenseViewHolder.no.setText(expenseRowArrayList.get(i).getNo());
            expenseViewHolder.expense_name.setText(expenseRowArrayList.get(i).getExpense_name());
            expenseViewHolder.amount.setText(expenseRowArrayList.get(i).getAmount());
            expenseViewHolder.date.setText(expenseRowArrayList.get(i).getDate());

            final ExpenseRow expenseRow = expenseRowArrayList.get(i);

            expenseViewHolder.tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Intent intent = new Intent(context,PopUpActivity2.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);

                    Dialog dialognew = new Dialog(context);
                    DisplayMetrics dm = context.getResources().getDisplayMetrics();
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    dialognew.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialognew.setContentView(R.layout.activity_pop_up2);
                    dialognew.getWindow().setLayout((int) (width * .9), (int) (height * .6));
                    dialognew.setCancelable(true);

                    sn = dialognew.findViewById(R.id.sn);
                    product = dialognew.findViewById(R.id.product_name);
                    description = dialognew.findViewById(R.id.description);
                    amnt = dialognew.findViewById(R.id.amount);
                    datepick = dialognew.findViewById(R.id.date);
                    editButton = dialognew.findViewById(R.id.edit);
                    deleteButton = dialognew.findViewById(R.id.delete);

                    expenseRow.setDescription("");

                    sn.setText(expenseRow.getNo());
                    product.setText(expenseRow.getExpense_name());
                    amnt.setText(expenseRow.getAmount());
                    description.setText(expenseRow.getDescription());
                    datepick.setText(expenseRow.getDate());

                    dialognew.show();

                    final Dialog editDialog = dialognew;

                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.setContentView(R.layout.expense_edit_layout);

                            sNo = editDialog.findViewById(R.id.sn);
                            sNo.setText(expenseRow.getNo());
                            pName = editDialog.findViewById(R.id.product_name);
                            pName.setText(expenseRow.getExpense_name());
                            amt = editDialog.findViewById(R.id.amount);
                            amt.setText(expenseRow.getAmount());
                            description0 = editDialog.findViewById(R.id.description0);
                            description0.setText(expenseRow.getDescription());
                            dateV = editDialog.findViewById(R.id.date);
                            dateV.setText(expenseRow.getDate());
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
        return 45;
    }
}

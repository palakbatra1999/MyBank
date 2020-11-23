package com.example.mybank.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.Models.Transaction;
import com.example.mybank.R;

import java.util.ArrayList;

public class TransactionAdapter extends  RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
private  static  final String TAG="";
private ArrayList<Transaction> transactions=new ArrayList<>();

    public TransactionAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(transactions.get(position).getDate());
        holder.description.setText(transactions.get(position).getDescription());
        holder.sender.setText(transactions.get(position).getRecipient());
      holder.transaction_id.setText("transaction_id = "+String.valueOf(transactions.get(position).get_id()));
      double amoun=transactions.get(position).getAmount();
      if(amoun>0)
      {
          holder.transaction_money.setText("+"+amoun);
          holder.transaction_money.setTextColor(Color.GREEN);
      }
      else
      {
          holder.transaction_money.setText(String.valueOf(amoun));
          holder.transaction_money.setTextColor(Color.RED);
      }


    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView transaction_money,description,date,sender,transaction_id;
        private CardView parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            parent=(CardView) itemView.findViewById(R.id.parent);
            transaction_money=(TextView) itemView.findViewById(R.id.transaction_money);
            description=(TextView)itemView.findViewById(R.id.description);
            date=(TextView)itemView.findViewById(R.id.date);
            sender=(TextView)itemView.findViewById(R.id.sender);
            transaction_id=(TextView) itemView.findViewById(R.id.transaction_id);



        }
    }
}

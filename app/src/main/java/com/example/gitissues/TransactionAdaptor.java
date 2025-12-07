package com.example.gitissues;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdaptor extends RecyclerView.Adapter<TransactionAdaptor.TransactionViewHolder> {

    private final Context context;
    private final List<Transaction> transactions;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public TransactionAdaptor(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        holder.tvTitle.setText(t.getTitle());
        holder.tvDate.setText(t.getDate());

        double amt = t.getAmount();
        String text = (amt >= 0 ? "+" : "-") + currencyFormat.format(Math.abs(amt));
        holder.tvAmount.setText(text);

        // Green for positive, red for negative
        if (amt >= 0) {
            holder.tvAmount.setTextColor(Color.parseColor("#1DB954")); // green
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#E53935")); // red
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvAmount;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTransactionTitle);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
}

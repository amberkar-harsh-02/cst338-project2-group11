package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import models.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {
    private List<Transaction> data;

    public TransactionAdapter(List<Transaction> data) {
        this.data = data;
    }

    // Helper to update data and refresh the list
    public void setData(List<Transaction> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvType, tvAmount, tvDate;
        VH(View v) { super(v);
            tvType = v.findViewById(R.id.tvType);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvDate = v.findViewById(R.id.tvDate);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_transaction, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Transaction t = data.get(i);

        h.tvType.setText(t.type);

        // Format the Double amount to Currency String
        h.tvAmount.setText(String.format(Locale.US, "$%.2f", t.amount));

        // Format the Long timestamp to Date String
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        h.tvDate.setText(sdf.format(new Date(t.timestamp)));
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        return data.size();
    }
}
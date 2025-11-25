package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import models.SavingsGoal;
import models.Transaction;

import java.util.List;
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {
    private final List<Transaction> data;
    public TransactionAdapter(List<Transaction> data) { this.data = data; }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvType, tvAmount, tvDate;
        VH(View v) { super(v);
            tvType = v.findViewById(R.id.tvType);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvDate = v.findViewById(R.id.tvDate);
        }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_transaction, p, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH h, int i) {
        Transaction t = data.get(i);
        h.tvType.setText(t.type);
        h.tvAmount.setText(t.amount);
        h.tvDate.setText(t.date);
    }
    @Override public int getItemCount() { return data.size(); }
}
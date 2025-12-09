package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import androidx.recyclerview.widget.RecyclerView;
import models.SavingsGoal;

import com.example.gitissues.R;
import com.example.gitissues.views.SemiCircleProgressView;

import java.util.List;
import java.util.Locale;

public class SavingsGoalAdapter extends RecyclerView.Adapter<SavingsGoalAdapter.VH> {

    private List<SavingsGoal> data;
    private final OnGoalClickListener listener;

    // Interface to handle clicks
    public interface OnGoalClickListener {
        void onGoalClick(SavingsGoal goal);
    }

    // Constructor now takes the listener
    public SavingsGoalAdapter(List<SavingsGoal> data, OnGoalClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setData(List<SavingsGoal> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        SemiCircleProgressView progressView;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGoalName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            progressView = itemView.findViewById(R.id.progress);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        SavingsGoal g = data.get(position);

        holder.tvName.setText(g.name);
        String progressText = String.format(Locale.US, "$%.0f / $%.0f", g.currentAmount, g.targetAmount);
        holder.tvAmount.setText(progressText);
        holder.progressView.setProgress(g.getProgressPercent());

        // Handle Click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGoalClick(g);
        });
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        return data.size();
    }
}
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

import java.util.List;
import java.util.Locale;

public class SavingsGoalAdapter extends RecyclerView.Adapter<SavingsGoalAdapter.VH> {

    private List<SavingsGoal> data;

    public SavingsGoalAdapter(List<SavingsGoal> data) {
        this.data = data;
    }

    public void setData(List<SavingsGoal> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        ProgressBar progress;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGoalName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            progress = itemView.findViewById(R.id.progress);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        SavingsGoal g = data.get(position);

        holder.tvName.setText(g.name);

        // Format: "$250.00 / $1000.00"
        String progressText = String.format(Locale.US, "$%.0f / $%.0f", g.currentAmount, g.targetAmount);
        holder.tvAmount.setText(progressText);

        // Use the model's helper to get integer percentage
        holder.progress.setProgress(g.getProgressPercent());
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        return data.size();
    }
}
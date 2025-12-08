package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {

    private List<User> users;
    private final OnUserDeleteListener deleteListener;

    // Interface to handle delete clicks
    public interface OnUserDeleteListener {
        void onDelete(User user);
    }

    public UserAdapter(List<User> users, OnUserDeleteListener listener) {
        this.users = users;
        this.deleteListener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;
        ImageButton btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUsername);
            tvInfo = itemView.findViewById(R.id.tvUserInfo);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        User user = users.get(position);

        holder.tvName.setText(user.username);
        String role = user.isAdmin ? "ADMIN" : "User";
        holder.tvInfo.setText("ID: " + user.userId + " • " + role);

        // Handle Delete Button Click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (users == null) return 0;
        return users.size();
    }
}
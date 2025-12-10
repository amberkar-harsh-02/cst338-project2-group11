package adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public static class ChatMessage {
        public String role; // "user" or "model"
        public String text;

        public ChatMessage(String role, String text) {
            this.role = role;
            this.text = text;
        }
    }

    private final List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(String role, String text) {
        messages.add(new ChatMessage(role, text));
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.bind(msg);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvSender;
        LinearLayout container;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvMessageContent);
            tvSender = itemView.findViewById(R.id.tvMessageSender);
            // We need the parent layout to change gravity (alignment)
            container = (LinearLayout) itemView;
        }

        public void bind(ChatMessage msg) {
            tvContent.setText(msg.text);

            if ("user".equals(msg.role)) {
                // Align Right for User
                container.setGravity(Gravity.END);
                tvContent.setBackgroundColor(0xFFDCF8C6); // Light Green
                tvSender.setText("You");
            } else {
                // Align Left for Monte
                container.setGravity(Gravity.START);
                tvContent.setBackgroundColor(0xFFE0E0E0); // Gray
                tvSender.setText("Monte");
            }
        }
    }
}
package adapters;

import android.graphics.Color;
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
            container = (LinearLayout) itemView;
        }

        public void bind(ChatMessage msg) {
            tvContent.setText(msg.text);

            if ("user".equals(msg.role)) {
                // USER: Align Right, Purple Bubble, White Text
                container.setGravity(Gravity.END);
                tvContent.setBackgroundResource(R.drawable.bg_chat_user);
                tvContent.setTextColor(Color.WHITE);
                tvSender.setVisibility(View.GONE); // Hide "You" label for cleaner look
            } else {
                // MONTE: Align Left, Gray Bubble, Black Text
                container.setGravity(Gravity.START);
                tvContent.setBackgroundResource(R.drawable.bg_chat_monte);
                tvContent.setTextColor(Color.BLACK);
                tvSender.setVisibility(View.VISIBLE);
                tvSender.setText("Monte");
            }
        }
    }
}
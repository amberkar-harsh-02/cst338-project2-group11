package com.example.gitissues;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import adapters.ChatAdapter;
import database.BankingRepository;
import models.Account;
import models.Transaction;

public class ChatActivity extends AppCompatActivity {

    private ChatAdapter adapter;
    private EditText etInput;
    private GenerativeModelFutures model;
    private BankingRepository repository;
    private String financialContext = ""; // Stores the user's balance info

    // --- INTENT FACTORY ---
    public static Intent getIntent(Context context) {
        return new Intent(context, ChatActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1. Setup Gemini Model
        // REPLACE WITH YOUR ACTUAL API KEY
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash","AIzaSyC0eYQH0JZyo8ixg4IZQuIgzHSm1N3w2KE");
        model = GenerativeModelFutures.from(gm);

        repository = new BankingRepository(this);

        // 2. Setup UI
        RecyclerView rv = findViewById(R.id.rvChatHistory);
        etInput = findViewById(R.id.etChatInput);
        ImageButton btnSend = findViewById(R.id.btnSendMessage);
        View btnBack = findViewById(R.id.btnBackChat);

        adapter = new ChatAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // 3. Load Financial Context (Background)
        loadUserContext();

        // 4. Listeners
        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserContext() {
        int userId = Session.userId(this);
        String username = Session.username(this);

        new Thread(() -> {
            List<Account> accounts = repository.getAccounts(userId);
            StringBuilder sb = new StringBuilder();
            sb.append("You are Monte, a helpful bank assistant for ").append(username).append(".\n");
            sb.append("Here is the user's current financial status:\n");

            if (accounts != null) {
                for (Account acc : accounts) {
                    sb.append("- ").append(acc.accountType).append(" Account: $").append(acc.balance).append("\n");

                    // Add last 3 transactions per account for context
                    List<Transaction> trans = repository.getTransactions(acc.accountId);
                    if (trans != null && !trans.isEmpty()) {
                        sb.append("  Recent activity: ");
                        for (int i = 0; i < Math.min(3, trans.size()); i++) {
                            Transaction t = trans.get(i);
                            sb.append(t.type).append(" ($").append(t.amount).append("), ");
                        }
                        sb.append("\n");
                    }
                }
            }
            sb.append("Answer questions briefly based on this data. If asked about new products, suggest our 'High Yield Savings'.");

            financialContext = sb.toString();

            // Optional: Monte greeting
            runOnUiThread(() -> adapter.addMessage("model", "Hello " + username + "! I have reviewed your accounts. How can I help you today?"));
        }).start();
    }

    private void sendMessage() {
        String query = etInput.getText().toString().trim();
        if (query.isEmpty()) return;

        // Add user message to UI
        adapter.addMessage("user", query);
        etInput.setText("");

        // Combine Context + Query for the AI
        String fullPrompt = financialContext + "\n\nUser Question: " + query;

        // Send to Gemini
        Content content = new Content.Builder().addText(fullPrompt).build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                runOnUiThread(() -> {
                    adapter.addMessage("model", resultText);
                    // Scroll to bottom
                    findViewById(R.id.rvChatHistory).scrollTo(0, 0);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Monte is sleeping...", Toast.LENGTH_SHORT).show();
                });
            }
        }, executor);
    }
}
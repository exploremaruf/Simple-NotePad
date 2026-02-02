package com.penguinlabs.simplenotepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // Variable gulo declare kora
    private EditText noteEditText;
    private FloatingActionButton saveButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Edge-to-edge padding fix
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. UI elements find kora
        noteEditText = findViewById(R.id.noteEditText);
        saveButton = findViewById(R.id.saveButton);

        // 2. SharedPreferences setup (Data save rakhar jonno)
        sharedPreferences = getSharedPreferences("NoteData", Context.MODE_PRIVATE);

        // 3. Purono note thakle seta load kora
        String savedNote = sharedPreferences.getString("note_key", "");
        noteEditText.setText(savedNote);

        // 4. Save Button click korle ki hobe
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// ছোট একটি ক্লিক অ্যানিমেশন
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                    // তোমার আগের সেভ করার লজিক
                    String noteText = noteEditText.getText().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("note_key", noteText);
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                }).start();
            }
        });

        noteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // User likhlei save hobe
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("note_key", s.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        // ১. নতুন ভিউগুলো খুঁজে বের করা
        TextView statusText = findViewById(R.id.statusText);
        ImageButton clearButton = findViewById(R.id.clearButton);

// ২. Clear বাটন ক্লিক লজিক
        clearButton.setOnClickListener(v -> {
            noteEditText.setText("");
            Toast.makeText(this, "Cleared!", Toast.LENGTH_SHORT).show();
        });

// ৩. TextWatcher এর ভেতর স্ট্যাটাস পরিবর্তন
        noteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                statusText.setText("Saving...");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("note_key", s.toString());
                editor.apply();

                // ১ সেকেন্ড পর 'Saved' দেখাবে
                statusText.postDelayed(() -> statusText.setText("Saved"), 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
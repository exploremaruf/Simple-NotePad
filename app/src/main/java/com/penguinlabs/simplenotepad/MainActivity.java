package com.penguinlabs.simplenotepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private EditText noteEditText;

    private FloatingActionButton saveButton;

    private SharedPreferences sharedPreferences;

    private TextView wordCountText;

    private TextView statusText;

    private ImageButton clearButton;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private Runnable statusRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noteEditText = findViewById(R.id.noteEditText);
        saveButton = findViewById(R.id.saveButton);
        wordCountText = findViewById(R.id.wordCountText);
        statusText = findViewById(R.id.statusText);
        clearButton = findViewById(R.id.clearButton);

        sharedPreferences = getSharedPreferences("NoteData", Context.MODE_PRIVATE);

        String savedNote = sharedPreferences.getString("note_key", "");
        noteEditText.setText(savedNote);
        updateWordCount(savedNote);

        clearButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("মুছে ফেলুন")
                    .setMessage("আপনি কি সব লেখা মুছে ফেলতে চান?")
                    .setPositiveButton("হ্যাঁ", (dialog, which) -> {
                        noteEditText.setText("");
                        saveToStorage("");
                        Toast.makeText(this, "সব মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("না", null)
                    .show();
        });

        ImageButton shareButton = findViewById(R.id.shareButton);

        shareButton.setOnClickListener(v -> {
            String noteText = noteEditText.getText().toString().trim();

            if (!noteText.isEmpty()) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, noteText);

                startActivity(Intent.createChooser(shareIntent, "নোটটি শেয়ার করুন"));

                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            } else {

                Toast.makeText(this, "শেয়ার করার জন্য কিছু লিখুন!", Toast.LENGTH_SHORT).show();
            }
        });

        noteEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String currentText = s.toString();
                statusText.setText("Saving...");

                saveToStorage(currentText);

                updateWordCount(currentText);

                handler.removeCallbacks(statusRunnable);
                statusRunnable = () -> statusText.setText("Saved");
                handler.postDelayed(statusRunnable, 1500);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveButton.setOnClickListener(v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                saveToStorage(noteEditText.getText().toString());
                Toast.makeText(MainActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            }).start();
        });
    }

    private void saveToStorage(String text) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("note_key", text);
        editor.apply();
    }

    private void updateWordCount(String text) {

        String trimmedText = text.trim();
        int words = trimmedText.isEmpty() ? 0 : trimmedText.split("\\s+").length;
        int chars = text.length();
        wordCountText.setText("Words: " + words + " | Chars: " + chars);
    }

}
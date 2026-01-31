package com.penguinlabs.simplenotepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Variable gulo declare kora
    private EditText noteEditText;
    private Button saveButton;
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
                String noteText = noteEditText.getText().toString();

                // Note-ta save kora
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("note_key", noteText);
                editor.apply();

                // User-ke message dewa
                Toast.makeText(MainActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
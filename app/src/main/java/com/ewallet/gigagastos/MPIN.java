package com.ewallet.gigagastos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MPIN extends AppCompatActivity {

    private EditText etMpin, etConfirm;
    private Button btnNext;

    private boolean isRemovingTextWatcher = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpin);

        // Assuming 'iv_back' is the ID of your ImageView representing the back button
        ImageView backButton = findViewById(R.id.iv_menu);
        backButton.setOnClickListener(view -> {
            // Navigate back to the Confirmation activity
            finish();
        });

        // Retrieve values passed from Confirmation activity
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phone_number");
        String firstName = intent.getStringExtra("first_name");
        String lastName = intent.getStringExtra("last_name");
        String birthday = intent.getStringExtra("birthday");
        String address = intent.getStringExtra("address");
        String email = intent.getStringExtra("email");

        etMpin = findViewById(R.id.et_mpin);
        etConfirm = findViewById(R.id.et_confirm);
        btnNext = findViewById(R.id.btn_next);

        // Add TextChangedListener to etMpin and etConfirm
        etMpin.addTextChangedListener(textWatcher);
        etConfirm.addTextChangedListener(textWatcher);

        // Set onClickListener for btnNext
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if etMpin and etConfirm match
                if (etMpin.getText().toString().equals(etConfirm.getText().toString())) {
                    // MPIN matches, proceed to store data in the database
                    DBHelper dbHelper = new DBHelper(MPIN.this);
                    boolean isInserted = dbHelper.insertContact(phoneNumber, firstName, lastName, birthday, address, email, etMpin.getText().toString());
                    if (isInserted) {
                        // Data inserted successfully
                        Toast.makeText(MPIN.this, "Successfully saved data to database", Toast.LENGTH_SHORT).show();
                        // Start MainActivity
                        // Start MainActivity
                        Intent intent = new Intent(MPIN.this, Login.class);
                        // Pass phone number as an extra to the Login activity
                        intent.putExtra("phone_number", phoneNumber);
                        startActivity(intent);

                    } else {
                        // Failed to insert data into the database
                        Toast.makeText(MPIN.this, "Failed to save data to database", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Not matching, show a toast message
                    Toast.makeText(MPIN.this, "MPIN does not match. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            // Check if all fields are filled
            boolean allFieldsFilled = etMpin.getText().length() >= 4 &&
                    etConfirm.getText().length() >= 4;

            // Enable/disable Next button and change styling based on field completion
            btnNext.setEnabled(allFieldsFilled);
            if (allFieldsFilled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btnNext.setBackgroundTintList(ContextCompat.getColorStateList(MPIN.this, R.color.ic_launcher_background));
                }
                btnNext.setTextColor(ContextCompat.getColor(MPIN.this, android.R.color.white));
            } else {
                // Reset button styling if not all fields are filled
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btnNext.setBackgroundTintList(ContextCompat.getColorStateList(MPIN.this, R.color.disabled_button));
                }
                btnNext.setTextColor(ContextCompat.getColor(MPIN.this, android.R.color.black));
            }

            // If the text length is greater than 4, truncate it to 4 characters
            if (s.length() > 4 && !isRemovingTextWatcher) {
                isRemovingTextWatcher = true; // Prevent recursive call
                s.delete(4, s.length());
                isRemovingTextWatcher = false;
            }
        }
    };
}

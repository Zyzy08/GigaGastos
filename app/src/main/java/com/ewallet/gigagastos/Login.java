package com.ewallet.gigagastos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    private ImageView iv_change;
    private TextView tv_number; // Declare TextView for displaying phone number
    private StringBuilder pin = new StringBuilder();
    private RadioButton rb_pin1, rb_pin2, rb_pin3, rb_pin4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        iv_change = findViewById(R.id.iv_change);
        tv_number = findViewById(R.id.tv_number); // Initialize TextView for phone number

        // Initialize RadioButtons
        rb_pin1 = findViewById(R.id.rb_pin1);
        rb_pin2 = findViewById(R.id.rb_pin2);
        rb_pin3 = findViewById(R.id.rb_pin3);
        rb_pin4 = findViewById(R.id.rb_pin4);

        // Set click listener for the ImageView
        iv_change.setOnClickListener(v -> {
            // Create an intent to start the MainActivity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        // Retrieve the phone number from MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            String phoneNumber = intent.getStringExtra("phone_number");
            // Display the phone number in the TextView
            tv_number.setText(phoneNumber);
        }

        // Initialize buttons
        Button btn_0 = findViewById(R.id.btn_0);
        Button btn_1 = findViewById(R.id.btn_1);
        Button btn_2 = findViewById(R.id.btn_2);
        Button btn_3 = findViewById(R.id.btn_3);
        Button btn_4 = findViewById(R.id.btn_4);
        Button btn_5 = findViewById(R.id.btn_5);
        Button btn_6 = findViewById(R.id.btn_6);
        Button btn_7 = findViewById(R.id.btn_7);
        Button btn_8 = findViewById(R.id.btn_8);
        Button btn_9 = findViewById(R.id.btn_9);
        ImageView iv_backspace = findViewById(R.id.iv_backspace); // Initialize the backspace ImageView

        // Set click listeners for buttons
        btn_0.setOnClickListener(v -> appendPin("0"));
        btn_1.setOnClickListener(v -> appendPin("1"));
        btn_2.setOnClickListener(v -> appendPin("2"));
        btn_3.setOnClickListener(v -> appendPin("3"));
        btn_4.setOnClickListener(v -> appendPin("4"));
        btn_5.setOnClickListener(v -> appendPin("5"));
        btn_6.setOnClickListener(v -> appendPin("6"));
        btn_7.setOnClickListener(v -> appendPin("7"));
        btn_8.setOnClickListener(v -> appendPin("8"));
        btn_9.setOnClickListener(v -> appendPin("9"));

        // Set click listener for the backspace ImageView
        iv_backspace.setOnClickListener(v -> removeLastDigit());
    }

    // Method to remove the last digit from the PIN
    private void removeLastDigit() {
        if (pin.length() > 0) {
            pin.deleteCharAt(pin.length() - 1);
            updateRadioButtonStatus(pin.length());
        }
    }

    // Method to append the clicked button value to the pin and update RadioButton status
    private void appendPin(String value) {
        pin.append(value);
        updateRadioButtonStatus(pin.length());
    }

    // Method to update the status of RadioButtons based on the pin length
    // Method to update the status of RadioButtons based on the pin length
    // Method to update the status of RadioButtons based on the pin length
    private void updateRadioButtonStatus(int pinLength) {
        // Reset all RadioButtons
        rb_pin1.setChecked(false);
        rb_pin2.setChecked(false);
        rb_pin3.setChecked(false);
        rb_pin4.setChecked(false);

        // Set the status of RadioButtons based on the pin length
        if (pinLength >= 1) {
            rb_pin1.setChecked(true);
        }
        if (pinLength >= 2) {
            rb_pin2.setChecked(true);
        }
        if (pinLength >= 3) {
            rb_pin3.setChecked(true);
        }
        if (pinLength == 4) {
            rb_pin4.setChecked(true);
            // Check the MPIN when the PIN is complete (4 digits)
            checkMPIN(pin.toString());
        }
    }

    // Method to check the MPIN in the database
    private void checkMPIN(String pin) {
        DBHelper dbHelper = new DBHelper(this);
        // Retrieve the phone number from the TextView
        String phoneNumber = tv_number.getText().toString();
        if (dbHelper.phoneNumberExists(phoneNumber)) {
            // Phone number exists in the database, retrieve the MPIN
            String storedMPIN = dbHelper.getMPIN(phoneNumber);
            if (storedMPIN != null && storedMPIN.equals(pin)) {
                // MPIN matches, proceed to next activity or operation
                Intent intent = new Intent(Login.this, Homepage.class);
                startActivity(intent);
            } else {
                Toast.makeText(Login.this, "Wrong MPIN", Toast.LENGTH_SHORT).show();
                clearPin();
            }
        }
    }

    private void clearPin() {
        pin.setLength(0); // Clear the PIN value
        updateRadioButtonStatus(0); // Reset the radio buttons
    }
}

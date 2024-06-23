package com.ewallet.gigagastos;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText et_number;
    private DBHelper dbHelper;
    private String phoneNumber;

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

        et_number = findViewById(R.id.et_number);
        Button btn_next = findViewById(R.id.btn_next);
        dbHelper = new DBHelper(this);

        // Add a text watcher to format the phone number as it is being typed
        et_number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Add a text watcher to restrict input to 10 digits while maintaining format
        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String formattedNumber = formatPhoneNumber(s.toString());
                if (!s.toString().equals(formattedNumber)) {
                    et_number.removeTextChangedListener(this); // Remove the listener to prevent infinite loop
                    et_number.setText(formattedNumber);
                    et_number.setSelection(formattedNumber.length()); // Set cursor position to the end
                    et_number.addTextChangedListener(this); // Add the listener back
                }
            }
        });

        btn_next.setOnClickListener(v -> {
            phoneNumber = et_number.getText().toString().trim();

            phoneNumber = phoneNumber.replaceAll("\\s", "");

            // Check if the phone number contains exactly 10 digits
            if (phoneNumber.length() != 10) {
                // Prompt the user that the phone number is invalid
                Toast.makeText(MainActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return; // Exit the method early
            }

            phoneNumber = "0" + phoneNumber; // Ensure leading '0'

            // Check if the phone number exists in the database
            boolean phoneNumberExists = dbHelper.phoneNumberExists(phoneNumber);

            if (phoneNumberExists) {
                // Phone number exists, proceed to Login activity
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.putExtra("phone_number", phoneNumber); // Pass phone number as an extra
                startActivity(intent);
            } else {
                // Phone number doesn't exist, proceed to Register activity
                Intent intent = new Intent(MainActivity.this, Register.class);
                intent.putExtra("phone_number", phoneNumber); // Pass phone number as an extra
                startActivity(intent);
            }
        });
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters and add spaces to maintain the format
        String digits = phoneNumber.replaceAll("\\D", "");
        StringBuilder formattedNumber = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            formattedNumber.append(digits.charAt(i));
            if ((i + 1) % 3 == 0 && i < digits.length() - 1) {
                formattedNumber.append(" ");
            }
        }
        return formattedNumber.toString();
    }
}

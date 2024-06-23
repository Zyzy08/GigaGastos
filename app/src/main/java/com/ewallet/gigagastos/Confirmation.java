package com.ewallet.gigagastos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Confirmation extends AppCompatActivity {

    private String phoneNumber, firstName, lastName, birthday, address, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Assuming 'iv_back' is the ID of your ImageView representing the back button
        ImageView backButton = findViewById(R.id.iv_menu);
        backButton.setOnClickListener(view -> {
            // Navigate back to the Register activity
            finish();
        });

        // Retrieve values passed from Register activity
        phoneNumber = getIntent().getStringExtra("phone_number");
        firstName = getIntent().getStringExtra("first_name");
        lastName = getIntent().getStringExtra("last_name");
        birthday = getIntent().getStringExtra("birthday");
        address = getIntent().getStringExtra("address");
        email = getIntent().getStringExtra("email");

        // Set the retrieved values to TextViews in the Confirmation layout
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvBirthday = findViewById(R.id.tv_birthday);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvEmail = findViewById(R.id.tv_email);

        tvName.setText(String.format("%s %s", firstName, lastName));

        // Format the birthday string to "MMM dd, yyyy"
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        try {
            Date parsedDate = inputFormat.parse(birthday);
            String formattedBirthday = outputFormat.format(parsedDate);
            tvBirthday.setText(formattedBirthday);
        } catch (ParseException e) {
            e.printStackTrace();
            tvBirthday.setText(birthday); // Use the original string if parsing fails
        }

        tvAddress.setText(address);
        tvEmail.setText(email);

        // Button for moving to the next activity
        Button nextButton = findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToMPINActivity();
            }
        });
    }

    private void moveToMPINActivity() {
        // Pass data to the MPIN activity
        Intent intent = new Intent(Confirmation.this, MPIN.class);
        intent.putExtra("phone_number", phoneNumber);
        intent.putExtra("first_name", firstName);
        intent.putExtra("last_name", lastName);
        intent.putExtra("birthday", birthday);
        intent.putExtra("address", address);
        intent.putExtra("email", email);
        startActivity(intent);
    }
}

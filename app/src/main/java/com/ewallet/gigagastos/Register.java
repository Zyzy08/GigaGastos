package com.ewallet.gigagastos;

import android.app.DatePickerDialog;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Register extends AppCompatActivity {

    private EditText et_fname, et_lname, et_birthday, et_address, et_mail;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Assign EditText instances
        et_fname = findViewById(R.id.et_mpin);
        et_lname = findViewById(R.id.et_confirm);
        et_birthday = findViewById(R.id.et_birthday);
        et_address = findViewById(R.id.et_address);
        et_mail = findViewById(R.id.et_mail);

        // Button for moving to the next activity
        nextButton = findViewById(R.id.btn_next);
        nextButton.setEnabled(false); // Initially disabled

        // TextWatcher to enable/disable the Next button based on field completion
        et_fname.addTextChangedListener(textWatcher);
        et_lname.addTextChangedListener(textWatcher);
        et_birthday.addTextChangedListener(textWatcher);
        et_address.addTextChangedListener(textWatcher);
        et_mail.addTextChangedListener(textWatcher);

        // Assuming 'iv_calendar' is the ID of your ImageView representing the calendar button
        ImageView calendarButton = findViewById(R.id.iv_calendar);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendarDialog();
            }
        });

        // Assuming 'iv_Back' is the ID of your ImageView representing the back button
        ImageView backButton = findViewById(R.id.iv_menu);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to MainActivity
                finish();
            }
        });

        // Button for moving to the next activity
        Button nextButton = findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToConfirmationActivity();
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Check if all fields are filled
            boolean allFieldsFilled = et_fname.getText().length() > 0 &&
                    et_lname.getText().length() > 0 &&
                    et_birthday.getText().length() > 0 &&
                    et_address.getText().length() > 0 &&
                    et_mail.getText().length() > 0;

            // Enable/disable Next button and change styling based on field completion
            nextButton.setEnabled(allFieldsFilled);
            if (allFieldsFilled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    nextButton.setBackgroundTintList(ContextCompat.getColorStateList(Register.this, R.color.ic_launcher_background));
                }
                nextButton.setTextColor(ContextCompat.getColor(Register.this, android.R.color.white));
            } else {
                // Reset button styling if not all fields are filled
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    nextButton.setBackgroundTintList(ContextCompat.getColorStateList(Register.this, R.color.disabled_button));
                }
                nextButton.setTextColor(ContextCompat.getColor(Register.this, android.R.color.black));
            }
        }
    };

    private void moveToConfirmationActivity() {
        // Retrieve values from EditText fields
        String firstName = et_fname.getText().toString();
        String lastName = et_lname.getText().toString();
        String birthday = et_birthday.getText().toString();
        String address = et_address.getText().toString();
        String email = et_mail.getText().toString();

        // Check if any field is empty
        if (firstName.isEmpty()) {
            et_fname.setError("First name is required");
            et_fname.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            et_lname.setError("Last name is required");
            et_lname.requestFocus();
            return;
        }

        if (birthday.isEmpty()) {
            et_birthday.setError("Birthday is required");
            et_birthday.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            et_address.setError("Address is required");
            et_address.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            et_mail.setError("Email is required");
            et_mail.requestFocus();
            return;
        }

        // Check if email is valid
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_mail.setError("Enter a valid email address");
            et_mail.requestFocus();
            return;
        }

        // Check if the birthday is valid
        if (!isDateValid(birthday)) {
            et_birthday.setError("Enter a valid birthday (MM/dd/yyyy)");
            et_birthday.requestFocus();
            return;
        }

        // Calculate age based on birthday
        int age = calculateAge(birthday);

        // Check if age is less than 18
        if (age < 18) {
            Toast.makeText(this, "You must be at least 18 years old to register", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the email address is already used by another user
        // Create an instance of DBHelper
        DBHelper dbHelper = new DBHelper(this);

        // Check if the email already exists
        if (dbHelper.emailExists(email)) {
            // Email already exists, show a toast message
            Toast.makeText(this, "Email is already used by another user.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming phoneNumber is the phone number provided from MainActivity
        String phoneNumber = getIntent().getStringExtra("phone_number");

        // Proceed to Confirmation activity and pass the phone number
        Intent intent = new Intent(Register.this, Confirmation.class);
        intent.putExtra("phone_number", phoneNumber);
        intent.putExtra("first_name", firstName);
        intent.putExtra("last_name", lastName);
        intent.putExtra("birthday", birthday);
        intent.putExtra("address", address);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    // Method to calculate age based on birthday
    private int calculateAge(String birthday) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Calendar dob = Calendar.getInstance();
        try {
            dob.setTime(dateFormat.parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 if parsing fails
        }

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--; // Reduce age by 1 if birthday hasn't occurred yet this year
        }
        return age;
    }

    // Method to check if a date string is valid
    private boolean isDateValid(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void showCalendarDialog() {
        // Get current date to set as default in the DatePickerDialog
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog and set listener to handle date selection
        DatePickerDialog datePickerDialog = new DatePickerDialog(Register.this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Handle date selection here
                    // Adjust month and day to have leading zeros if necessary
                    String formattedMonth = String.format(Locale.getDefault(), "%02d", selectedMonth + 1);
                    String formattedDayOfMonth = String.format(Locale.getDefault(), "%02d", selectedDayOfMonth);
                    String selectedDate = formattedMonth + "/" + formattedDayOfMonth + "/" + selectedYear;
                    // Set the selected date to the EditText
                    et_birthday.setText(selectedDate);
                }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}

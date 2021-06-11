package com.tactical.sanidigsmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import customfonts.EditText__SF_Pro_Display_Light;
import customfonts.MyTextView_Roboto_Regular;
import customfonts.MyTextView_SF_Pro_Display_Medium;

public class Sign_Up extends AppCompatActivity implements View.OnClickListener {

    //the textviews
    MyTextView_SF_Pro_Display_Medium alreadyuserLogin;

    //edittext fields
    EditText__SF_Pro_Display_Light fullnamefield, phonenumberfield, emailfield, passwordfield;
    String wholename, phonenumber, emailaddress, passwordtext, allin, allin2;

    //button
    MyTextView_Roboto_Regular signupbutton;

    //flags for fields before submit
    boolean isNameValid, isEmailValid, isPhoneValid, isPasswordValid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);

        //check if user is already registered
        SharedPreferences sharedprefs = this.getSharedPreferences("sanidigspref",0);
        allin2 = sharedprefs.getString("installed","");
        Log.d("signup", "oncreatre");
        Log.d("installed? :", allin2);

        if(allin2.matches("yes")){

            //then go to the next activity
            Intent intented = new Intent(this, SanidigsHome.class);
            startActivity(intented);

            //close activity
            finish();

        }


        alreadyuserLogin = findViewById(R.id.alreadyuserlogin);
        alreadyuserLogin.setOnClickListener(this);

        //fields
        fullnamefield = findViewById(R.id.fullnamefield);
        //listenerfor field
        fullnamefield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // Check for a valid name.
                if (s.toString().isEmpty()) {
                    fullnamefield.setError("Please enter your full name");
                    isNameValid = false;
                } else  {
                    isNameValid = true;
                }

            }
        });

        phonenumberfield = findViewById(R.id.phonenumberfield);
        phonenumberfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // Check for a valid phone number.
                if (s.toString().isEmpty()) {
                    phonenumberfield.setError("Please enter a valid phone number");
                    isPhoneValid = false;
                } else if(s.toString().length() <= 11) {

                    phonenumberfield.setError("Please enter a valid phone number leading with 256");
                    isPhoneValid = false;
                }
                else  {
                    isPhoneValid = true;
                }

            }
        });

        emailfield = findViewById(R.id.emailfield);
        emailfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // Check for a valid email address.
                if (s.toString().isEmpty()) {
                    emailfield.setError("Please your email address");
                    isEmailValid = false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    emailfield.setError("Please enter a valid email address");
                    isEmailValid = false;
                } else  {
                    isEmailValid = true;
                }

            }
        });

        passwordfield = findViewById(R.id.passwordfield);
        passwordfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // Check for a valid password.
                if (s.toString().isEmpty()) {
                    passwordfield.setError("Please enter your password");
                    isPasswordValid = false;
                } else if (s.length() < 6) {
                    passwordfield.setError("Your password must more than 6 characters");
                    isPasswordValid = false;
                } else  {
                    isPasswordValid = true;
                }

            }
        });

        //button
        signupbutton = findViewById(R.id.signupSubmit);
        signupbutton.setOnClickListener(this);









    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.alreadyuserlogin:
                //to login activity
                Intent intent = new Intent(Sign_Up.this,SanidigsHome.class);
                startActivity(intent);
                //Remove activity
                finish();
                break;

            case R.id.signupSubmit:



                if (isNameValid && isEmailValid && isPhoneValid && isPasswordValid) {

                    wholename = fullnamefield.getText().toString();
                    phonenumber = phonenumberfield.getText().toString();
                    emailaddress = emailfield.getText().toString();
                    passwordtext = passwordfield.getText().toString();



                    //now store these values in the shared pref
                    SharedPreferences sharedPreferences = this.getSharedPreferences("sanidigspref",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("fullname",wholename );
                    editor.putString("phonenumber",phonenumber);
                    editor.putString("emailaddress", emailaddress);
                    editor.putString("password", passwordtext);
                    editor.putString("installed","yes");
                    editor.commit();


                    Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();

                    //then go to the next activity
                    Intent intent1 = new Intent(this, SanidigsHome.class);
                    startActivity(intent1);

                    //close activity
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), "Please fill the fields provided!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}

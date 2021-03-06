package com.example.dulanjali.agroworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_Register extends AppCompatActivity {

    // Views
    Button btnLogin;
    TextInputLayout inputUserName,inputFullName,inputEmail,inputPhoneNo,inputPassword;
    MaterialButton btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__register);

        //link views with xml
        btnLogin = findViewById(R.id.nav_login);
        inputUserName = findViewById(R.id.reg_username);
        inputFullName = findViewById(R.id.reg_fullname);
        inputEmail = findViewById(R.id.reg_email);
        inputPhoneNo = findViewById(R.id.reg_phone);
        inputPassword = findViewById(R.id.reg_password);
        btnRegister = findViewById(R.id.reg_button);

        mAuth = FirebaseAuth.getInstance();

        //button click to register user
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validateUserName() | !validateEmail() | !validatePassword() | !validateFullName() | !validatePhone() )
                {
                    return;
                }

               final String name = inputUserName.getEditText().getText().toString();
                String email = inputEmail.getEditText().getText().toString();
                String password = inputPassword.getEditText().getText().toString();
                String fullname = inputFullName.getEditText().getText().toString();
                String phone = inputPhoneNo.getEditText().getText().toString();

                RegisterAccount(name,fullname,email,password,phone);

            }
        });

        //button click to open user login
        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Activity_Register.this,Activity_Login.class));
                    }
                }
        );
    }

    //Register data send to database
    private void RegisterAccount(final String name, final String fullname, final String email, String password, final String phone )
    {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String current_user_id = mAuth.getCurrentUser().getUid();
                            storeUserDefaultReference = FirebaseDatabase.getInstance().getReference().child("users").child(current_user_id);
                            storeUserDefaultReference.child("uid").setValue(current_user_id);
                            storeUserDefaultReference.child("email").setValue(email);
                            storeUserDefaultReference.child("username").setValue(name);
                            storeUserDefaultReference.child("user_fullname").setValue(fullname);
                            storeUserDefaultReference.child("user_phone").setValue(phone)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Intent regIntent = new Intent(Activity_Register.this,Activity_Login.class);
                                                regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(regIntent);
                                                Toast.makeText(Activity_Register.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                        }
                                    });


                        }

                    }
                });

    }

    ///validations with regular expressions
    private Boolean validateUserName()
    {
        String val = inputUserName.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if(val.isEmpty())
        {
            inputUserName.setError("Field cannot be empty");
            return false;
        }
        else if(val.length()>=15)
        {
            inputUserName.setError("Username is too long");
            return false;
        }
        else if(!val.matches(noWhiteSpace))
        {
            inputUserName.setError("White Spaces are not allowed");
            return false;
        }
        else
        {
            inputUserName.setError(null);
            inputUserName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateFullName()
    {
        String val = inputFullName.getEditText().getText().toString();

        if(val.isEmpty())
        {
            inputFullName.setError("Field cannot be empty");
            return false;
        }
        else if(val.length()>=50)
        {
            inputFullName.setError("Username is too long");
            return false;
        }
        else
        {
            inputFullName.setError(null);
            inputFullName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhone()
    {
        String val = inputPhoneNo.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if(val.isEmpty())
        {
            inputPhoneNo.setError("Field cannot be empty");
            return false;
        }
        else if(val.length()> 10| val.length()<10)
        {
            inputPhoneNo.setError("Invalid Phone");
            return false;
        }
        else if(!val.matches(noWhiteSpace))
        {
            inputPhoneNo.setError("White Spaces are not allowed");
            return false;
        }
        else
        {
            inputPhoneNo.setError(null);
            inputPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail()
    {
        String val = inputEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(val.isEmpty())
        {
            inputEmail.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(emailPattern))
        {
            inputEmail.setError("Invalid Email");
            return false;
        }
        else
        {
            inputEmail.setError(null);
            inputEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword()
    {
        String val = inputPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$)" +
                ".{4,}"+
                "$";


        if(val.isEmpty())
        {
            inputPassword.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(passwordVal))
        {
            inputPassword.setError("Password is too weak");
            return false;
        }
        else
        {
            inputPassword.setError(null);
            inputPassword.setErrorEnabled(false);
            return true;
        }
    }


}

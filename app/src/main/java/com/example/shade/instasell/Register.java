package com.example.shade.instasell;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Register extends AppCompatActivity {

    @BindView(R.id.nameRegister)
    EditText nameField;
    @BindView(R.id.emailRegister)
    EditText emailField;
    @BindView(R.id.passwordRegister)
    EditText passwordField;
    @BindView(R.id.registerButtonRegister)
    Button registerButton;
    @BindView(R.id.progressbarRegister)
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(!nameField.getText().toString().equals("")
                        &&  !emailField.getText().toString().equals("")
                        &&  !passwordField.getText().toString().equals(""))
                {
                    mAuth.createUserWithEmailAndPassword(emailField.getText().toString(),passwordField.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        makeToast("Success Added");
                                        addUser(nameField.getText().toString());
                                        sendEmailVerf();
                                        startActivity(new Intent(getBaseContext(),Login.class));

                                    }
                                    else
                                    {
                                        makeToast("Failed");
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
                else
                {
                        makeToast("Please fill all empty Fields");
                }
            }
        });


    }


    public void addUser(String uName)
    {
        User user = new User(uName,mAuth.getCurrentUser().getEmail(),mAuth.getCurrentUser().getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("Users").child(user.getId()).setValue(user);

        Log.d("Done","user is done");

    }

    public void sendEmailVerf()
    {
        if(mAuth.getCurrentUser()!=null)
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        Log.d("sendVer","done");
                    else
                        Log.d("sendVer","Error");

                }
            });

            }


    public void makeToast(String yourtoast)
    {
        Toast.makeText(this,yourtoast,Toast.LENGTH_SHORT).show();
    }


}

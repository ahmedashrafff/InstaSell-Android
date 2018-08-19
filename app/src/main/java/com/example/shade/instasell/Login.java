package com.example.shade.instasell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity {

    @BindView(R.id.loginButtonLogin)
    Button loginButton;
    @BindView(R.id.emailLogin)
    EditText emailField;
    @BindView(R.id.passwordLogin)
    EditText passwordField;
    @BindView(R.id.progressBarLogin)
    ProgressBar progressBar;
    @BindView(R.id.registerLogin)
    TextView register;
    @BindView(R.id.overlayLogin)
    ImageView overlay;

    private FirebaseAuth.AuthStateListener mAuthlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        setupFirebaseListener();
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!emailField.getText().toString().equals("") &&
                        !passwordField.getText().toString().equals(""))
                {
                        showProgress(true);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailField.getText().toString(),
                            passwordField.getText().toString()).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        showProgress(false);
                                        if( FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                                        {
                                            Log.d("loged","yes");
                                            makeToast("Success In");

                                        }
                                        else
                                        {
                                            FirebaseAuth.getInstance().signOut();
                                            makeToast("Failed Please Verfiy");
                                        }

                                    }
                                    else
                                    {
                                        showProgress(false);
                                        makeToast("Failed");

                                    }


                                }

                            });
                }
                else
                {
                    makeToast("Please fill the empty Fields");
                }

            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),Register.class));
            }
        });





    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthlistener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthlistener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthlistener);
        }
    }


    public void makeToast(String yourtoast)
    {
        Toast.makeText(this,yourtoast,Toast.LENGTH_SHORT).show();
    }


    public void setupFirebaseListener()
    {
        mAuthlistener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    if(firebaseAuth.getCurrentUser().isEmailVerified())
                    {
                        showProgress(true);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable()
                        {


                            @Override
                            public void run() {
                                Log.d("UserLogin","there is user Logged");
                                finish();
                                startActivity(new Intent(getBaseContext() ,Home.class));
                            }

                        }, 500);

                    }


                }
            }
        };




    }

    public void showProgress(boolean state)
    {
        if(state==true)
        {
            overlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        else if(state==false)
        {
            overlay.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }


}

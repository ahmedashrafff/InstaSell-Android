package com.example.shade.instasell;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class Settings extends AppCompatActivity implements SelectPhotoDialog.OnPhotoSelectedListener{

    @BindView(R.id.toolbarSettings)
    Toolbar toolbar;
    @BindView(R.id.usernameSettings)
    TextView usernameField;
    @BindView(R.id.profilepictureSettings)
    ImageView profilepicture;
    Uri imageURI;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usernameField.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString());
        Log.d("WhyWHy",FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        profilepicture.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());



    }

    @Override
    protected void onStart() {
        super.onStart();
        profilepicture.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());

    }

    @OnClick(R.id.profilepictureButtonSettings)
    public  void updatePP()
    {
        SelectPhotoDialog selectphotoDialog=new SelectPhotoDialog();
        selectphotoDialog.show(getSupportFragmentManager(),"selectPhoto");

        if(imageURI!=null)
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(imageURI)
                    .build();


            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getBaseContext(),"PP Updated",Toast.LENGTH_SHORT).show();
                            profilepicture.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_SHORT).show();

                            Log.d("WhyWhy",e.getMessage());
                        }
                    });

        }


    }

    @OnClick(R.id.editNameSettings)
    public void editName()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Edit User Name");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.editnamedialog, null);
        alert.setView(dialogView);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText newName = (EditText) dialogView.findViewById(R.id.newusernameEditDialog);
                String newUsername=newName.getText().toString();

                if(newUsername!=null)
                {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newUsername)
                            .build();

                    FirebaseAuth.getInstance().getCurrentUser()
                            .updateProfile(profileUpdates)
                            .addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    Toast.makeText(getBaseContext(),"Name Updated",Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_SHORT).show();

                        }
                    });


                }


                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

    }

    @OnClick(R.id.editPassSettings)
    public void editPassword()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Edit User Name");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.editpassworddialog, null);
        alert.setView(dialogView);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                EditText newPass= (EditText) dialogView.findViewById(R.id.newpasswordEditPassword);
                EditText newPassConfirm= (EditText) dialogView.findViewById(R.id.newpasswordconfirmEditPassword);

                String newPassword=newPass.getText().toString();
                String newPasswordConfirm=newPassConfirm.getText().toString();

                if(newPassword!=null && newPassConfirm!=null)
                {
                    if(newPassword.equals(newPasswordConfirm))
                    {
                        FirebaseAuth.getInstance().getCurrentUser()
                                .updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getBaseContext(),"Updated",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.d("Failewhy",e.getMessage());
                                        Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }



                dialog.dismiss();

            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                dialog.dismiss();

            }
        });

        alert.show();

    }














    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void getImagePath(Uri imagePath) {
        imageURI=imagePath;

    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {

    }
}

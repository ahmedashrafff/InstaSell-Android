package com.example.shade.instasell;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddPost extends AppCompatActivity implements SelectPhotoDialog.OnPhotoSelectedListener{

    @BindView(R.id.imagePost)
    ImageView imagepost;
    @BindView(R.id.titlePost)
    TextView titlepost;
    @BindView(R.id.pricePost)
    TextView pricepost;
    @BindView(R.id.descriptionPost)
    TextView descriptionpost;
    @BindView(R.id.cityPost)
    TextView citypost;
    @BindView(R.id.mobilenumberPost)
    TextView mobilenumber;
    @BindView(R.id.addressPost)
    TextView addresspost;
    @BindView(R.id.postButtonPost)
    Button postButton;

    LovelyProgressDialog progressDialog;

    Uri imageUri;
    Bitmap imageBitmap;
    Boolean successFlag=true;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

         progressDialog= new LovelyProgressDialog(this);
        progressDialog
                .setIcon(R.drawable.baseline_cloud_upload_white_48dp)
                .setTitle("Loading")
                .setMessage("Ad is being uploaded ...")
                .setTopColorRes(R.color.colorPrimaryDark)
                .setCancelable(true);




        imagepost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                SelectPhotoDialog selectphotoDialog=new SelectPhotoDialog();
                selectphotoDialog.show(getSupportFragmentManager(),"selectPhoto");

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!isEmpty(titlepost.getText().toString()) && !isEmpty(descriptionpost.getText().toString())
                        && !isEmpty(pricepost.getText().toString()))
                {
                    if(imageBitmap!=null)
                    {
                        uploadImage("Bitmap");


                        if(successFlag==false)
                            makeToast("There is an error please try again...");

                    }
                    else if(imageUri!=null)
                    {

                        uploadImage("Uri");

                        if(successFlag==false)
                            makeToast("There is an error please try again...");

                    }
                    else if(imageBitmap==null& imageUri==null)
                    {
                        makeToast("Please insert a photo");

                    }


                }

                else
                {

                    makeToast("Please fill the empty fields");
                }
            }
        });

    }


    @Override
    public void getImagePath(Uri imagePath) {

        imageUri=imagePath;
        imageBitmap=null;

        imagepost.setImageURI(imagePath);


    }

    @Override
    public void getImageBitmap(Bitmap bitmap)
    {
        imageUri=null;
        imageBitmap=bitmap;


        imagepost.setImageBitmap(bitmap);


    }


    public void uploadImage(String typeIs)
    {
        if(typeIs.equals("Bitmap"))
        {
            progressDialog.show();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInBytes = baos.toByteArray();
            String postID= FirebaseDatabase.getInstance().getReference().push().getKey();

            FirebaseStorage.getInstance().getReference()
                    .child("Images")
                    .child(postID)
                    .putBytes(imageInBytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            String downloadUrl=taskSnapshot.getDownloadUrl().toString();
                            uploadPost(downloadUrl);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            successFlag=false;
                            progressDialog.dismiss();

                        }
                    });
        }


        else if (typeIs.equals("Uri"))
        {
            progressDialog.show();
            String postID=FirebaseDatabase.getInstance().getReference().push().getKey();
            FirebaseStorage.getInstance().getReference()
                    .child("Images")
                    .child(postID)
                    .putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            String downloadUrl=taskSnapshot.getDownloadUrl().toString();
                            uploadPost(downloadUrl);
                            progressDialog.dismiss();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            successFlag=false;
                            progressDialog.dismiss();

                        }
                    });

        }
    }

    public void uploadPost(String imdUrl)
    {

        String postID= FirebaseDatabase.getInstance().getReference().push().getKey();
        String userID= FirebaseAuth.getInstance().getUid();
        String userEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Post post = new Post();
        post.setTitle(titlepost.getText().toString().toLowerCase());
        post.setDescription(descriptionpost.getText().toString().toLowerCase());
        post.setPrice(pricepost.getText().toString()+ " LE.");
        post.setCity(citypost.getText().toString().toLowerCase());
        post.setAddress(addresspost.getText().toString().toLowerCase());
        post.setMobileNumber(mobilenumber.getText().toString());
        post.setId(postID);
        post.setUserId(userID);
        post.setUserEmail(userEmail);
        post.setImagedownloadurl(imdUrl);


        FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postID)
                .setValue(post)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        successFlag=false;
                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userID)
                .child("Posts")
                .child(postID)
                .setValue(postID)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        successFlag=false;
                    }
                });

        makeToast("Successfully posted");


    }


    public void makeToast(String yourToast)
    {
        Toast.makeText(this,yourToast,Toast.LENGTH_SHORT).show();}


    private boolean isEmpty(String string){
        return string.equals("");
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




}

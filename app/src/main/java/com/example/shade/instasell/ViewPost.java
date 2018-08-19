package com.example.shade.instasell;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewPost extends AppCompatActivity
{
    @BindView(R.id.titleViewPost)
    TextView title;
    @BindView(R.id.descriptionViewPost)
    TextView description;
    @BindView(R.id.priceViewPost)
    TextView price;
    @BindView(R.id.cityViewPost)
    TextView citypost;
    @BindView(R.id.addressViewPost)
    TextView address;
    @BindView(R.id.toptitleViewPost)
    TextView toptitle;
    @BindView(R.id.smsbuttonViewPost)
    FloatingActionButton sms;
    @BindView(R.id.phonebuttonViewPost)
    FloatingActionButton phone;
    @BindView(R.id.emailbuttonViewPost)
    FloatingActionButton email;
    @BindView(R.id.watchlistButtonViewPost)
    FloatingActionButton watchlist;
    @BindView(R.id.imageViewPost)
    ImageView adImage;
    @BindView(R.id.toolbarViewPost)
    android.support.v7.widget.Toolbar toolbar;

    boolean inDB;
    Post post;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpost);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            post=new Post();
            post.setTitle(bundle.getString("title").toString());
            post.setImagedownloadurl(bundle.getString("image").toString());
            post.setDescription(bundle.getString("description").toString());
            post.setPrice(bundle.getString("price").toString());
            post.setCity(bundle.getString("city").toString());
            post.setAddress(bundle.getString("address").toString());
            post.setUserEmail(bundle.getString("email").toString());
            post.setMobileNumber(bundle.getString("mobilenumber").toString());
            post.setId(bundle.getString("postid").toString());


            Glide.with(this)
                    .load(post.getImagedownloadurl())
                    .into(adImage);

            title.setText(post.getTitle());
            description.setText(post.getDescription());
            price.setText(post.getPrice());
            citypost.setText(post.getCity());
            address.setText(post.getAddress());
            toptitle.setText(post.getTitle());

            checkWatchlist();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.smsbuttonViewPost)
    public void gotoSMS()
    {
        YoYo.with(Techniques.Pulse)
                .duration(300)
                .repeat(1)
                .playOn(sms);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("smsto:"+post.getMobileNumber())); // This ensures only SMS apps respond
        intent.putExtra("sms_body", "");
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            confirmationDialog(intent,"SMS","Open SMS App?");
        }
    }

    @OnClick(R.id.emailbuttonViewPost)
    public void gotoEmail()
    {
        YoYo.with(Techniques.Pulse)
                .duration(300)
                .repeat(1)
                .playOn(email);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+post.getUserEmail())); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        if (intent.resolveActivity(getPackageManager()) != null) {
            confirmationDialog(intent,"Email","Open Email App?");
        }

    }


    @OnClick(R.id.phonebuttonViewPost)
    public void gotoDial()
    {
        YoYo.with(Techniques.Pulse)
                .duration(300)
                .repeat(1)
                .playOn(phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", post.getMobileNumber(), null));
        confirmationDialog(intent,"Dial","Open Dial App?");

    }

    public void confirmationDialog(final Intent intent, String title, String message)
    {
        final LovelyStandardDialog lovelyStandardDialog= new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.HORIZONTAL);
        lovelyStandardDialog
                .setTopColorRes(R.color.colorPrimaryDark)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.baseline_info_white_48dp)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(intent);
                        lovelyStandardDialog.dismiss();

                    }
                })
                .setNegativeButton("No", null)
                .show();
            }


    @OnClick(R.id.watchlistButtonViewPost)
    public void addtoWatchlist()
    {
        if(inDB==false)
        {
            YoYo.with(Techniques.RubberBand)
                    .duration(500)
                    .repeat(1)
                    .playOn(watchlist);
            watchlist.setImageResource(R.drawable.watchlistfill);
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("Watchlist")
                    .child(post.getId())
                    .setValue(post.getId());
            inDB=true;
            Toast.makeText(this,"Added to watchlist",Toast.LENGTH_SHORT).show();
        }
        else if(inDB==true)
        {

            YoYo.with(Techniques.RubberBand)
                    .duration(500)
                    .repeat(1)
                    .playOn(watchlist);
            watchlist.setImageResource(R.drawable.watchlistedge);
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("Watchlist")
                    .child(post.getId())
                    .removeValue();
            inDB=false;
            Toast.makeText(this,"Removed from watchlist",Toast.LENGTH_SHORT).show();

        }


    }


    public  void checkWatchlist()
    {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("Watchlist")
                .child(post.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists())
                        {
                            inDB=false;
                            watchlist.setImageResource(R.drawable.watchlistedge);
                        }
                        else if(dataSnapshot.exists())
                        {
                            inDB=true;
                            watchlist.setImageResource(R.drawable.watchlistfill);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
    }


    @OnClick(R.id.imageViewPost)
    public void imagefullscreen(View view)
    {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.imagefullscreen, null);
        ImageView imagefullscreen=(ImageView) popupView.findViewById(R.id.imageFullscreen);
        Glide.with(this)
                .load(post.getImagedownloadurl())
                .into(imagefullscreen);


        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }


}

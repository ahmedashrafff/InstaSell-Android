package com.example.shade.instasell;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyrantgit.explosionfield.ExplosionField;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.PostViewHolder> {

    Context context;
    List<Post> posts;

    public MyAdsAdapter(Context context, List<Post> posts)
    {
        this.context = context;
        this.posts = posts;


    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myad_viewholder,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position)
    {
        Glide.with(context)
                .load(posts.get(position).getImagedownloadurl())
                .into(holder.postImage);
        holder.postTitle.setText(posts.get(position).getTitle());
        holder.postPrice.setText(posts.get(position).getPrice());
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ViewPost.class);
                intent.putExtra("title",posts.get(position).getTitle());
                intent.putExtra("image",posts.get(position).getImagedownloadurl());
                intent.putExtra("price",posts.get(position).getPrice());
                intent.putExtra("description",posts.get(position).getDescription());
                intent.putExtra("city",posts.get(position).getCity());
                intent.putExtra("address",posts.get(position).getAddress());
                intent.putExtra("mobilenumber",posts.get(position).getMobileNumber());
                intent.putExtra("email",posts.get(position).getUserEmail());
                intent.putExtra("postid",posts.get(position).getId());
                context.startActivity(intent);

            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.alertDialogshow(position);



            }
        });





    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.myadviewholderLayout)
        CardView cardView;
        @BindView(R.id.postimageMyAdViewHolder)
        ImageView postImage;
        @BindView(R.id.postpriceMyAdViewHolder)
        TextView postPrice;
        @BindView(R.id.posttitleMyAdViewHolder)
        TextView postTitle;
        @BindView(R.id.constlayoutMyAdViewHolder)
        ConstraintLayout constraintLayout;
        @BindView(R.id.deleteMyAdViewHolder)
        Button deleteButton;
        @BindView(R.id.editMyAdViewHolder)
        Button editButton;

        boolean done=false;


        public PostViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);

        }

        public void alertDialogshow(final int position)
        {
            final LovelyStandardDialog lovelyStandardDialog= new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.HORIZONTAL);
            lovelyStandardDialog
                    .setTopColorRes(R.color.colorPrimaryDark)
                    .setButtonsColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.baseline_info_white_48dp)
                    .setTitle("Delete")
                    .setMessage("Do you want to delete this Ad?")
                    .setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            ExplosionField explosionField = new ExplosionField(context);
                            explosionField.explode(cardView);


                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable()
                            {


                                @Override
                                public void run() {

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Posts")
                                            .child(posts.get(position).getId())
                                            .removeValue();
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("Posts")
                                            .child(posts.get(position).getId())
                                            .removeValue();
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("Watchlist")
                                            .child(posts.get(position).getId())
                                            .removeValue();

                                    ((MyAds)context).finish();
                                    context.startActivity(new Intent(context,MyAds.class));
                                }

                            }, 800);





                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        }
    }

}

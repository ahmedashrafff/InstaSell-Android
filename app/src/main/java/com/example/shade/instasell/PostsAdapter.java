package com.example.shade.instasell;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_SHORT;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    Context context;
    List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts)
    {
        this.context = context;
        this.posts = posts;


    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_viewholder,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, final int position)
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




    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.postimageViewholder)
        ImageView postImage;
        @BindView(R.id.postpriceViewholder)
        TextView postPrice;
        @BindView(R.id.posttitleViewholder)
        TextView postTitle;
        @BindView(R.id.constlayoutViewHolder)
        ConstraintLayout constraintLayout;


        public PostViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);

        }
    }

}

package com.example.shade.instasell;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Watchlist extends AppCompatActivity {

    @BindView(R.id.toolbarWatchlist)
    Toolbar toolbar;
    @BindView(R.id.recyclerWatchlist)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("Watchlist")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        List<String> watchlistIds = new ArrayList<>();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren())
                        {
                            String id=snapshot.getValue(String.class);
                            watchlistIds.add(id);
                        }

                        getWatchlist(watchlistIds);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }


    public void getWatchlist(final List<String> myadsIds) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Post> myWatchlist = new ArrayList<>();
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    Post post = postsnapshot.getValue(Post.class);
                    for (int i = 0; i < myadsIds.size(); i++) {
                        if (myadsIds.get(i).equals(post.getId())) {

                            myWatchlist.add(post);
                        }
                    }
                }



                setRecyclerView(myWatchlist);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }


    public  void setRecyclerView(List<Post> allPosts)
    {
        PostsAdapter postsAdapter= new PostsAdapter(this,allPosts);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(postsAdapter);
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

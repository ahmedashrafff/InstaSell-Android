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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAds extends AppCompatActivity {

    @BindView(R.id.toolbarMyAds)
    Toolbar toolbar;
    @BindView(R.id.recyclerMyAds)
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("Posts").addValueEventListener(new ValueEventListener()
        {
            List<String> myadsIds = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    String id=snapshot.getValue(String.class);
                    myadsIds.add(id);
                }

                getMyads(myadsIds);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void getMyads(final List<String> myadsIds)
    {
        DatabaseReference ref=  FirebaseDatabase.getInstance().getReference();
        ref.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<Post> myAds=new ArrayList<>();
                for(DataSnapshot postsnapshot:dataSnapshot.getChildren())
                {
                    Post post= postsnapshot.getValue(Post.class);
                    Log.d("hhhhhhhhh",post.getTitle());
                    for(int i=0; i<myadsIds.size(); i++)
                    {
                        if(myadsIds.get(i).equals(post.getId()))
                        {
                            Log.d("hhhhhhhhh","found");

                            myAds.add(post);
                        }
                    }
                }


                setRecyclerView(myAds);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }



    public  void setRecyclerView(List<Post> allPosts)
    {
        MyAdsAdapter myAdsAdapter= new MyAdsAdapter(this,allPosts);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(myAdsAdapter);

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

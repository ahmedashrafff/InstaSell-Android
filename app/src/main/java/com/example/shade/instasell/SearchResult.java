package com.example.shade.instasell;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResult extends AppCompatActivity {

    @BindView(R.id.toolbarSearchResult)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.recyclerSearchResult)
    RecyclerView recyclerView;
    @BindView(R.id.noresultSearchResult)
    TextView noResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final String searchItem = getIntent().getStringExtra("searchItem");
        if(searchItem!=null)
        {
            FirebaseDatabase.getInstance().getReference()
                    .child("Posts")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Post post=new Post();
                            List<Post> matchedPosts=new ArrayList<>();
                            for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                            {
                                post=postSnapshot.getValue(Post.class);

                                if(post.getTitle().contains(searchItem))
                                    matchedPosts.add(post);

                            }

                            if(matchedPosts.isEmpty())
                            {
                                recyclerView.setVisibility(View.INVISIBLE);
                                noResult.setVisibility(View.VISIBLE);
                            }
                            else
                                setRecyclerView(matchedPosts);




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



        }

    }


    public  void setRecyclerView(List<Post> allPosts)
    {
        PostsAdapter myAdsAdapter= new PostsAdapter(this,allPosts);
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


package com.example.shade.instasell;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    @BindView(R.id.recyclerHome)
    RecyclerView recyclerView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;


    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final SearchView searchBar=(SearchView) findViewById(R.id.searchHome);

        setupFirebaseListener();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getLatestAds();

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setIconified(false);
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getBaseContext(),SearchResult.class);
                intent.putExtra("searchItem",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.myadsHome)
        {
            startActivity(new Intent(this,MyAds.class));


        }
        else if (id == R.id.watchlistHome)
        {
            startActivity(new Intent(this,Watchlist.class));
        }
        else if (id == R.id.settingsHome)
        {
            startActivity(new Intent(this,Settings.class));
        }
        else if (id == R.id.logoutHome)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,Login.class));
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void setupFirebaseListener()
    {
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    Log.d("UserSearch","User is loggged");
                }
                else
                {
                    Log.d("UserSearch","User is not loggged");
                    startActivity(new Intent(getBaseContext(),Login.class));
                    finish();
                }
            }
        };
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

      /*
        ImageView profilepicture = (ImageView) header.findViewById(R.id.profilepictureHome);
        profilepicture.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());*/

        View header = navigationView.getHeaderView(0);
        TextView username=(TextView) header.findViewById(R.id.usernameHome);
        username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());


    }


    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);

    }



    @OnClick(R.id.addpostHome)
    public void goToAddPost(View view)
    {
        startActivity(new Intent(this,AddPost.class));
    }


    @OnClick(R.id.viewallButtonHome)
    public void viewallAds()
    {
        startActivity(new Intent(this,AllAds.class));
    }


    public void getLatestAds()
    {
        FirebaseDatabase.getInstance().getReference().child("Posts").limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        List<Post> newPosts=new ArrayList<>();
                        for(DataSnapshot postDB :dataSnapshot.getChildren())
                        {   Post post= postDB.getValue(Post.class);
                            newPosts.add(post);
                            Log.d("Nownow",post.getTitle());
                        }

                        Collections.reverse(newPosts);
                        setRecyclerView(newPosts);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }



    public void setRecyclerView(List<Post> posts)
    {
        PostsAdapter postsAdapter=new PostsAdapter(this,posts);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(postsAdapter);

    }

}

package com.adaptivehandyapps.ahathing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.auth.AnonymousAuthActivity;
import com.adaptivehandyapps.ahathing.auth.EmailPasswordActivity;
import com.adaptivehandyapps.ahathing.auth.GoogleSignInActivity;
import com.adaptivehandyapps.ahathing.dal.StoryProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

///////////////////////////////////////////////////////////////////////////
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private boolean mVacating = false;

    StoryProvider mStoryProvider;

    // Firebase auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set content to main
        setContentView(R.layout.activity_main);
        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // setup fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabmap);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Patience, Grasshopper.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // setup drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // setup navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // create play provider
        setPlayProvider(new StoryProvider(this, getPlayProviderCallback()));

        // update the main content with stage
        int contentId = R.layout.content_stage;
        replaceFragment(contentId);

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...TODO?
            }
        };
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
    public StoryProvider getPlayProvider() { return mStoryProvider;}
    public Boolean setPlayProvider(StoryProvider storyProvider) { mStoryProvider = storyProvider; return true;}
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_whoami) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                String email = user.getEmail();
                Uri photoUrl = user.getPhotoUrl();

                // display nam eundefined
                if (name == null) {
                    // try splitting email at @
                    String split[];
                    split = email.split("@");
                    // if @ present
                    if (split.length > 1) {
                        // assign display name from email name less domain
                        name = split[0];
                    }
                    else {
                        // if no @ extract up to 1st 8 chars of email - will this ever happen?
                        int len = 8;
                        if (email.length() < 8) len = name.length() - 1;
                        name = email.substring(0, len);
                    }
                    // update profile with display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
//                        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });
                }
                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getToken() instead.
                String uid = user.getUid();
                Log.d(TAG, name + ", " + email + ", " + uid);
                Toast.makeText(this, name + ", " + email + ", " + uid, Toast.LENGTH_LONG).show();
            }
            else {
                Log.d(TAG, "No FirebaseUser found.");
                Toast.makeText(this, "Please signin. No FirebaseUser found.", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if (id == R.id.action_anonauth) {
            startActivity(new Intent(this, AnonymousAuthActivity.class));
            return true;
        }
        else if (id == R.id.action_emailauth) {
            startActivity(new Intent(this, EmailPasswordActivity.class));
            return true;
        }
        else if (id == R.id.action_googleauth) {
            startActivity(new Intent(this, GoogleSignInActivity.class));
            return true;
        }
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new) {
            // new game
            // update the main content by replacing fragments
            int contentId = R.layout.content_new;
            replaceFragment(contentId);
        } else if (id == R.id.nav_join) {

        } else if (id == R.id.nav_trash) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_view) {

        } else if (id == R.id.nav_dims) {

        } else if (id == R.id.nav_name) {

        } else if (id == R.id.nav_owners) {

        } else if (id == R.id.nav_spare) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    // lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        mVacating = false;
        Log.v(TAG, "onStart");
        // Firebase
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVacating = false;
        Log.v(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        mVacating = true;
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        mVacating = true;
        super.onStop();
        Log.v(TAG, "onStop");
        // Firebase
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.v(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "onRestoreInstanceState");
    }
    ///////////////////////////////////////////////////////////////////////////
    // provider refresh callback
    private StoryProvider.OnPlayProviderRefresh getPlayProviderCallback() {
        // instantiate callback
        StoryProvider.OnPlayProviderRefresh callback = new StoryProvider.OnPlayProviderRefresh() {

            @Override
            public void onPlayProviderRefresh(Boolean refresh) {
                Log.d(TAG, "getPlayProviderCallback onPlayProviderRefresh interior...");
                if (!mVacating) {
                    Log.d(TAG, "getPlayProviderCallback onPlayProviderRefresh not vacating...");
//                    refresh(refresh);
                }
            }
        };
        return callback;
    }
    ///////////////////////////////////////////////////////////////////////////
    // replace fragment
    private Boolean replaceFragment(int contentId) {

        Fragment fragment = new ContentFragment();

        ContentFragment cf = (ContentFragment)fragment;
        cf.setPlayProvider(mStoryProvider);

        Bundle args = new Bundle();
        args.putInt(ContentFragment.ARG_CONTENT_ID, contentId);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();


        return true;
    }
///////////////////////////////////////////////////////////////////////////
}

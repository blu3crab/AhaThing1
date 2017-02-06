package com.adaptivehandyapps.ahathing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
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

import com.adaptivehandyapps.ahathing.dal.PlayProvider;

///////////////////////////////////////////////////////////////////////////
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private boolean mVacating = false;

    PlayProvider mPlayProvider;

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
        setPlayProvider(new PlayProvider(this, getPlayProviderCallback()));

        // update the main content with stage
        int contentId = R.layout.content_stage;
        replaceFragment(contentId);
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
    public PlayProvider getPlayProvider() { return mPlayProvider;}
    public Boolean setPlayProvider(PlayProvider playProvider) { mPlayProvider = playProvider; return true;}
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
        if (id == R.id.action_settings) {
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
    private PlayProvider.OnPlayProviderRefresh getPlayProviderCallback() {
        // instantiate callback
        PlayProvider.OnPlayProviderRefresh callback = new PlayProvider.OnPlayProviderRefresh() {

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
        cf.setPlayProvider(mPlayProvider);

        Bundle args = new Bundle();
        args.putInt(ContentFragment.ARG_CONTENT_ID, contentId);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();


        return true;
    }
///////////////////////////////////////////////////////////////////////////
}

package com.adaptivehandyapps.ahathing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.auth.AnonymousAuthActivity;
import com.adaptivehandyapps.ahathing.auth.EmailPasswordActivity;
import com.adaptivehandyapps.ahathing.auth.GoogleSignInActivity;
import com.adaptivehandyapps.ahathing.dal.StoryProvider;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final Integer REQUEST_CODE_GALLERY = 1;
    private static final Boolean FORCE_PHOTO_SELECTION = true;

    private static final Integer INSANE_LIMIT = 1024;

    private boolean mVacating = false;

    private StoryProvider mStoryProvider;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    private String mContentOp = DaoDefs.INIT_STRING_MARKER;
    private String mContentObjType = DaoDefs.INIT_STRING_MARKER;
    private String mContentMoniker = DaoDefs.INIT_STRING_MARKER;

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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final DrawerLayout drawer = mDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
//                if( newState == DrawerLayout.STATE_DRAGGING && drawer.isDrawerOpen(GravityCompat.START) == false ) {
                 if( !drawer.isDrawerOpen(GravityCompat.START)) {
                     // Drawer starting to open
                     Log.d(TAG, "Opening drawer...");
                     FirebaseUser user = mAuth.getCurrentUser();
                     if (user != null) {
                         // if nav photo holder exists
                         ImageView iv_photo = (ImageView) findViewById(R.id.iv_navphoto);
                         if (iv_photo != null) {
                             // default nav photo
                             iv_photo.setImageResource(DaoDefs.LOGO_IMAGE_RESID);
                             // if user photo exists
                             Uri photoUri = user.getPhotoUrl();
                             if (photoUri != null) {
                                 try {
                                     // set nav photo to user photo
                                     String realPath = getRealPathFromURI(photoUri);
                                     Log.d(TAG, "user photo uri " + photoUri.toString());
                                     iv_photo.setImageURI(photoUri);
                                 }
                                 catch (Exception ex) {
                                     Log.e(TAG, "onDrawerStateChanged invalid photo URI exception: " + ex.getMessage());

                                     // TODO: prompt for photo
                                     Uri uri = Uri.parse("@drawable/bluecrab48");

                                     // TODO: extract profile update
                                     // update profile with display name
                                     UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                             .setPhotoUri(uri)
//                .setDisplayName(name)
//                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                             .build();

                                     user.updateProfile(profileUpdates)
                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if (task.isSuccessful()) {
                                                         Log.d(TAG, "User profile updated with photoUri @drawable/bluecrab48");
                                                     }
                                                     else {
                                                         Log.e(TAG, "User profile NOT updated with photoUri @drawable/bluecrab48");
                                                     }
                                                 }
                                             });

                                 }
                             }
                         }
                         TextView tv_name = (TextView) findViewById(R.id.tv_navname);
                         if (tv_name != null) {
                             String name = user.getDisplayName();
                             if (name != null) tv_name.setText(name);
                         }
                         TextView tv_email = (TextView) findViewById(R.id.tv_navemail);
                         if (tv_email != null) {
                             String email = user.getEmail();
                             if (email != null) tv_email.setText(email);
                         }
                     }
                    super.onDrawerStateChanged(newState);
                 }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // setup navigation view
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // create story provider
        setStoryProvider(new StoryProvider(this, getStoryProviderCallback()));

        // add new play
        mStoryProvider.addNewStage(mStoryProvider.getDaoStoryRepo(), mStoryProvider.getDaoStageRepo());

        // set navigation menu
        setNavMenu();

        // TODO: refactor MainActivity onCreate & ContentFragment callback
        // if story ready
        if (mStoryProvider.isStoryReady()) {
            // launch story
            mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
            mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
            mContentMoniker = mStoryProvider.getActiveStory().getMoniker();
            replaceFragment(mContentOp, mContentObjType, mContentMoniker);
        }
        else {
            // if active theatre, launch edit
            if (mStoryProvider.getActiveTheatre() != null) {
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = mStoryProvider.getActiveTheatre().getMoniker();
                replaceFragment(mContentOp, mContentObjType, mContentMoniker);
            }
            else {
                // no theatre active, launch new
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + mStoryProvider.getDaoTheatreRepo().size();
                replaceFragment(mContentOp, mContentObjType, mContentMoniker);
            }
        }

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mStoryProvider.getProviderListener().setTheatreListener();
//                    mStoryProvider.queryThings();
                }
                else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    mStoryProvider.removeFirebaseListener();
                }
                // ...TODO?
            }
        };

    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
    public StoryProvider getStoryProvider() { return mStoryProvider;}
    public Boolean setStoryProvider(StoryProvider storyProvider) { mStoryProvider = storyProvider; return true;}
    ///////////////////////////////////////////////////////////////////////////////////////////
    private Boolean setNavMenu() {

//        if( !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            // Drawer starting to open
//            Log.d(TAG, "setNavMenu - Drawer NOT starting to open...");
//            return false;
//        }
        // append active object to menu title
        String prefix = DaoDefs.INIT_STRING_MARKER;
        int iconId = R.drawable.ic_star_black_48dp;
        Menu menu = mNavigationView.getMenu();
        menu.clear();
        int objTypeCount = DaoDefs.DAOOBJ_TYPE_RESERVE;
        for (int i = 0; i < objTypeCount; i++) {
            String activeName = DaoDefs.INIT_STRING_MARKER;
            if (i == DaoDefs.DAOOBJ_TYPE_THEATRE) {
                iconId = DaoDefs.DAOOBJ_TYPE_THEATRE_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                if (mStoryProvider.isTheatreReady() && mStoryProvider.getActiveTheatre() != null) {
                    activeName = mStoryProvider.getActiveTheatre().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_EPIC) {
                iconId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                if (mStoryProvider.isEpicReady() && mStoryProvider.getActiveEpic() != null) {
                    activeName = mStoryProvider.getActiveEpic().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_STORY) {
                iconId = DaoDefs.DAOOBJ_TYPE_STORY_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                if (mStoryProvider.isStoryReady() && mStoryProvider.getActiveStory() != null) {
                    activeName = mStoryProvider.getActiveStory().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_STAGE) {
                iconId = DaoDefs.DAOOBJ_TYPE_STAGE_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                if (mStoryProvider.isStageReady() && mStoryProvider.getActiveStage() != null) {
                    activeName = mStoryProvider.getActiveStage().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_ACTOR) {
                iconId = DaoDefs.DAOOBJ_TYPE_ACTOR_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                activeName = "actor...";
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_ACTION) {
                iconId = DaoDefs.DAOOBJ_TYPE_ACTION_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                activeName = "action...";
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_OUTCOME) {
                iconId = DaoDefs.DAOOBJ_TYPE_OUTCOME_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                activeName = "outcome...";
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_AUDIT) {
                iconId = DaoDefs.DAOOBJ_TYPE_AUDIT_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER;
                activeName = "recent...";
            }
            else {
                iconId = DaoDefs.DAOOBJ_TYPE_UNKNOWN_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
                activeName = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
            }
            String itemName = prefix.concat(": " + activeName);

            MenuItem menuItem = menu.add(itemName);
            menuItem.setIcon(iconId);
            Log.d(TAG, "setNavMenu  add menu item:" + menuItem.getItemId() + ", itemname: " + menuItem.toString());

        }
        // add theatres
        addSubMenu(DaoDefs.DAOOBJ_TYPE_THEATRE);
        // add epics
        addSubMenu(DaoDefs.DAOOBJ_TYPE_EPIC);
        // add stories
        addSubMenu(DaoDefs.DAOOBJ_TYPE_STORY);
        // add stages
        addSubMenu(DaoDefs.DAOOBJ_TYPE_STAGE);
        // add actors
        addSubMenu(DaoDefs.DAOOBJ_TYPE_ACTOR);
        // add actors
        addSubMenu(DaoDefs.DAOOBJ_TYPE_ACTION);
        // add actors
        addSubMenu(DaoDefs.DAOOBJ_TYPE_OUTCOME);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    private SubMenu addSubMenu(@DaoDefs.DaoObjType int objType) {
        // extract moniker list
        String title = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
        int iconId = DaoDefs.DAOOBJ_TYPE_UNKNOWN_IMAGE_RESID;
        List<String> monikerList = new ArrayList<>();
        if (objType == DaoDefs.DAOOBJ_TYPE_THEATRE) {
            title = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_THEATRE_IMAGE_RESID;
            monikerList = mStoryProvider.getDaoTheatreRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_EPIC) {
            title = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
            monikerList = mStoryProvider.getDaoEpicRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_STORY) {
            title = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_STORY_IMAGE_RESID;
            monikerList = mStoryProvider.getDaoStoryRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_STAGE) {
            title = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_STAGE_IMAGE_RESID;
            monikerList = mStoryProvider.getDaoStageRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_ACTOR) {
            title = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_ACTOR_IMAGE_RESID;
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_ACTION) {
            title = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_ACTION_IMAGE_RESID;
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_OUTCOME) {
            title = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_OUTCOME_IMAGE_RESID;
        }
        // add submenu from moniker list plus a "new" item
        Menu menu = mNavigationView.getMenu();
        SubMenu subMenu = menu.addSubMenu(title);
        subMenu.clear();
        MenuItem subMenuItem;
        for (String moniker : monikerList) {
            subMenuItem = subMenu.add(moniker);
            subMenuItem.setIcon(iconId);
            Log.d(TAG, "addSubMenu submenu item:" + subMenuItem.getItemId() + ", itemname: " + subMenuItem.toString());
        }
        subMenuItem = subMenu.add("New " + title);
        subMenuItem.setIcon(iconId);
        Log.d(TAG, "addSubMenu submenu item:" + subMenuItem.getItemId() + ", itemname: " + subMenuItem.toString());
        return subMenu;
    }
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

                // if display name undefined
                if (name == null) {
                    // update firebase user profile with display name derived from email
                    name = updateFirebaseDisplayNameFromEmail(user, email);
                }

                if (FORCE_PHOTO_SELECTION) {
//                    if (FORCE_PHOTO_SELECTION || photoUrl == null) {
                    // update firebase user profile with photo uri selected from gallery
                    updateFirebasePhotoUrlFromGallery();
                }

                // confirm Firebase updates
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    String uid = user.getUid();
                    Log.d(TAG, name + ", " + email + ", " + uid);
                    Toast.makeText(this, user.getDisplayName() + ", " + user.getEmail() + ", " + user.getPhotoUrl() + ", " + uid, Toast.LENGTH_LONG).show();
                }
            }
            else {
                Log.d(TAG, "No Firebase User found.");
                Toast.makeText(this, "Please signin. No Firebase User found.", Toast.LENGTH_LONG).show();
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
        String itemname = item.toString();
        String itemSplit[] = itemname.split(":");
        Log.d(TAG, "onNavigationItemSelected itemname: " + itemname + ", split:" + itemSplit[0]);
//        for static menu items, extract id & compare to resource
//        int id = item.getItemId();
//        if (id == R.id.nav_theatre)) {
        // check split for top level object: active theatre, story, stage, etc...
        if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
            if (mStoryProvider.getActiveTheatre() != null) {
                replaceFragment(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT, DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER, mStoryProvider.getActiveTheatre().getMoniker());
            }
            else {
                String moniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + mStoryProvider.getDaoTheatreRepo().size();
                replaceFragment(ContentFragment.ARG_CONTENT_VALUE_OP_NEW, DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER, moniker);
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
            if (mStoryProvider.getActiveEpic() != null) {
                replaceFragment(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT, DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER, mStoryProvider.getActiveEpic().getMoniker());
            }
            else {
                String moniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + mStoryProvider.getDaoEpicRepo().size();
                replaceFragment(ContentFragment.ARG_CONTENT_VALUE_OP_NEW, DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER, moniker);
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
            if (mStoryProvider.getActiveStory() != null) {
                replaceFragment(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT, DaoDefs.DAOOBJ_TYPE_STORY_MONIKER, mStoryProvider.getActiveStory().getMoniker());
            }
            else {
                String moniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + mStoryProvider.getDaoStoryRepo().size();
                replaceFragment(ContentFragment.ARG_CONTENT_VALUE_OP_NEW, DaoDefs.DAOOBJ_TYPE_STORY_MONIKER, moniker);
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER)) {
            replaceFragment(ContentFragment.ARG_CONTENT_VALUE_OP_SHOWLIST, DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER, "audit trial");
        }
        else {
            // submenu selection - test for existing selection or new object
            if (itemname.toLowerCase().contains("new")) {
                // new object - extract object type
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                String split[] = itemname.split(" ");
                mContentObjType = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
                if (split.length > 1) {
                    mContentObjType = split[1];
                }
                // default moniker to list size
                if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                    Integer next = mStoryProvider.getDaoTheatreRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    while (mStoryProvider.getDaoTheatreRepo().contains(mContentMoniker) && next < INSANE_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    Integer next = mStoryProvider.getDaoEpicRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    while (mStoryProvider.getDaoEpicRepo().contains(mContentMoniker) && next < INSANE_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + mStoryProvider.getDaoStoryRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + mStoryProvider.getDaoStageRepo().size();
                }
                // launch DaoMaker
                replaceFragment(mContentOp, mContentObjType, mContentMoniker);
            }
            else {
                // existing object - set active based on find object type
                if (mStoryProvider.getDaoTheatreRepo().get(itemname) != null) {
                    // theatre - set active
                    mStoryProvider.setActiveTheatre((DaoTheatre) mStoryProvider.getDaoTheatreRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoEpicRepo().get(itemname) != null) {
                    // Epic - set active
                    mStoryProvider.setActiveEpic((DaoEpic) mStoryProvider.getDaoEpicRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoStoryRepo().get(itemname) != null) {
                    // story - set active
                    mStoryProvider.setActiveStory((DaoStory) mStoryProvider.getDaoStoryRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoStageRepo().get(itemname) != null) {
                    // stage - set active
                    mStoryProvider.setActiveStage((DaoStage)mStoryProvider.getDaoStageRepo().get(itemname));
                }
                // update nav menu
                setNavMenu();
                // launch active story
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = mStoryProvider.getActiveStory().getMoniker();
                replaceFragment(mContentOp, mContentObjType, mContentMoniker);
            }

        }
        // close drawer
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        mDrawerLayout.closeDrawer(GravityCompat.START);
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
    private StoryProvider.OnStoryProviderRefresh getStoryProviderCallback() {
        // instantiate callback
        StoryProvider.OnStoryProviderRefresh callback = new StoryProvider.OnStoryProviderRefresh() {

            @Override
            public void onPlayProviderRefresh(Boolean refresh) {
                Log.d(TAG, "getStoryProviderCallback onPlayProviderRefresh interior...");
                if (!mVacating) {
                    Log.d(TAG, "getStoryProviderCallback onStoryProviderRefresh not vacating...setNavMenu");
                    // set navigation menu
                    setNavMenu();

//                    refresh(refresh);
                }
            }
        };
        return callback;
    }
    ///////////////////////////////////////////////////////////////////////////
    // replace fragment
    private Boolean replaceFragment(String op, String objType, String moniker) {

        // TODO: refactor MainActivity onCreate & ContentFragment callback
        Fragment fragment = new ContentFragment();

        ContentFragment cf = (ContentFragment)fragment;
        cf.setStoryProvider(mStoryProvider);

        Bundle args = new Bundle();
        args.putString(ContentFragment.ARG_CONTENT_KEY_OP, op);
        args.putString(ContentFragment.ARG_CONTENT_KEY_OBJTYPE, objType);
        args.putString(ContentFragment.ARG_CONTENT_KEY_MONIKER, moniker);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();


        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // update firebase user profile with display name derived from email
    private String updateFirebaseDisplayNameFromEmail(FirebaseUser user, String email) {
        // default name by extracting up to 8 chars from start of email
        int len = 8;
        if (email.length() < 8) len = email.length() - 1;
        String name = email.substring(0, len);

        // try splitting email at @
        String split[];
        split = email.split("@");
        // if @ present
        if (split.length > 1) {
            // assign display name from email name less domain
            name = split[0];
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
        return name;
    }
    ///////////////////////////////////////////////////////////////////////////
    // update firebase user profile with photo uri selected from gallery
    private Boolean updateFirebasePhotoUrlFromGallery() {

        // To open up a gallery browser
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_CODE_GALLERY);

        return true;
    }
    // To handle when an image is selected from the browser, add the following to your Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_GALLERY) {

                // currImageURI is the global variable I'm using to hold the content:// URI of the image
                final Uri currImageURI = data.getData();
                Log.d(TAG, "Gallery photoUri selection: " + currImageURI.toString());

                String realPath = getRealPathFromURI(currImageURI);
                Log.d(TAG, "Gallery photo (real path) selection: " + realPath);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // TODO: extract profile update
                    // update profile with display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(currImageURI)
//                .setDisplayName(name)
//                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated with photoUri " + currImageURI.toString());
                                    }
                                    else {
                                        Log.e(TAG, "User profile NOT updated with photoUri " + currImageURI.toString());
                                    }
                                }
                            });
                }
            }
        }
    }

    // And to convert the image URI to the direct file system path of the image file
    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery( contentUri,
        Cursor cursor = getContentResolver().query(contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }///////////////////////////////////////////////////////////////////////////
}

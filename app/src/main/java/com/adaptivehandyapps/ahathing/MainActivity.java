/*
 * Project: AhaThing1
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker JAN 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adaptivehandyapps.ahathing;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.auth.AnonymousAuthActivity;
import com.adaptivehandyapps.ahathing.auth.EmailPasswordActivity;
import com.adaptivehandyapps.ahathing.auth.GoogleSignInActivity;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

///////////////////////////////////////////////////////////////////////////
// MainActivity
//      implements NavigationView
//          NavMenu - build NavMenu & sub menus
//          NavItem - parses nav selection extracting op, objtype, moniker
//      services (bound)
//          RepoProvider - manage FireBase remote & local repo
//          PlayListService - manage local active playlist
//      fragment management
//          ContentFragment - present fragments based on current op, objtype, moniker
//      helpers
//          PrefsUtils - manages preferences
//      HAS A objects
//          StageManager - execute stage actions, outcomes
//          SoundManager - play sounds
//      auth
//          FireBase auth listener
//          signin
//
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

//    public static final String STAR_MONIKER_NADA = "Signin Here!";
//
    private static final Integer REQUEST_CODE_GALLERY = 1;
    private static final Boolean FORCE_PHOTO_SELECTION = true;

    private boolean mVacating = false;

    ///////////////////////////////////////////////////////////////////////////

    private Activity mActivity;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private NavMenu mNavMenu;
    private NavItem mNavItem;

    private String mContentOp = DaoDefs.INIT_STRING_MARKER;
    private String mContentObjType = DaoDefs.INIT_STRING_MARKER;
    private String mContentMoniker = DaoDefs.INIT_STRING_MARKER;

    // Firebase auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

//    // stargate
//    private DaoStarGate daoStarGate = new DaoStarGate();
//
//    public String getStarMoniker() { return daoStarGate.getStarMoniker(); }
//    public String addActorBoard() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String starMoniker = STAR_MONIKER_NADA;
//        if (user != null && user.getDisplayName() != null) {
//            Log.d(TAG, "Firebase DisplayName " + user.getDisplayName());
//            // update StarMoniker with display name
//            starMoniker = user.getDisplayName();
//        }
//        // update StarMoniker
//        daoStarGate.addActorBoard(starMoniker);
//        return starMoniker;
//    }
//
    ///////////////////////////////////////////////////////////////////////////
    private SoundManager mSoundManager;
    public SoundManager getSoundManager() {
        return mSoundManager;
    }
    public void setSoundManager(SoundManager soundManager) {
        this.mSoundManager = soundManager;
    }
    ///////////////////////////////////////////////////////////////////////////
    private StageManager mStageManager;
    public StageManager getStageManager() {
        return mStageManager;
    }
    public void setStageManager(StageManager stageManager) {
        this.mStageManager = stageManager;
    }
    ///////////////////////////////////////////////////////////////////////////
    private StarGateManager mStarGateManager;
    public StarGateManager getStarGateManager() {
        return mStarGateManager;
    }
    public void setStarGateManager(StarGateManager StarGateManager) {
        this.mStarGateManager = StarGateManager;
    }

    ///////////////////////////////////////////////////////////////////////////
    // playlist service
    private PlayListService mPlayListService;
    private boolean mPlayListBound = false;
//    // singleton playlist
//    private static final PlayList playList = new PlayList();
//    public static PlayList getPlayListInstance() { return playList; }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mPlayListConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayListService.LocalBinder binder = (PlayListService.LocalBinder) service;
            mPlayListService = binder.getService();
            mPlayListBound = true;
            Log.d(TAG, "onServiceConnected: mPlayListBound " + mPlayListBound + ", mPlayListService " + mPlayListService);
            // bind to repo provider
            bindRepoProvider();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPlayListBound = false;
        }
    };

    public PlayListService getPlayListService() {
        return mPlayListService;
    }
    public void setPlayListService(PlayListService playListService) {
        mPlayListService = playListService;
    }
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // repo provider service
    private RepoProvider mRepoProvider;
    private boolean mRepoProviderBound = false;
//    // singleton repo
//    private static final RepoProvider repoProvider = new RepoProvider();
//    public static RepoProvider getRepoProviderInstance() { return repoProvider; }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mRepoProviderConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RepoProvider.LocalBinder binder = (RepoProvider.LocalBinder) service;
            mRepoProvider = binder.getService();
            mRepoProviderBound = true;
            Log.d(TAG, "onServiceConnected: mRepoProviderBound " + mRepoProviderBound + ", mRepoProviderService " + mRepoProvider);

            // assign repo callback
            getRepoProvider().setCallback(getRepoProviderCallback());
            getRepoProvider().setPlayListService(getPlayListService());

            if (mNavMenu != null) {
                mNavMenu.setPlayListService(getPlayListService());
                mNavMenu.setRepoProvider(getRepoProvider());
            }
            if (mNavItem != null) {
                mNavItem.setPlayListService(getPlayListService());
                mNavItem.setRepoProvider(getRepoProvider());
            }

            // all services bound
            if (mPlayListBound) {
                // set navigation menu
                buildNavMenu();
            }
            // trigger stargate update for this device
            getStarGateManager().setStarGate();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPlayListBound = false;
        }
    };

    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }

    public void setRepoProvider(RepoProvider repoProvider) {
        mRepoProvider = repoProvider;
    }
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        Log.d(TAG, PrefsUtils.toString(this));
        // set content to main
        setContentView(R.layout.activity_main);

        // TODO: determine landscape or portrait & set image source accordingly
        View iv_splash = findViewById(R.id.iv_splash);
        if (iv_splash != null) {
            Log.d(TAG, "ready to splash!");
        }
        else {
            Log.e(TAG,"Oops!  where's the splash view?");
        }
        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                                 // TODO: get photo of user
//                                 try {
//                                     // set nav photo to user photo
////                                     String realPath = getRealPathFromURI(photoUri);
//                                     Log.d(TAG, "user photo uri " + photoUri.toString());
//                                     iv_photo.setImageURI(photoUri);
//                                 }
//                                 catch (Exception ex) {
//                                     Log.e(TAG, "onDrawerStateChanged invalid photo URI exception: " + ex.getMessage());

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

//                                 }
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
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // instantiate nav  menu & item
        mNavMenu = new NavMenu();
        mNavItem = new NavItem();

        // establish sound manager
        mSoundManager = new SoundManager(this);
        // establish stage manager
        mStageManager = new StageManager(this);
        // establish StarGate manager
        mStarGateManager = new StarGateManager(this);

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // TODO: test sign out - sign in sequence
                    // restore repo provider listeners based on auth user
                    if (getRepoProvider() != null) getRepoProvider().setPlayListService(getPlayListService());
                }
                else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // TODO: clear database, listeners, etc.?
                    if (getRepoProvider() != null) getRepoProvider().removeFirebaseListener();
                }
                // trigger stargate update for this device
                getStarGateManager().setStarGate();
                // if StarGateManager model exists
                if (getStarGateManager().getStarGateModel() != null) {
                    // update existing StarGateManager model
                    getStarGateManager().updateModel(getStarGateManager().getStarGateModel());
                }
            }
        };
//        // instantiate nav  menu & item
//        mNavMenu = new NavMenu();
//        mNavItem = new NavItem();
//
//        // establish sound manager
//        mSoundManager = new SoundManager(this);
//        // establish stage manager
//        mStageManager = new StageManager(this);
//        // establish StarGate manager
//        mStarGateManager = new StarGateManager(this);
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
    ///////////////////////////////////////////////////////////////////////////////////////////
    public Boolean buildNavMenu() {

//        if( !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            // Drawer starting to open
//            Log.d(TAG, "buildNavMenu - Drawer NOT starting to open...");
//            return false;
//        }
        // build nav menu
        mNavMenu.build(mNavigationView, getStarGateManager().getStarMoniker());

        // if story ready
        if (getPlayListService().getActiveStory() != null) {
            Log.d(TAG, "buildNavMenu: launching story...");
            // launch story
            mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
            mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
            mContentMoniker = getPlayListService().getActiveStory().getMoniker();
        }
        else {
            Log.d(TAG, "buildNavMenu: Story NOT ready!");
            // launch stargate
            mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_STARGATE;
            mContentObjType = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
            mContentMoniker = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
        }
        ContentFragment.replaceFragment(this, mContentOp, mContentObjType, mContentMoniker);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String itemname = item.toString();
        String itemSplit[] = itemname.split(":");
        Log.d(TAG, "onNavigationItemSelected itemname: " + itemname + ", split:" + itemSplit[0]);
//        for static menu items, extract id & compare to resource
//        int id = item.getItemId();
        // TODO: check double replace fragment?
        // parse nav item - returns update trigger
        if (mNavItem.parse(itemname, itemSplit)) {
            // update nav menu
            buildNavMenu();
        }
        mContentOp = mNavItem.getOp();
        mContentObjType = mNavItem.getObjType();
        mContentMoniker = mNavItem.getMoniker();
        // if op has been assigned
        if (!mContentOp.equals(ContentFragment.ARG_CONTENT_VALUE_OP_NADA)) {
            ContentFragment.replaceFragment(this, mContentOp, mContentObjType, mContentMoniker);
        }
        else {
            Log.e(TAG, "Oops! Unknown selection: " + itemname);
        }
        // close drawer
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean mStageViewActive = false;
    public Boolean isStageViewActive() { return mStageViewActive; }
    public Boolean setStageViewActive(Boolean active) { mStageViewActive = active; return active;}
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (isStageViewActive()) {
                Log.d(TAG,"Stage view active! allow change of focus...");
                super.onBackPressed();
            }
            else {
                Log.d(TAG,"Stage view NOT active! restore stage focus...");
                // if story available
                if (getPlayListService().getActiveStory() != null) {
                    Log.d(TAG, "onBackPressed: Story ready!");
                    // launch PLAY story
                    mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                    mContentMoniker = getPlayListService().getActiveStory().getMoniker();
                }
                else {
                    Log.d(TAG, "onBackPressed: Story NOT ready!");
                    // launch stargate
                    mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_STARGATE;
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
                }
                ContentFragment.replaceFragment(this, mContentOp, mContentObjType, mContentMoniker);
            }
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

    ///////////////////////////////////////////////////////////////////////////////////////////
    // lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        mVacating = false;
        Log.v(TAG, "onStart");
        // Firebase
        mAuth.addAuthStateListener(mAuthListener);
        // Bind to Local Services
        bindPlayListService();
    }
    public Boolean bindPlayListService() {
        Intent intentPlayList = new Intent(this, PlayListService.class);
        bindService(intentPlayList, mPlayListConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onStart: mPlayListBound " + mPlayListBound + ", mPlayListService " + mPlayListService);
        return true;
    }
    public Boolean bindRepoProvider() {
        Intent intentRepoProvider = new Intent(this, RepoProvider.class);
        bindService(intentRepoProvider, mRepoProviderConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onStart: mRepoProviderBound " + mRepoProviderBound + ", mRepoProviderService " + mRepoProvider);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVacating = false;
        Log.v(TAG, "onResume");
        Log.d(TAG, PrefsUtils.toString(this));
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
        Log.d(TAG, PrefsUtils.toString(this));
        // Firebase
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // TODO: remove firebase listeners?
        ///////////////////////////////////////////////////////////////////////////////////////////
        // Unbind from services
        if (mPlayListBound) {
            Log.d(TAG, "onStop unbinding PlayList service" + mPlayListConnection.toString());
            unbindService(mPlayListConnection);
            mPlayListBound = false;
        }
        if (mRepoProviderBound) {
            Log.d(TAG, "onStop unbinding RepoProvider service" + mRepoProviderConnection.toString());
            unbindService(mRepoProviderConnection);
            mRepoProviderBound = false;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
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
    private RepoProvider.OnRepoProviderRefresh getRepoProviderCallback() {
        // instantiate callback
        RepoProvider.OnRepoProviderRefresh callback = new RepoProvider.OnRepoProviderRefresh() {

            @Override
            public void onRepoProviderRefresh(Boolean refresh) {
                Log.d(TAG, "getRepoProviderCallback OnRepoProviderRefresh interior...");
                if (!mVacating) {
                    Log.d(TAG, "getRepoProviderCallback OnRepoProviderRefresh not vacating...buildNavMenu");
                    // ensure playlist is bound to repo
                    if (!getPlayListService().isRepoProviderBound()) {
                        // bind playlist to repo
                        getPlayListService().bindRepoProvider();
                    }
                    // set navigation menu
                    buildNavMenu();
                    // if op has been assigned
                    if (!mContentOp.equals(ContentFragment.ARG_CONTENT_VALUE_OP_NADA)) {
                        ContentFragment.replaceFragment(mActivity, mContentOp, mContentObjType, mContentMoniker);
                    }
                    else {
                        Log.e(TAG, "Oops! Nada op... ");
                    }

                }
            }
        };
        return callback;
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
    }
    ///////////////////////////////////////////////////////////////////////////
}

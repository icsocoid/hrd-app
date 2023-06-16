package com.sia.als.mail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sia.als.R;
import com.sia.als.fragment.EmailFragment;
import com.sia.als.mail.database.CurrentUser;
import com.sia.als.mail.database.EmailMessage;
import com.sia.als.mail.database.User;
import com.sia.als.mail.fragment.FolderFragment;
import com.sia.als.mail.fragment.InboxFragment;
import com.sia.als.mail.fragment.SmartBoxFragment;
import com.sia.als.mail.services.NotificationMaker;
import com.sia.als.mail.utils.Constants;
import com.sia.als.mail.utils.Settings;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class MainMailActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

//    @Bind(R.id.main_tool_bar)
    Toolbar toolbar;
//    @Bind(R.id.main_frame_layout)
    FrameLayout frameLayout;

    private Drawer drawer;
    private AccountHeader accountHeader;
    private PrimaryDrawerItem pInbox, pSmartBox, pSentBox, pTrashBox;
    private SecondaryDrawerItem sSettings, sFeedback, sContribute;
    private ArrayList<IProfile> allAccountHeaders;
    private IDrawerItem selectedDrawerItem;
    private String currentDrawerItem;
    private Settings settings;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 108;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);

        settings = new Settings(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.main_tool_bar);
        frameLayout = (FrameLayout) findViewById(R.id.main_frame_layout);

        setupToolbar();
        setupDrawer();

        if(savedInstanceState != null){
            currentDrawerItem = savedInstanceState.getString("currentDrawerItem", "inbox");
        }
        else {
            currentDrawerItem = "inbox";
        }

        switch(currentDrawerItem){
            case "inbox": selectedDrawerItem = pInbox; break;
            case "smartbox": selectedDrawerItem = pSmartBox; break;
            case "sentbox": selectedDrawerItem = pSentBox; break;
            case "trashbox": selectedDrawerItem = pTrashBox; break;
            default: selectedDrawerItem = pInbox;
                currentDrawerItem = "inbox";
        }

        setSelectedAccountHeader(true);

        showUpdatesDialog();
        checkPermission();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentDrawerItem", currentDrawerItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedDrawerItem != null) {
            setToolbarTitle(selectedDrawerItem);
        }
        setSelectedAccountHeader(false);
    }

    private void setupToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbarText));
//        setSupportActionBar(toolbar);
    }

    private void setupDrawer() {
        pInbox = new PrimaryDrawerItem().withName(getString(R.string.drawer_inbox)).withIcon(R.drawable.inbox);
        pSmartBox = new PrimaryDrawerItem().withName(getString(R.string.drawer_smartbox)).withIcon(R.drawable.fire_element);
        pSentBox = new PrimaryDrawerItem().withName(getString(R.string.drawer_sent)).withIcon(R.drawable.sent);
        pTrashBox = new PrimaryDrawerItem().withName(getString(R.string.drawer_trash)).withIcon(R.drawable.trash);

        sSettings = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(getString(R.string.drawer_settings)).withIcon(R.drawable.settings);
        sFeedback = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(getString(R.string.drawer_feedback)).withIcon(R.drawable.feedback);
        sContribute = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(getString(R.string.drawer_contribute)).withIcon(R.drawable.github_drawer);

        sSettings.withSelectable(false);
        sFeedback.withSelectable(false);
        sContribute.withSelectable(false);

        setupAllAccountHeaders();
        final String createAccountString = getString(R.string.drawer_new_account);

        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withProfiles(allAccountHeaders)
                .withHeaderBackground(new ColorDrawable(getResources().getColor(R.color.primary_dark)))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile.getName().getText().equals(createAccountString)) {
                            CurrentUser.setCurrentUser(null, getApplicationContext());

                            return true;
                        } else {
                            CurrentUser.setCurrentUser(User.getUserFromUserName(profile.getName().getText()), getApplicationContext());
                            startActivity(new Intent(MainMailActivity.this, EmailFragment.class));
                            drawer.closeDrawer();
                            if (selectedDrawerItem == null) {
                                selectedDrawerItem = pInbox;
                                currentDrawerItem = "inbox";
                            }
                            else if (!selectedDrawerItem.isSelectable()) {
                                selectedDrawerItem = pInbox;
                                currentDrawerItem = "inbox";
                            }
                            setDrawerSelection(selectedDrawerItem);
                            setToolbarTitle(selectedDrawerItem);
                            return true;
                        }
                    }
                })
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
//                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        pInbox,
                        pSentBox,
                        pTrashBox,
                        new DividerDrawerItem(),
                        pSmartBox,
                        new DividerDrawerItem(),
                        sSettings,
                        sFeedback,
                        sContribute
                ).withDelayOnDrawerClose(200)
                .withCloseOnClick(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Fragment fragment = null;
                        Bundle bundle = null;
                        String fragmentTag = "";
                        drawer.closeDrawer();
                        if (drawerItem == null)
                            drawerItem = pInbox;

                        setToolbarTitle(drawerItem);
                        if (drawerItem.equals(pInbox)) {
                            selectedDrawerItem = (PrimaryDrawerItem) drawerItem;
                            currentDrawerItem = "inbox";

                            if(getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_INBOX)!=null) {
                                fragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_INBOX);
                            }
                            else {
                                fragment = new InboxFragment();
                            }

                            Snackbar.make(frameLayout, getString(R.string.drawer_inbox), Snackbar.LENGTH_SHORT).show();
                            fragmentTag = Constants.FRAGMENT_TAG_INBOX;
                        } else if (drawerItem.equals(pSmartBox)) {
                            selectedDrawerItem = (PrimaryDrawerItem) drawerItem;
                            currentDrawerItem = "smartbox";
                            fragment = new SmartBoxFragment();
                            Snackbar.make(frameLayout, getString(R.string.drawer_smartbox), Snackbar.LENGTH_SHORT).show();
                            fragmentTag = Constants.FRAGMENT_TAG_SMARTBOX;
                        } else if (drawerItem.equals(pSentBox)) {
                            selectedDrawerItem = (PrimaryDrawerItem) drawerItem;
                            currentDrawerItem = "sentbox";

                            if(getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_FOLDER + Constants.SENT)!=null){
                                fragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_FOLDER + Constants.SENT);
                            }
                            else{
                                fragment = new FolderFragment();
                                bundle = new Bundle();
                                bundle.putString(Constants.FOLDER, Constants.SENT);
                                fragment.setArguments(bundle);
                            }


                            fragmentTag = Constants.FRAGMENT_TAG_FOLDER + Constants.SENT;
                            Snackbar.make(frameLayout, getString(R.string.drawer_sent), Snackbar.LENGTH_SHORT).show();
                        } else if (drawerItem.equals(pTrashBox)) {
                            selectedDrawerItem = (PrimaryDrawerItem) drawerItem;
                            currentDrawerItem = "trashbox";

                            if(getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_FOLDER + Constants.TRASH)!=null){
                                fragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_FOLDER + Constants.TRASH);
                            }
                            else{
                                fragment = new FolderFragment();
                                bundle = new Bundle();
                                bundle.putString(Constants.FOLDER, Constants.TRASH);
                                fragment.setArguments(bundle);
                            }

                            fragmentTag = Constants.FRAGMENT_TAG_FOLDER + Constants.TRASH;
                            Snackbar.make(frameLayout, getString(R.string.drawer_trash), Snackbar.LENGTH_SHORT).show();
//                        } else if (drawerItem.equals(sSettings)) {
//                            Intent intent = new Intent(MainMailActivity.this, SettingsActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            startActivity(intent);
//                            return true;
//                        } else if (drawerItem.equals(sFeedback)) {
//                            Intent intent = new Intent(MainMailActivity.this, FeedbackActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            startActivity(intent);
//                            return true;
//                        } else if (drawerItem.equals(sContribute)) {
//                            Intent intent = new Intent(MainMailActivity.this, ContributeActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            startActivity(intent);
//                            return true;
                        }
                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.main_frame_layout, fragment, fragmentTag).commit();
                            fragmentManager.popBackStack();
                        }
                        return false;
                    }
                })
                .build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }

    private void setSelectedAccountHeader(boolean shouldClick) {
        String currentUsername = CurrentUser.getCurrentUser(this).getUsername();
        for (int i = 0; i < allAccountHeaders.size(); i++) {
            if (allAccountHeaders.get(i).getName().getText().equals(currentUsername))
                accountHeader.setActiveProfile(allAccountHeaders.get(i), shouldClick);
        }
    }

    private void setupAllAccountHeaders() {
        allAccountHeaders = new ArrayList<>();
        List<User> users = User.getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(users.get(i).getUsername());
            /**
             * A fun image for each profile drawer
             */
            if (i % 3 == 0)
                profileDrawerItem.withIcon(getResources().getDrawable(R.drawable.user_white));
            if (i % 3 == 1)
                profileDrawerItem.withIcon(getResources().getDrawable(R.drawable.user_blue));
            if (i % 3 == 2)
                profileDrawerItem.withIcon(getResources().getDrawable(R.drawable.user_grey));
            allAccountHeaders.add(profileDrawerItem);
        }

        final String createAccount = getString(R.string.drawer_new_account);
        allAccountHeaders.add(new ProfileDrawerItem().withName(createAccount).withIcon(getResources().getDrawable(R.drawable.plus)));
    }

    public void showLogoutDialog(final User currentUser) {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setTitle(getString(R.string.dialog_title_logout));
        if (User.getUsersCount() >= 2)
            materialDialog.setMessage(getString(R.string.dialog_msg_logout_multi));
        else
            materialDialog.setMessage(getString(R.string.dialog_msg_logout_single));
        materialDialog.setNegativeButton("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
        materialDialog.setPositiveButton(getString(R.string.dialog_btn_logout), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settings.save(Settings.KEY_MOBILE_DATA, false);

                EmailMessage.deleteAllMailsOfUser(currentUser);

                NotificationMaker.cancelNotification(getApplicationContext());
                materialDialog.dismiss();
                Snackbar.make(frameLayout, getString(R.string.snackbar_logging_out), Snackbar.LENGTH_LONG).show();

                /**
                 * Delete the current User and set the next user in line as current user
                 */
                User.deleteUser(currentUser);
                if (!User.getAllUsers().isEmpty())
                    CurrentUser.setCurrentUser(User.getAllUsers().get(0), getApplicationContext());
                else
                    CurrentUser.setCurrentUser(null, getApplicationContext());


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainMailActivity.this, EmailFragment.class));
                    }
                }, 1000);
            }
        });
        materialDialog.show();
    }

    private void setToolbarTitle(IDrawerItem drawerItem) {
        String currentUserName = User.getUserThreeLetterName(CurrentUser.getCurrentUser(this));
        String toolbarTitle = "";
        if (((PrimaryDrawerItem) drawerItem).getName().getText().equals(pInbox.getName().getText()))
            toolbarTitle = getString(R.string.drawer_inbox) + " : " + currentUserName;
        if (((PrimaryDrawerItem) drawerItem).getName().getText().equals(pSentBox.getName().getText()))
            toolbarTitle = getString(R.string.drawer_sent) + " : " + currentUserName;
        if (((PrimaryDrawerItem) drawerItem).getName().getText().equals(pSmartBox.getName().getText()))
            toolbarTitle = getString(R.string.drawer_smartbox) + " : " + currentUserName;
        if (((PrimaryDrawerItem) drawerItem).getName().getText().equals(pTrashBox.getName().getText()))
            toolbarTitle = getString(R.string.drawer_trash) + " : " + currentUserName;
        getSupportActionBar().setTitle(toolbarTitle);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void setDrawerSelection(IDrawerItem drawerItem) {
        if (((PrimaryDrawerItem) drawerItem).getName().getText().equals(pInbox.getName().getText()))
            drawer.setSelection(pInbox);
        if (((PrimaryDrawerItem) drawerItem).getName().getText().equals(pSentBox.getName().getText()))
            drawer.setSelection(pSentBox);
        if (((PrimaryDrawerItem) drawerItem).getName().getText().equals(pSmartBox.getName().getText()))
            drawer.setSelection(pSmartBox);
        if (((PrimaryDrawerItem) drawerItem).getName().getText().equals(pTrashBox.getName().getText()))
            drawer.setSelection(pTrashBox);
    }

    private void showUpdatesDialog() {
        if (!settings.getBoolean(Settings.KEY_UPDATE_SHOWN)) {
            final MaterialDialog materialDialog = new MaterialDialog(MainMailActivity.this);
            materialDialog
                    .setTitle(getString(R.string.dialog_title_updates_1))
                    .setMessage(getString(R.string.dialog_msg_updates_1))
                    .setPositiveButton(getString(R.string.dialog_btn_positive_updates_1), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settings.save(Settings.KEY_UPDATE_SHOWN, true);
                            drawer.openDrawer();
                            materialDialog.dismiss();
                        }
                    })
                    .setCanceledOnTouchOutside(false);
            materialDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(frameLayout, "Great! You can now save images to gallery", Snackbar.LENGTH_LONG).show();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    final MaterialDialog materialDialog = new MaterialDialog(MainMailActivity.this);
                    materialDialog.setTitle("STORAGE PERMISSION")
                            .setMessage("If you disable this, you won't be able to download attachments! Are you sure?")
                            .setCanceledOnTouchOutside(false)
                            .setPositiveButton("Enable", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkPermission();
                                    materialDialog.dismiss();
                                }
                            })
                            .setNegativeButton("Disable, I'm Sure", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                    Snackbar.make(frameLayout, "You shall not be able to save attachments", Snackbar.LENGTH_LONG).show();
                                }
                            });
                    materialDialog.show();
                }
                return;
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Not Granted. Unable to download Image.");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "Showing shouldShowRequestPermissionRationale");
                final MaterialDialog materialDialog = new MaterialDialog(this);
                materialDialog
                        .setTitle("STORAGE PERMISSION")
                        .setMessage("We need this to save attachments.")
                        .setCanceledOnTouchOutside(false)
                        .setPositiveButton("Got It!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                materialDialog.dismiss();
                                ActivityCompat.requestPermissions(MainMailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                            }
                        });
                materialDialog.show();
            } else {
                Log.d(TAG, "Requesting Permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        } else {
            Log.d(TAG, "Already Granted");
        }
    }


}
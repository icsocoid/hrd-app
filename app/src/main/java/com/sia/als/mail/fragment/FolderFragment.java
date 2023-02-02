package com.sia.als.mail.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sia.als.MainActivity;
import com.sia.als.R;
import com.sia.als.mail.adapters.MailAdapter;
import com.sia.als.mail.asynTasks.MultiMailAction;
import com.sia.als.mail.asynTasks.MultiMailActionListener;
import com.sia.als.mail.asynTasks.RefreshInbox;
import com.sia.als.mail.asynTasks.RefreshInboxListener;
import com.sia.als.mail.database.CurrentUser;
import com.sia.als.mail.database.EmailMessage;
import com.sia.als.mail.database.User;
import com.sia.als.mail.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by rish on 6/10/15.
 */

public class FolderFragment extends Fragment implements RefreshInboxListener, MultiMailActionListener, MailAdapter.MultiMailActionSelectedListener {

//    @Bind(R.id.folder_empty_view)
    LinearLayout emptyLayout;
//    @Bind(R.id.folder_recycleView)
    RecyclerView recyclerView;
//    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;
//    @Bind(R.id.folder_delete_fab)
    FloatingActionButton fabDelete;

    private MailAdapter mailAdapter;
    private ProgressDialog progressDialog, progressDialog2;
    private ArrayList<EmailMessage> allEmails;
    private User currentUser;
    private String folder;
    private MenuItem selectAll;
    private boolean markedMails[];

    public FolderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder, container, false);
//        ButterKnife.bind(FolderFragment.this, rootView);
        Bundle args = getArguments();
        folder = args.getString(Constants.FOLDER, Constants.SENT);

        if (folder.equals(Constants.SENT))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.sent));
        else if (folder.equals(Constants.TRASH))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.trash));

        currentUser = CurrentUser.getCurrentUser(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        registerInternalBroadcastReceivers();
        setupMailAdapter();
        setupSwipeRefreshLayout();
        setupSearchBar();

        new RefreshInbox(currentUser, getActivity(), FolderFragment.this, folder, Constants.REFRESH_TYPE_REFRESH).execute();

        swipeRefreshLayout.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
            markedMails = savedInstanceState.getBooleanArray("markedEmails");
        else
            markedMails = null;

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("markedEmails", mailAdapter.getMarkedMails());
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * This is done for maintaining the fragment lifecycle. Read onPostRefresh comment.
         **/
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getInt(Constants.BUNDLE_ON_POST_REFRESH_EMAILS_SIZE, -1) != -1) {
            onPostRefresh(bundle.getInt(Constants.BUNDLE_ON_POST_REFRESH_EMAILS_SIZE));
        }
    }

    private void registerInternalBroadcastReceivers() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshAdapter();
            }
        }, new IntentFilter(Constants.BROADCAST_REFRESH_ADAPTERS));
    }

    private void setupMailAdapter() {
        allEmails = new ArrayList<>();
        mailAdapter = new MailAdapter(allEmails, getActivity(), this, folder);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mailAdapter);
    }

    private void setupSearchBar() {
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RefreshInbox(currentUser, getActivity(), FolderFragment.this, folder, Constants.REFRESH_TYPE_REFRESH).execute();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.darker_gray,
                android.R.color.holo_blue_dark);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_folder_menu, menu);
        selectAll = menu.getItem(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
        }else if(id == R.id.action_selectall){
            if(item.isChecked()){
                item.setChecked(false);
                item.setIcon(R.drawable.ic_action_selectall_unchecked);
                selectAllMails(false);
                setupSelectAll(false);
            }
            else{
                item.setChecked(true);
                item.setIcon(R.drawable.ic_action_selectall_checked);
                selectAllMails(true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreRefresh() {
        progressDialog2 = ProgressDialog.show(getActivity(), "", getString(R.string.dialog_msg_loading), true);
        progressDialog2.setCancelable(false);
        progressDialog2.show();
    }

    @Override
    public void onPostRefresh(boolean success, final ArrayList<EmailMessage> refreshedEmails, User user) {

        allEmails = new ArrayList<>(refreshedEmails);
        /**
         * This is done for maintaining the fragment lifecycle.
         * Check if the fragment is attached to the activity
         *       if it isn't, then set bundle stating that a refresh is required.
         */
        if (getFragmentManager() != null) {
            FolderFragment thisFragment = (FolderFragment) getFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_FOLDER+folder);
            if (thisFragment != null) {
                if (!thisFragment.isAdded()) {
                    if (thisFragment != null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.BUNDLE_ON_POST_REFRESH_EMAILS_SIZE, refreshedEmails.size());
                        thisFragment.setArguments(bundle);
                    }
                } else {
                    onPostRefresh(refreshedEmails.size());
                }
            } else {
                refreshAdapter();
                progressDialog2.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            refreshAdapter();
            progressDialog2.dismiss();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void onPostRefresh(final int refreshedEmailsSize) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                refreshAdapter();
                if (refreshedEmailsSize == 0)
                    Snackbar.make(swipeRefreshLayout, getString(R.string.snackbar_new_webmail_zero), Snackbar.LENGTH_LONG).show();
                else if (refreshedEmailsSize == 1)
                    Snackbar.make(swipeRefreshLayout, getString(R.string.snackbar_new_webmail_one), Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(swipeRefreshLayout, refreshedEmailsSize + getString(R.string.snackbar_new_webmail_many), Snackbar.LENGTH_LONG).show();
                progressDialog2.dismiss();

                if (!allEmails.isEmpty()) {
                    emptyLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                } else {
                    emptyLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }

            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPreMultiMailAction() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.dialog_msg_attempting_action));
        progressDialog.show();
    }

    @Override
    public void onPostMultiMailAction(boolean success, String mailAction, ArrayList<EmailMessage> emailsForMultiAction) {
        if (!success)
            Snackbar.make(swipeRefreshLayout, getString(R.string.snackbar_delete_unsuccessful), Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(swipeRefreshLayout, getString(R.string.snackbar_delete_successful), Snackbar.LENGTH_LONG).show();

        progressDialog.dismiss();
        refreshAdapter();
        fabDelete.setVisibility(View.GONE);

        new RefreshInbox(currentUser, getActivity(), FolderFragment.this, folder, Constants.REFRESH_TYPE_REFRESH).execute();
    }

    public void refreshAdapter() {
        mailAdapter = new MailAdapter(allEmails, getActivity(), this, folder);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        restoreMarkedMails();
        recyclerView.setAdapter(mailAdapter);
    }

    public void logout() {
//        ((MainActivity) getActivity()).showLogoutDialog(currentUser);
    }

    @Override
    public void onItemClickedForDelete(final ArrayList<EmailMessage> emailsMarkedForAction) {

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(swipeRefreshLayout, getString(R.string.snackbar_deleting), Snackbar.LENGTH_LONG).show();
                new MultiMailAction(currentUser, getActivity(), FolderFragment.this, emailsMarkedForAction, getString(R.string.msg_action_delete)).execute();
            }
        });

        if (!emailsMarkedForAction.isEmpty()) {
            if (fabDelete.getVisibility() != View.VISIBLE) {
                fabDelete.setVisibility(View.VISIBLE);
                fabDelete.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom));
                setupSelectAll(true);
            }
        } else {
            if (fabDelete.getVisibility() != View.GONE) {
                fabDelete.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_bottom));
                fabDelete.setVisibility(View.GONE);
                setupSelectAll(false);
            }
        }
    }

    private void setupSelectAll(boolean set){
        if(selectAll != null){
            if(set){
                selectAll.setVisible(true);
                selectAll.setEnabled(true);
            }
            else{
                selectAll.setVisible(false);
                selectAll.setEnabled(false);
            }
        }
    }

    private void selectAllMails(boolean select){
        markedMails = new boolean[ allEmails.size() ];
        if(select){
            Arrays.fill(markedMails, true);
        }
        else{
            Arrays.fill(markedMails, false);
        }
        refreshAdapter();
    }

    private void restoreMarkedMails(){
        if(markedMails != null) {
            mailAdapter.restoreMarkedMails(markedMails);
            markedMails = null;
        }
    }
}
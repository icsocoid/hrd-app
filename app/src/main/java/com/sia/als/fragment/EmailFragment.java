package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.sia.als.R;
import com.sia.als.mail.MainMailActivity;
import com.sia.als.mail.asynTasks.Login;
import com.sia.als.mail.asynTasks.LoginListener;
import com.sia.als.mail.database.CurrentUser;
import com.sia.als.mail.database.User;
import com.sia.als.mail.fragment.InboxFragment;
import com.sia.als.mail.network.AnalyticsAPI;
import com.sia.als.mail.utils.Settings;
import com.sia.als.util.SessionManagement;

import java.util.ArrayList;

public class EmailFragment extends Fragment implements LoginListener {
    View view;
    EditText txtPassword;
    Button btn_login;
    private String enteredUsername = "testing@als.holdings";
    private String enteredPassword = "";
    private User currentUser;
    private ProgressDialog progressDialog;
    private Settings settings;
    SessionManagement sessionManagement;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        view = inflater.inflate(R.layout.login_email_fragment, container, false);
//        currentUser = CurrentUser.getCurrentUser(getTargetFragment().getContext());
        currentUser = CurrentUser.getCurrentUser(getContext());
        txtPassword = view.findViewById(R.id.login_password);
        settings = new Settings(getContext());
        btn_login = view.findViewById(R.id.btn_sign_in);

//        if (currentUser == null) {
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_login.setEnabled(true);
                    enteredPassword = txtPassword.getText().toString();

//                    if (!enteredUsername.contains("@" + getString(R.string.webmail_domain))) {
//                        enteredUsername = enteredUsername + "@" + getString(R.string.webmail_domain);
//                    }

                    if (User.doesUserExist(enteredUsername, enteredPassword)) {
                        Snackbar.make(view, getString(R.string.snackbar_login_user_exist), Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CurrentUser.setCurrentUser(User.getUserFromUserName(enteredUsername), getContext());
                                InboxFragment inboxFragment = new InboxFragment();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.m_frame, inboxFragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                            }
                        }, 1000);
                    } else {
//                        User user = new User(enteredUsername, enteredPassword);
//                        new Login(user, getContext(), EmailFragment.this).execute();
                        EmailFragment emailFragment = new EmailFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.m_frame, emailFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    }
                }
            });
//        } else {
//            InboxFragment inboxFragment = new InboxFragment();
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.m_frame, inboxFragment)
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .commit();
//        }

        return view;
    }

    @Override
    public void onPreLogin() {
        progressDialog = ProgressDialog.show(getContext(), "", getString(R.string.dialog_msg_logging_in), true);
        progressDialog.setCancelable(false);
        progressDialog.show();
//        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        mgr.hideSoftInputFromWindow(btn_login.getWindowToken(), 0);
        Snackbar.make(btn_login, getString(R.string.snackbar_login_attempting), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPostLogin(boolean loginSuccess, String timeTaken, User user) {
        progressDialog.dismiss();
        btn_login.setEnabled(true);
        if (!loginSuccess) {
            Snackbar.make(btn_login, getString(R.string.snackbar_login_failed), Snackbar.LENGTH_LONG).show();
//            usernameField.setText(enteredUsername);
            txtPassword.setText("");
        } else {
            AnalyticsAPI.sendLoginDataToFirebase(user);
            Snackbar.make(btn_login, getString(R.string.snackbar_login_successful), Snackbar.LENGTH_LONG).show();
            user = User.createNewUser(user);
            CurrentUser.setCurrentUser(user, getActivity().getApplicationContext());
            InboxFragment inboxFragment = new InboxFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.m_frame, inboxFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
//            usernameField.setText("");
            txtPassword.setText("");
        }
    }


}

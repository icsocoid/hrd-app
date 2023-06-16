package com.sia.als.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sia.als.MainActivity;
import com.sia.als.R;
import com.sia.als.adapter.NotifikasiPengajuanAbsensiAdapter;
import com.sia.als.config.Config;
import com.sia.als.fragment.AbsensiFragment;
import com.sia.als.fragment.AdminHomeFragment;
import com.sia.als.fragment.DetailNotifPengajuanAbsensiFragment;
import com.sia.als.fragment.HomeFragment;
import com.sia.als.fragment.IzinFragment;
import com.sia.als.fragment.NewProfileFragment;
import com.sia.als.fragment.NotifikasiPengajuanAbsensiFragment;
import com.sia.als.fragment.NotifyIzinFragment;
import com.sia.als.fragment.QrCodeFragment;
import com.sia.als.model.PengajuanAbsensi;
import com.sia.als.util.SessionManagement;

public class NotifyActivity extends AppCompatActivity {
    ImageView tabHome,tabHistory,tabAbsen,tabIzin,tabAccount;
    TextView txtHome,txtHistory,txtIzin,txtAkun;
    SessionManagement sessionManagement;

//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        NotifyIzinFragment profileFragment = new NotifyIzinFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.m_frame, profileFragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit();
//    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManagement = new SessionManagement(this);
        NotifikasiPengajuanAbsensiFragment profileFragment = new NotifikasiPengajuanAbsensiFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, profileFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void actionBottomBar()
    {
        tabHome = (ImageView) findViewById(R.id.tab_home);
        tabHistory = (ImageView) findViewById(R.id.tab_history);
        tabAbsen = (ImageView) findViewById(R.id.tab_absen);
        tabIzin = (ImageView) findViewById(R.id.tab_izin);
        tabAccount = (ImageView) findViewById(R.id.tab_account);

        tabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeFragment();
            }
        });

        tabHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showHistoryFragment();
            }
        });

        tabAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAbsenFragment();
            }
        });

        tabIzin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIzinFragment();
            }
        });

        tabAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountFragment();
            }
        });
    }

    private void resetTextColor()
    {


    }

    private void showNotifikasi(String message)
    {

    }

    private void showHomeFragment()
    {
        autoLogout();
        resetTextColor();
        // txtHome.setTextColor(getResources().getColor(R.color.sky_blue));
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, homeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showHistoryFragment(){
        autoLogout();
        resetTextColor();
        //txtHistory.setTextColor(getResources().getColor(R.color.sky_blue));
        AbsensiFragment absensiFragment = new AbsensiFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, absensiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showAbsenFragment(){
        autoLogout();
        QrCodeFragment qrCodeFragment = new QrCodeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, qrCodeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showIzinFragment(){
        autoLogout();
        resetTextColor();
        //txtIzin.setTextColor(getResources().getColor(R.color.sky_blue));
        IzinFragment izinFragment = new IzinFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, izinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showAccountFragment(){
        autoLogout();
        resetTextColor();
        //txtAkun.setTextColor(getResources().getColor(R.color.sky_blue));
        if(sessionManagement.isAdmin())
        {
            txtAkun.setText("Admin");
            AdminHomeFragment qrCodeFragment = new AdminHomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.m_frame, qrCodeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        else
        {
//            ProfileFragment qrCodeFragment = new ProfileFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.m_frame, qrCodeFragment)
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .commit();

            NewProfileFragment qrCodeFragment = new NewProfileFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.m_frame, qrCodeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    private void autoLogout()
    {
        String mac = sessionManagement.getUserDetails().get(Config.KEY_MAC);
        if(mac != null){
            if(mac.equals(""))
            {
                sessionManagement.logoutSession();
                Intent logout = new Intent();
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(logout);
            }
        }


    }


}

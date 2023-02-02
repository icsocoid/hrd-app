package com.sia.als;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.ads.mediationtestsuite.activities.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sia.als.activity.LoginActivity;
import com.sia.als.config.Config;
import com.sia.als.fragment.AbsensiFragment;
import com.sia.als.fragment.AdminHomeFragment;
import com.sia.als.fragment.DetailIzinFragment;
import com.sia.als.fragment.DetailNotifFragment;
import com.sia.als.fragment.DetailNotifPengajuanAbsensiFragment;
import com.sia.als.fragment.HomeFragment;
import com.sia.als.fragment.IzinFragment;
import com.sia.als.fragment.NewProfileFragment;
import com.sia.als.fragment.NotifikasiPengajuanAbsensiFragment;
import com.sia.als.fragment.ProfileFragment;
import com.sia.als.fragment.QrCodeFragment;
import com.sia.als.fragment.ReadQrCodeFragment;
import com.sia.als.util.ConnectivityReceiver;
import com.sia.als.util.NoInternetConnection;
import com.sia.als.util.SessionManagement;


public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private AppUpdateManager mAppUpdateManager;
    private static final int U_APP_UPDATE = 862;

   // public BottomBar bottomBar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ImageView tabHome,tabHistory,tabAbsen,tabIzin,tabAccount;
    TextView txtHome,txtHistory,txtIzin,txtAkun;
    int defaultTextColor;
    SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManagement = new SessionManagement(this);
        /*txtHome = (TextView) findViewById(R.id.text_home);
        txtHistory = (TextView) findViewById(R.id.text_history);
        txtIzin = (TextView) findViewById(R.id.text_izin);
        txtAkun = (TextView) findViewById(R.id.text_akun); */
      //  defaultTextColor = txtHistory.getTextColors().getDefaultColor();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    //displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");


                    //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    // txtMessage.setText(message);
                }
            }
        };

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String page = extras.getString("page");
            if(page.equals("detail_izin"))
            {
                Bundle bundle = new Bundle();
                bundle.putString("id_izin",extras.getString("id_izin"));
                DetailIzinFragment detailIzinFragment = new DetailIzinFragment();
                detailIzinFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.m_frame, detailIzinFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
            else if(page.equals("terlambat"))
            {
                Bundle bundle = new Bundle();
                bundle.putString("id_terlambat",extras.getString("id_terlambat"));
                NotifikasiPengajuanAbsensiFragment notifikasiPengajuanAbsensiFragment = new NotifikasiPengajuanAbsensiFragment();
                notifikasiPengajuanAbsensiFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.m_frame, notifikasiPengajuanAbsensiFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
            else if(page.equals("detail_notifikasi"))
            {
                Bundle bundle = new Bundle();
                bundle.putString("notifikasi_id",extras.getString("id_notifikasi"));
                DetailNotifFragment detailNotifFragment = new DetailNotifFragment();
                detailNotifFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.m_frame, detailNotifFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
            else if(page.equals("detail_pengajuan"))
            {
                Bundle bundle = new Bundle();
                bundle.putString("id_notif",extras.getString("id_notif"));
                DetailNotifPengajuanAbsensiFragment detailNotifPengajuanAbsensiFragment = new DetailNotifPengajuanAbsensiFragment();
                detailNotifPengajuanAbsensiFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.m_frame, detailNotifPengajuanAbsensiFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        }
        else
        {
            showHomeFragment();
        }

        actionBottomBar();

        AppUpdateManager mAppUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.updatePriority() >= 5
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, MainActivity.this
                            ,U_APP_UPDATE);
                }catch (IntentSender.SendIntentException e){
                    e.printStackTrace();
                }
            }
        });

        //mAppUpdateManager.registerListener(installStateUpdatedListener);

    }

    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener()
    {
        @Override
        public void onStateUpdate(@NonNull InstallState installState)
        {
            if (installState.installStatus() == InstallStatus.DOWNLOADED)
            {
                showCompletedUpdate();
            }
        }
    };

    @Override
    protected void onStop() {
        //if (mAppUpdateManager!=null) mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    private void showCompletedUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New app is ready!",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mAppUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == U_APP_UPDATE && resultCode != RESULT_OK){
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateManager mAppUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, MainActivity.this
                            ,U_APP_UPDATE);
                }catch (IntentSender.SendIntentException e){
                    e.printStackTrace();
                }
            }
        });
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
        ReadQrCodeFragment qrCodeFragment = new ReadQrCodeFragment();
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


    @Override

    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (!isConnected) {
            Intent intent = new Intent(MainActivity.this, NoInternetConnection.class);
            startActivity(intent);
        }
    }

    private void autoLogout()
    {
        String mac = sessionManagement.getUserDetails().get(Config.KEY_MAC);
        if(mac != null){
            if(mac.equals(""))
            {
                sessionManagement.logoutSession();
                Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(logout);
            }
        }


    }
}

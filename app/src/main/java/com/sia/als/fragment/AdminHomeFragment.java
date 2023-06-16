package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.util.CustomVolleyJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminHomeFragment extends Fragment {
    View view;
    LinearLayout karyawanBtn,newsBtn,absenBtn,kalenderBtn,requestBtn,accountBtn,officeBtn;
    TextView toolbarTitle,hadirTxt,sakitTxt,cutiTxt,izinTxt,alphaTxt,clockTxt,tanggalTxt;
    Button saveBtn;
    Handler timerHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_admin, container, false);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("ADMIN DASHBOARD");
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        saveBtn.setVisibility(View.GONE);
        clockTxt = (TextView) view.findViewById(R.id.clock_txt);
        tanggalTxt = (TextView) view.findViewById(R.id.tanggal_txt);
        karyawanBtn = (LinearLayout) view.findViewById(R.id.karyawan_btn);
        newsBtn = (LinearLayout) view.findViewById(R.id.news_btn);
        absenBtn = (LinearLayout) view.findViewById(R.id.absen_btn);
        kalenderBtn = (LinearLayout) view.findViewById(R.id.kalender_btn);
        requestBtn = (LinearLayout) view.findViewById(R.id.request_btn);
        accountBtn = (LinearLayout) view.findViewById(R.id.account_btn);
        officeBtn = (LinearLayout) view.findViewById(R.id.kantor_btn);
        hadirTxt = (TextView) view.findViewById(R.id.hadir_txt);
        sakitTxt = (TextView) view.findViewById(R.id.sakit_txt);
        cutiTxt = (TextView) view.findViewById(R.id.cuti_txt);
        alphaTxt = (TextView) view.findViewById(R.id.alpha_txt);
        izinTxt = (TextView) view.findViewById(R.id.ijin_txt);
        absenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRekapAbsensi();
            }
        });
        karyawanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKaryawan();
            }
        });
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIzin();
            }
        });
        officeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOffice();
            }
        });
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });
        kalenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKalender();
            }
        });
        timerHandler.postDelayed(timerRunnable,0);
        showDate();
        makeDataRequest();

        return view;
    }

    public void showRekapAbsensi()
    {
        RekapAbsensiFragment qrCodeFragment = new RekapAbsensiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, qrCodeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showKaryawan()
    {
        KaryawanFragment dataFragment = new KaryawanFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, dataFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showOffice()
    {
        OfficeFragment dataFragment = new OfficeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, dataFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showKalender()
    {
        CalenderFragment dataFragment = new CalenderFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, dataFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showIzin()
    {
        AdminIzinFragment dataFragment = new AdminIzinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, dataFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showProfile()
    {
        ProfileFragment qrCodeFragment = new ProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, qrCodeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void makeDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_home_admin_req";
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                Config.ADMIN_HOME_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                       hadirTxt.setText(response.getString("hadir"));
                       sakitTxt.setText(response.getString("sakit"));
                       cutiTxt.setText(response.getString("cuti"));
                       alphaTxt.setText(response.getString("alpha"));
                       izinTxt.setText(response.getString("izin"));

                    } else {
                        String error = response.getString("message");
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void showDate()
    {
        String pattern = "EEEE, dd MMMM yyyy";
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, new Locale("id","ID"));
        String date = simpleDateFormat.format(new Date());
        tanggalTxt.setText(date);
    }

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            String pattern = "HH:mm:ss";
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            clockTxt.setText(date);
            timerHandler.postDelayed(this, 1000);
        }
    };
}

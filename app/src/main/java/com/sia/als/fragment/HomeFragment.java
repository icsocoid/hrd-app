package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sia.als.AppController;
import com.sia.als.MainActivity;
import com.sia.als.R;
import com.sia.als.activity.LoginActivity;
import com.sia.als.adapter.HistoryAbsensiAdapter;
import com.sia.als.adapter.NotifikasiAdapter;
import com.sia.als.adapter.PengajuanAdapter;
import com.sia.als.adapter.RekapHomeAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Absensi;
import com.sia.als.model.Izin;
import com.sia.als.model.Pengajuan;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.EndlessRecyclerViewScrollListener;
import com.sia.als.util.SessionManagement;
import com.sia.als.util.Utility;

import cz.kinst.jakub.view.StatefulLayout;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private View view;
    ImageButton backButton;
    Button saveBtn, retryBtn, notif;
    RecyclerView rvData;
    RekapHomeAdapter rekapHomeAdapter;
    NotifikasiAdapter notifikasiAdapter;
    TextView toolbarTitle,clockTxt,waktuTxt,tanggalTxt,jamMasukTxt,jamPulangTxt,statusMasukTxt,statusPulangTxt, dateCuti;
    Handler timerHandler = new Handler();
    SessionManagement sessionManagement;
    StatefulLayout statefulLayout;
    //ImageView imageView;
    LinearLayout cutiLay;
    TextView cutiTxt,sisaCutiTxt,cutiBersamaTxt;
    SwipeRefreshLayout swipeRefreshLayout;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;
    LinearLayoutManager linearLayoutManager;
    List<Izin> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_home, container, false);
        backButton =  (ImageButton) getActivity().findViewById(R.id.button_back);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        dateCuti = (TextView) view.findViewById(R.id.data_cuti);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("HOME");
        backButton.setVisibility(View.GONE);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setBackgroundResource(R.drawable.ic_bell);
        notif.setWidth(24);
        notif.setHeight(24);
        notif.setText("");
        notif.setVisibility(View.VISIBLE);
        clockTxt = (TextView) view.findViewById(R.id.clock_txt);
        waktuTxt = (TextView) view.findViewById(R.id.waktu_txt);
        rvData = (RecyclerView) view.findViewById(R.id.rv_data_recap);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        tanggalTxt = (TextView) view.findViewById(R.id.tanggal_txt);
        jamMasukTxt = (TextView) view.findViewById(R.id.jam_masuk_txt);
        jamPulangTxt = (TextView) view.findViewById(R.id.jam_pulang_txt);
        statusMasukTxt = (TextView) view.findViewById(R.id.status_masuk_txt);
        statusPulangTxt = (TextView) view.findViewById(R.id.status_pulang_txt);
        cutiTxt = (TextView) view.findViewById(R.id.cuti_dipakai);
        sisaCutiTxt = (TextView) view.findViewById(R.id.sisa_cuti);
        cutiBersamaTxt = (TextView) view.findViewById(R.id.cuti_bersama);
        cutiLay = (LinearLayout) view.findViewById(R.id.lay_cuti);
        rvData.setLayoutManager(new LinearLayoutManager(getContext()));
        sessionManagement = new SessionManagement(getContext());
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));

        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        retryBtn = (Button) errorView.findViewById(R.id.button_retry);
        statefulLayout.setStateView(Config.STATE_ERROR, errorView);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetData();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                resetData();
            }
        });
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotifFragment();
                makeDataRequest();
            }
        });
        resetData();
        //waktuTxt.setOnClickListener();
        //waktu();
        tahun();

        timerRunnable.run();
        showDate();

        return view;
    }

    private void resetData()
    {
        rekapHomeAdapter = null;
        data = new ArrayList<>();
        makeDataRequest();
    }

    private void waktu() {

    }

    private void showNotifFragment() {
        NotifikasiFragment notifikasiFragment = new NotifikasiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, notifikasiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    // url post http://hrd.als.today/hrm/hrmapi/absensi_hari_ini
    public void makeDataRequest() {
        /*final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();*/

        statefulLayout.setState(Config.STATE_PROGRESS);

        String tag_json_obj = "json_data_rekap_izin_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.HOME, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                try {

                    statefulLayout.setState(Config.STATE_EMPTY);

                    Boolean status = response.getBoolean("status");
                    if (status) {
                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        String jamMasuk = response.getString("jam_masuk");
                        String jamPulang = response.getString("jam_pulang");
                        String statusMasuk = response.getString("status_masuk");
                        String statusPulang = response.getString("status_pulang");

                        jamMasukTxt.setText(jamMasuk);
                        jamPulangTxt.setText(jamPulang);
                        statusMasukTxt.setText(statusMasuk);
                        statusPulangTxt.setText(statusPulang);
                        String cuti = response.getString("cuti");
                        if(cuti.equals("0"))
                        {
                            cutiLay.setVisibility(View.GONE);
                        }
                        else
                        {
                            JSONObject objCuti = response.getJSONObject("cuti");
                            cutiTxt.setText(objCuti.getString("jumlah_cuti"));
                            sisaCutiTxt.setText(objCuti.getString("sisa"));
                            cutiBersamaTxt.setText(objCuti.getString("cuti_bersama"));
                        }
                        //List<Izin> data = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("izin");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Izin izin = new Izin();
                                izin.setId(json_data.getString("id_izin"));
                                izin.setNamaIzin(json_data.getString("nama_izin"));
                                izin.setJumlah(json_data.getInt("jumlah_izin"));
                                data.add(izin);

                            }
                            if(rekapHomeAdapter == null)
                            {
                                // isFirst = false;
                                isFirst = false;
                                rekapHomeAdapter = new RekapHomeAdapter(getContext(),data);
                                rekapHomeAdapter.setOnItemClickListener(new RekapHomeAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, Izin obj, int position) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id_izin",String.valueOf(obj.getId()));
                                        Log.i("homeIzin", String.valueOf(obj.getId()));
                                        RekapIzinFragment rekapIzinFragment = new RekapIzinFragment();
                                        rekapIzinFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, rekapIzinFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });



//                                Log.i("data_rekap", String.valueOf(data.size()));
//                                rekapHomeAdapter = new RekapHomeAdapter(getContext(),data);
                                rvData.setAdapter(rekapHomeAdapter);
//                                rvData.refreshDrawableState();
                                rvData.smoothScrollToPosition(0);
//                                rvData.setVisibility(View.VISIBLE);

                            }
                            else
                            {
                                rekapHomeAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;


                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }

                    }
                    else {
                        //state empty
                        //rvData.setVisibility(View.GONE);
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }

                } catch (JSONException e) {
                    //state error
                    e.printStackTrace();

                    //statefulLayout.setState(Config.STATE_ERROR);
                    retryBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            makeDataRequest();
                        }
                    });
                    //dialog.hide();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // dialog.hide();
                //state internet no connection
                swipeRefreshLayout.setRefreshing(false);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    statefulLayout.setState(Config.STATE_NO_CONNECTION);
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }

            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    public void tahun(){
        String pattern = ("yyyy");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("id","ID"));
        String date = simpleDateFormat.format(new Date());
        dateCuti.setText(date);
        String month = Utility.sdfMonth.format(new Date());
        waktuTxt.setText(Utility.convertBulanIndo(month)+" "+date);
    }

    private void autoLogout(String firebaseId)
    {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        String token = pref.getString("regId", null);
        Log.i("token_database", token);
        Log.i("firebase_token", firebaseId);
        if(!token.equals(firebaseId)) {
            sessionManagement.logoutSession();
            editor.clear();
            editor.commit();
            Intent logout = new Intent(getActivity(), LoginActivity.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(logout);
        }

    }

}
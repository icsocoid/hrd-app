package com.sia.als.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.Group;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.MainActivity;
import com.sia.als.R;
import com.sia.als.activity.KodePerusahaanActivity;
import com.sia.als.activity.LoginActivity;
import com.sia.als.adapter.HistoryAbsensiAdapter;
import com.sia.als.adapter.PengajuanAdapter;
import com.sia.als.adapter.PtkpAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Absensi;
import com.sia.als.model.Pengajuan;
import com.sia.als.model.Ptkp;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.EndlessRecyclerViewScrollListener;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cz.kinst.jakub.view.StatefulLayout;

public class AbsensiFragment extends Fragment {
    private View view;
    ImageButton backButton, close;
    ImageView imageView;
    Button saveBtn,btFilter, notif;
    SessionManagement sessionManagement;
    TextView toolbarTitle, today1, date;
    RecyclerView rvAbsensi,rvTerlambat;
    Group emptyGroup;
    HistoryAbsensiAdapter historyAbsensiAdapter;
    StatefulLayout statefulLayout;
    Button retryBtn;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    LinearLayout calender1, calender2,filterDialogBtn, masuk,pulang;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;
    List<Absensi> data = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    private int mMonth, mYear, mDay;
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
    private String dariTanggal;
    private String sampaiTanggal;
    DetailNotifPengajuanAbsensiFragment detailNotifPengajuanAbsensiFragment;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_rekap_absensi, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setVisibility(View.VISIBLE);
        notif.setText("");
        notif.setWidth(24);
        notif.setHeight(24);
        notif.setBackgroundResource(R.drawable.ic_order_white);
//        notif.setBackgroundResource(R.drawable.ic_notif);
        setDariTanggal("");
        setSampaiTanggal("");
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Histori Absensi");
        backButton.setVisibility(View.GONE);
        imageView = (ImageView) view.findViewById(R.id.img_choose_date);
        rvAbsensi = (RecyclerView) view.findViewById(R.id.absensi_list_recycler);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        filterDialogBtn = (LinearLayout) view.findViewById(R.id.layoutJournalChooseDate);
        masuk = (LinearLayout) view.findViewById(R.id.masuk);
        pulang = (LinearLayout) view.findViewById(R.id.pulang);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvAbsensi.setLayoutManager(linearLayoutManager);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nested_content);
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
                makeDataRequest(0);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
              resetData();
              initScrollListener();
            }

        });
        //fungsi munculkan dialog
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();

            }
        });

        filterDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

//        notif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                showNotifikasiPengajuanAbsensiFragment();
//                String user_id = "0";
//                String tag_json_obj = "json_profile_req";
//                Map<String, String> params = new HashMap<String, String>();
//                user_id = sessionManagement.getUserDetails().get(Config.KEY_ID);
//
//                params.put("user_id", user_id);
//                CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
//                        Config.HEAD_TEAM, params, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        try {
//                            Boolean status = response.getBoolean("status");
//                            if (status) {
//                                showNotifikasiPengajuanAbsensiFragment();
//                            }else{
//                                showPengajuanAbsensiFragment();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
////                        Log.i("info user",error.getMessage());
//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//            }
//        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistoryTerlambatFragment();
            }
        });

        initScrollListener();
        return view;
    }

    private void showHistoryTerlambatFragment() {
        HistoryTerlambat historyTerlambat = new HistoryTerlambat();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, historyTerlambat)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
    private void showPengajuanAbsensiFragment() {
        PengajuanAbsensiFragment pengajuanAbsensiFragment = new PengajuanAbsensiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, pengajuanAbsensiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showNotifikasiPengajuanAbsensiFragment() {
        NotifikasiPengajuanAbsensiFragment notifikasiPengajuanAbsensiFragment = new NotifikasiPengajuanAbsensiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, notifikasiPengajuanAbsensiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


    private void showFilterDialog()
    {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_filter);

        close = dialog.findViewById(R.id.bt_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btFilter = (Button) dialog.findViewById(R.id.bt_filter);
        today1 = dialog.findViewById(R.id.today1);
        today1.setText(simpleDateFormat.format(new Date()));
        calender1 =  dialog.findViewById(R.id.calender1);
        calender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // today1.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                int month= monthOfYear+1;
                                String fm=""+month;
                                String fd=""+dayOfMonth;
                                if(month<10){
                                    fm ="0"+month;
                                }
                                if (dayOfMonth<10){
                                    fd="0"+dayOfMonth;
                                }
                                String drTgl = year+ "-" + fm + "-" + fd;
                                today1.setText( drTgl);
                                setDariTanggal(drTgl);

                            }
                        }, mYear, mMonth, mDay);
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });

        /*calender open onclick*/
        calender2 =  dialog.findViewById(R.id.calender2);
        date = dialog.findViewById(R.id.date);
        date.setText(simpleDateFormat.format(new Date()));
        calender2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                int month= monthOfYear+1;
                                String fm=""+month;
                                String fd=""+dayOfMonth;
                                if(month<10){
                                    fm ="0"+month;
                                }
                                if (dayOfMonth<10){
                                    fd="0"+dayOfMonth;
                                }
                                String spTgl = year+ "-" + fm + "-" + fd;
                                date.setText(spTgl);
                                setSampaiTanggal(spTgl);

                            }
                        }, mYear, mMonth, mDay);
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });
        btFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetData();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setDariTanggal(String dariTanggal)
    {
        this.dariTanggal = dariTanggal;
    }
    
    public String getDariTanggal()
    {
        return dariTanggal;
    }
    
    public void setSampaiTanggal(String sampaiTanggal)
    {
        this.sampaiTanggal = sampaiTanggal;
    }
    
    public String getSampaiTanggal()
    {
        return sampaiTanggal;
    }

    //init buat endless scroll
    private void initScrollListener()
    {
        rvAbsensi.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int hal, int totalItemsCount) {
                if(!isLoading)
                {
                    page = page + limit;
                    makeDataRequest(page);
                }

            }
        });
    }

    private void resetData()
    {
        isFirst = true;
        page = 0;
        data = new ArrayList<>();
        historyAbsensiAdapter = null;
        makeDataRequest(page);
    }

    public void makeDataRequest(int halaman) {
        if(isFirst)
        {
            statefulLayout.setState(Config.STATE_PROGRESS);
        }
        String tag_json_obj = "json_data_history_absensi_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        params.put("halaman", String.valueOf(halaman));
        params.put("limit", String.valueOf(limit));
        params.put("dari_tanggal", getDariTanggal());
        params.put("sampai_tanggal", getSampaiTanggal());
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.HISTORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    if(isFirst)
                    {
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }

                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject karyawan = response.getJSONObject("karyawan");
                        int statusKaryawan = karyawan.getInt("status_employee");
                        if (statusKaryawan == 1)
                        {
                            statefulLayout.setState(StatefulLayout.State.CONTENT);
                            try {
                                JSONArray Jarray = response.getJSONArray("absensi");
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject json_data = Jarray.getJSONObject(i);
                                    Absensi absensi = new Absensi();
                                    absensi.setId(json_data.getString("id"));
                                    absensi.setJam_masuk(json_data.getString("jam_masuk"));
                                    absensi.setJam_pulang(json_data.getString("jam_pulang"));
                                    absensi.setStatus_masuk(json_data.getString("status_masuk"));
                                    absensi.setStatus_pulang(json_data.getString("status_pulang"));
                                    absensi.setTanggal(json_data.getString("tanggal"));
                                    data.add(absensi);
                                }
                                if(historyAbsensiAdapter == null)
                                {
                                    isFirst = false;
                                    historyAbsensiAdapter = new HistoryAbsensiAdapter(getContext(),data);
                                    historyAbsensiAdapter.setOnItemClickListener(new HistoryAbsensiAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, Absensi obj, int position) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("absen_id",obj.getId());
                                            DetailAbsensiFragment detailAbsensiFragment = new DetailAbsensiFragment();
                                            detailAbsensiFragment.setArguments(bundle);
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            fragmentManager.beginTransaction()
                                                    .replace(R.id.m_frame, detailAbsensiFragment)
                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                    .commit();
                                        }
                                    });

                                    rvAbsensi.setAdapter(historyAbsensiAdapter);
                                    //rvAbsensi.refreshDrawableState();
                                    //swipeRefreshLayout.setRefreshing(false);
                                    rvAbsensi.smoothScrollToPosition(0);

                                }
                                else
                                {
                                    historyAbsensiAdapter.notifyDataSetChanged();
                                }
                                isLoading = false;
                                //getData(page,limit);

                            } catch (JSONException e) {
                                String error = response.getString("message");
                                TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();
                            }
                        }
                        else
                        {
                            sessionManagement.logoutSession();
                            Intent logout = new Intent(getActivity(), KodePerusahaanActivity.class);
                            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            // Add new Flag to start new Activity
                            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(logout);
                        }

                    }
                    else
                    {
//                        notif.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                showPengajuanAbsensiFragment();
//                            }
//                        });
                        if(isFirst)
                        {
                            statefulLayout.setState(Config.STATE_EMPTY);
                        }
                        else
                        {
                            isLoading = true;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    statefulLayout.setState(Config.STATE_ERROR);
                    retryBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            makeDataRequest(0);
                        }
                    });

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //dialog.hide();
                swipeRefreshLayout.setRefreshing(false);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    statefulLayout.setState(Config.STATE_NO_CONNECTION);
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    @Override
    public void onResume() {
        super.onResume();
        resetData();
    }

}

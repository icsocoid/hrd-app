package com.sia.als.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.activity.LoginActivity;
import com.sia.als.adapter.HistoryAbsensiAdapter;
import com.sia.als.adapter.IzinAdapter;
import com.sia.als.adapter.NotifikasiAdapter;
import com.sia.als.adapter.PengajuanAdapter;
import com.sia.als.adapter.RekapHomeAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Absensi;
import com.sia.als.model.Izin;
import com.sia.als.model.Notifikasi;
import com.sia.als.model.Pengajuan;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.EndlessRecyclerViewScrollListener;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.kinst.jakub.view.StatefulLayout;

public class NotifikasiFragment extends Fragment {
    private View view;
    ImageButton backButton;
    SessionManagement sessionManagement;
    TextView toolbarTitle;
    RecyclerView rvNotif;
    NotifikasiAdapter notifikasiAdapter;
    Button notif;
    StatefulLayout statefulLayout;
    Button retryBtn;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;
    List<Notifikasi> data = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_development, container, false);
//        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
//        notif = (Button) getActivity().findViewById(R.id.button_general);
//        notif.setVisibility(View.GONE);
//        notif.setBackgroundResource(R.drawable.ic_notif);
//        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
//        toolbarTitle.setText("NOTIFIKASI");
//        backButton.setVisibility(View.GONE);
//        rvNotif = (RecyclerView) view.findViewById(R.id.list_notif);
//        rvNotif.setLayoutManager(new LinearLayoutManager(getContext()));
//        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
//        linearLayoutManager = new LinearLayoutManager(getContext());
//        rvNotif.setLayoutManager(linearLayoutManager);
//        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nested_content);
//        sessionManagement = new SessionManagement(getContext());
//        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
//        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
//        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));
//        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
//        retryBtn = (Button) errorView.findViewById(R.id.button_retry);
//        statefulLayout.setStateView(Config.STATE_ERROR, errorView);
//        retryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                makeDataRequest(0);
//            }
//        });
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh(){
//                resetData();
//            }
//        });
//        //sessionManagement = new SessionManagement(getContext());
//
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showFragment();
//            }
//        });
//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                showFragment();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
//
//        initScrollListener();
        return view;

    }

//    private void showFragment() {
//        HomeFragment homeFragment = new HomeFragment();
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.m_frame, homeFragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit();
//    }
//
//    private void initScrollListener()
//    {
//        rvNotif.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int hal, int totalItemsCount) {
//                if(!isLoading)
//                {
//                    page = page + limit;
//                    makeDataRequest(page);
//                }
//
//            }
//        });
//    }
//
//    private void resetData()
//    {
//        isFirst = true;
//        page = 0;
//        data = new ArrayList<>();
//        notifikasiAdapter = null;
//        makeDataRequest(page);
//    }
//
//    public void makeDataRequest(int halaman){
//        if(isFirst)
//        {
//            statefulLayout.setState(Config.STATE_PROGRESS);
//        }
//        String tag_json_obj = "json_data_notifikasi_req";
//        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("user_id", userId);
//        params.put("halaman", String.valueOf(halaman));
//        params.put("limit", String.valueOf(limit));
//        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
//                Config.NOTIFICATION_URL, params, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                swipeRefreshLayout.setRefreshing(false);
//                try {
//                    if(isFirst)
//                    {
//                        statefulLayout.setState(Config.STATE_EMPTY);
//                    }
//
//                    Boolean status = response.getBoolean("status");
//                    if (status) {
//                        JSONObject karyawan = response.getJSONObject("karyawan");
//                        int statusKaryawan = karyawan.getInt("status_employee");
//                        if (statusKaryawan == 1)
//                        {
//                            statefulLayout.setState(StatefulLayout.State.CONTENT);
//                            try {
//                                JSONArray Jarray = response.getJSONArray("data");
//                                for (int i = 0; i < Jarray.length(); i++) {
//                                    JSONObject json_data = Jarray.getJSONObject(i);
//                                    Notifikasi notifikasi = new Notifikasi();
//                                    notifikasi.setId(json_data.getString("id"));
//                                    notifikasi.setUserId(json_data.getString("user_id"));
//                                    notifikasi.setStatusNotif(json_data.getString("status_notif"));
//                                    notifikasi.setSubject(json_data.getString("subject"));
//                                    notifikasi.setMessage(json_data.getString("message"));
//                                    notifikasi.setContent(json_data.getString("content"));
//                                    //notifikasi.setPhoto(json_data.getString("photo_notif"));
//                                    notifikasi.setTanggal(json_data.getString("tanggal"));
//                                    data.add(notifikasi);
//                                }
//                                if(notifikasiAdapter == null)
//                                {
//                                    isFirst = false;
//                                    notifikasiAdapter = new NotifikasiAdapter(getContext(),data);
//                                    notifikasiAdapter.setOnItemClickListener(new NotifikasiAdapter.OnItemClickListener(){
//                                        @Override
//                                        public void onItemClick(View view, Notifikasi obj, int position) {
//                                            Bundle bundle = new Bundle();
//                                            bundle.putString("notifikasi_id",String.valueOf(obj.getId()));
//                                            DetailNotifFragment detailNotifFragment = new DetailNotifFragment();
//                                            detailNotifFragment.setArguments(bundle);
//                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                            fragmentManager.beginTransaction()
//                                                    .replace(R.id.m_frame, detailNotifFragment)
//                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                                    .commit();
//                                        }
//                                    });
//                                    rvNotif.setAdapter(notifikasiAdapter);
//                                    //rvAbsensi.refreshDrawableState();
//                                    //swipeRefreshLayout.setRefreshing(false);
//                                    rvNotif.smoothScrollToPosition(0);
//
//                                }
//                                else
//                                {
//                                    notifikasiAdapter.notifyDataSetChanged();
//                                }
//                                isLoading = false;
//                                //getData(page,limit);
//
//                            } catch (JSONException e) {
//                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                        else
//                        {
//                            sessionManagement.logoutSession();
//                            Intent logout = new Intent(getActivity(), LoginActivity.class);
//                            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            // Add new Flag to start new Activity
//                            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(logout);
//                        }
//
//                    }
//                    else
//                    {
//                        if(isFirst)
//                        {
//                            statefulLayout.setState(Config.STATE_EMPTY);
//                        }
//                        else
//                        {
//                            isLoading = true;
//                        }
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    //dialog.hide();
//                    statefulLayout.setState(Config.STATE_ERROR);
//                    retryBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            makeDataRequest(0);
//                        }
//                    });
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //dialog.hide();
//                swipeRefreshLayout.setRefreshing(false);
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    statefulLayout.setState(Config.STATE_NO_CONNECTION);
//                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        resetData();
//    }


}

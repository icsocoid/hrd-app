package com.sia.als.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.PerdinAdapter;
import com.sia.als.adapter.SlipGajiAdapter;
import com.sia.als.adapter.TaskAdapter;
import com.sia.als.config.Config;
import com.sia.als.mail.asynTasks.Login;
import com.sia.als.model.Pengajuan;
import com.sia.als.model.Perdin;
import com.sia.als.model.SlipGaji;
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

import cz.kinst.jakub.view.StatefulLayout;

public class SlipGajiFragment extends Fragment {

    private View view;
    SessionManagement sessionManagement;
    TextView toolbarTitle;
    ImageView imageView;
    RecyclerView rvSlip;
    SlipGajiAdapter slipGajiAdapter;
    Button retryBtn, notif;

    StatefulLayout statefulLayout;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;
    List<SlipGaji> data = new ArrayList<>();



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_slip_gaji, container, false);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        sessionManagement = new SessionManagement(getContext());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        imageView = (ImageView) view.findViewById(R.id.img_choose_date);
        rvSlip = (RecyclerView) view.findViewById(R.id.slip_list_recycle);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        linearLayoutManager = new LinearLayoutManager(getContext());
        retryBtn = (Button) errorView.findViewById(R.id.button_retry);
        notif = (Button) getActivity().findViewById(R.id.button_general);

        toolbarTitle.setText("Slip Gaji");
        notif.setVisibility(View.GONE);
        rvSlip.setLayoutManager(linearLayoutManager);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));
        retryBtn = (Button) errorView.findViewById(R.id.button_retry);

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDataRequest(0);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetData();
                initScrollListener();
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return view;
    }

    private void showFragment() {
        NewProfileFragment newProfileFragment = new NewProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, newProfileFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void initScrollListener() {
        rvSlip.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int hal, int totalItemsCount) {
                if (!isLoading)
                {
                    page = page + limit;
                    makeDataRequest(page);
                }
            }
        });
    }

    private void resetData() {
        isFirst = true;
        page = 0;
        data = new ArrayList<>();
        slipGajiAdapter = null;
        makeDataRequest(page);
    }

    private void makeDataRequest(int halaman) {
        if (isFirst)
        {
            statefulLayout.setState(Config.STATE_PROGRESS);
        }
        String tag_json_obj = "json_data_history_slip_gaji_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        params.put("halaman", String.valueOf(halaman));
        params.put("limit", String.valueOf(limit));
        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.SLIP_GAJI_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    if(isFirst)
                    {
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }
                    Boolean status = response.getBoolean("success");
                    if (status) {
                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        try {
                            JSONArray Jarray = response.getJSONArray("payroll");
                            for (int i = 0; i < Jarray.length(); i++){
                                JSONObject jsonObject = Jarray.getJSONObject(i);
                                SlipGaji slipGaji = new SlipGaji();
                                slipGaji.setId(jsonObject.getString("id"));
                                slipGaji.setPeriode(jsonObject.getString("nama_periode"));
                                slipGaji.setRangeperiode(jsonObject.getString("range_periode"));
                                data.add(slipGaji);
                            }
                            if (slipGajiAdapter == null)
                            {
                                isFirst = false;
                                slipGajiAdapter = new SlipGajiAdapter(getContext(), data);
                                slipGajiAdapter.setOnDetailClickListener(new SlipGajiAdapter.OnDetailClickListener() {
                                    @Override
                                    public void onItemClick(View view, SlipGaji obj, int position) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("slip_id", obj.getId());
                                        DetailSlipGajiFragment detailSlipGajiFragment = new DetailSlipGajiFragment();
                                        detailSlipGajiFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, detailSlipGajiFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });

                                rvSlip.setAdapter(slipGajiAdapter);
                                rvSlip.smoothScrollToPosition(0);
                            }
                            else{
                                slipGajiAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;

                        } catch (JSONException e) {
                            String error = response.getString("message");
                            TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
                        }
                    }
                    else {
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
                swipeRefreshLayout.setRefreshing(false);
                //dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    statefulLayout.setState(Config.STATE_NO_CONNECTION);
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }
    @Override
    public void onResume(){
        super.onResume();
        resetData();
    }

}


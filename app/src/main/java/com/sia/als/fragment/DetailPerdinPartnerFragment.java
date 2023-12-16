package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.sia.als.R;
import com.sia.als.adapter.PerdinPartnerDetailAdapter;
import com.sia.als.config.Config;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.model.PerdinPartner;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.EndlessRecyclerViewScrollListener;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.kinst.jakub.view.StatefulLayout;

public class DetailPerdinPartnerFragment extends Fragment {
    private View view;
    SessionManagement sessionManagement;
    TextView toolbarTitle, noPerdinTxt, namaKlienTxt, tglAwalTxt, tglAkhirTxt, totPerdinTxt;
    Button retryBtn, notif, approveBtn;
    ImageButton backButton;
    SwipeRefreshLayout swipeRefreshLayout;
    List<PerdinPartner> data = new ArrayList<>();
    PerdinPartnerDetailAdapter perdinPartnerDetailAdapter;
    RecyclerView rvPerdinPartnerDetail;
    StatefulLayout statefulLayout;
    LinearLayoutManager linearLayoutManager;
    LinearLayout totalLay, buttonLay;
    boolean isLoading = false;
    boolean isFirst = true;
    Locale localeID;
    NumberFormat numberFormat;
    String id;
    String oldFormat = "yyyy-MM-dd";
    String patternDate= "dd MMMM yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perdin_partner_detail, container, false);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        noPerdinTxt = (TextView) view.findViewById(R.id.nomor_perdin_txt);
        namaKlienTxt = (TextView) view.findViewById(R.id.nama_klien_txt);
        tglAwalTxt = (TextView) view.findViewById(R.id.tanggal_awal_txt);
        tglAkhirTxt = (TextView) view.findViewById(R.id.tanggal_akhir_txt);
        totPerdinTxt = (TextView) view.findViewById(R.id.total_perdin_txt);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        approveBtn = (Button) view.findViewById(R.id.approve_btn);
        retryBtn = (Button) errorView.findViewById(R.id.button_retry);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        rvPerdinPartnerDetail = (RecyclerView) view.findViewById(R.id.perdin_partner_detail_list_recycler);
        linearLayoutManager = new LinearLayoutManager(getContext());
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        localeID = new Locale("in", "ID");
        numberFormat = NumberFormat.getNumberInstance(localeID);
        totalLay = (LinearLayout) view.findViewById(R.id.total_lay);
        buttonLay = (LinearLayout) view.findViewById(R.id.button_lay);

        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDataSubmit(id);
            }
        });



        toolbarTitle.setText("Perjalanan Dinas Partner");
        notif.setVisibility(View.VISIBLE);
        rvPerdinPartnerDetail.setLayoutManager(linearLayoutManager);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDataRequest(id);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetData();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        if(getArguments() != null)
        {
            id = getArguments().getString("perdin_id");
            makeDataRequest(id);
        }
        return view;
    }

    private void makeDataSubmit(String id) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_perdin_partner_submit";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.APPROVE_PERDIN_PARTNER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status){
                        SuksesDialog suksesDialog = new SuksesDialog(DetailPerdinPartnerFragment.this);
                        suksesDialog.show(getFragmentManager(),"");
                    }else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_LONG,TastyToast.CONFUSING).show();
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                    TastyToast.makeText(getActivity(), "" + e, Toast.LENGTH_LONG,TastyToast.CONFUSING).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    private void showFragment() {
        PerdinPartnerFragment perdinPartnerFragment = new PerdinPartnerFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, perdinPartnerFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void resetData() {
        isFirst = true;
        data = new ArrayList<>();

    }

    private void makeDataRequest(String id) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_perdin_detail_partner_req";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        Log.i("makeData:paramsId", params.toString());
        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_PERDIN_PARTNER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject obj = response.getJSONObject("perdin");
                        String nomorPerdin = obj.getString("no_transaksi");
                        String namaKlien = obj.getString("nama_klien");
                        String tglAwal = obj.getString("tanggal_awal");
                        String tglAkhir = obj.getString("tanggal_akhir");
                        String totalPerdin = obj.getString("total_perdin");
                        Integer statusPerdin = obj.getInt("status_perdin");
                        if (statusPerdin != 1){
                            approveBtn.setVisibility(View.GONE);
                        }

                        try {
                            Date d = sdf.parse(tglAwal);
                            sdf.applyPattern(patternDate);
                            tglAwal = sdf.format(d);
                        }
                        catch (ParseException p) {}

                        try {
                            Date date = sdf.parse(tglAkhir);
                            sdf.applyPattern(patternDate);
                            tglAkhir = sdf.format(date);
                        }
                        catch (ParseException p) {}

                        noPerdinTxt.setText(nomorPerdin);
                        namaKlienTxt.setText(namaKlien);
                        tglAwalTxt.setText(tglAwal);
                        tglAkhirTxt.setText(tglAkhir);

                        if (totalPerdin.equals("0")){
                            totalLay.setVisibility(View.GONE);
                        }
                        totPerdinTxt.setText(numberFormat.format(Double.parseDouble(totalPerdin)));

                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        try {
                            JSONArray jArray =obj.getJSONArray("employee");
                            for (int i = 0; i < jArray.length(); i++){
                                JSONObject jObject = jArray.getJSONObject(i);
                                PerdinPartner perdinPartner = new PerdinPartner();
                                perdinPartner.setNamaKaryawan(jObject.getString("namaKaryawan"));
                                perdinPartner.setTotalPerdin(jObject.getString("nominal_perdin"));
                                perdinPartner.setTotalBonSementara(jObject.getString("nominal_bs"));
                                data.add(perdinPartner);
                            }
                            if (perdinPartnerDetailAdapter == null){
                                isFirst = false;
                                perdinPartnerDetailAdapter = new PerdinPartnerDetailAdapter(getContext(), data);
                                rvPerdinPartnerDetail.setAdapter(perdinPartnerDetailAdapter);
                            }else {
                                perdinPartnerDetailAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }catch (JSONException e ){
                            String error = response.getString("message");
                            TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
                        }
                    }else {
                        String error = response.getString("message");
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
                        if(isFirst)
                        {
                            statefulLayout.setState(Config.STATE_EMPTY);
                        }
                        else
                        {
                            isLoading = true;
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    statefulLayout.setState(Config.STATE_ERROR);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    public void showTask() {
        PerdinPartnerFragment perdinPartnerFragment = new PerdinPartnerFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, perdinPartnerFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}

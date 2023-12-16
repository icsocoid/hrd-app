package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.PerdinDetailAdapter;
import com.sia.als.adapter.TaskDetailAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Perdin;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
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

public class DetailPerdinFragment extends Fragment {
    private View view;
    ImageButton backButton;
    TextView nomorPerdinTxt, namaKlienTxt, tanggalTxt, noteTxt, nominalPerdinTxt, nominalBsTxt;
    Bitmap bitmap;
    String oldFormat = "yyyy-MM-dd";
    String patternIzin = "dd MMMM yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
    String id;
    SessionManagement sessionManagement;
    List<Perdin> data = new ArrayList<>();
    RecyclerView rvPerdinDetail;
    PerdinDetailAdapter perdinDetailAdapter;
    StatefulLayout statefulLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    Locale localeID;
    NumberFormat numberFormat;

    boolean isLoading = false;
    boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perdin_detail, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        backButton.setVisibility(View.GONE);
        nomorPerdinTxt = (TextView) view.findViewById(R.id.nomor_perdin_txt);
        namaKlienTxt = (TextView) view.findViewById(R.id.nama_klien_txt);
        tanggalTxt = (TextView) view.findViewById(R.id.tanggal_perdin_txt);
        noteTxt = (TextView) view.findViewById(R.id.catatan_txt);
        nominalPerdinTxt = (TextView) view.findViewById(R.id.nominal_perdin_txt);
        nominalBsTxt = (TextView) view.findViewById(R.id.nominal_bs_txt);
        sessionManagement = new SessionManagement(getContext());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        rvPerdinDetail = (RecyclerView) view.findViewById(R.id.perdin_detail_list_recycler);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        linearLayoutManager = new LinearLayoutManager(getContext());
        localeID = new Locale("in", "ID");
        numberFormat = NumberFormat.getNumberInstance(localeID);

        rvPerdinDetail.setLayoutManager(linearLayoutManager);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetData();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment();
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

    private void resetData() {
        isFirst = true;
        data = new ArrayList<>();
        perdinDetailAdapter = null;
        makeDataRequest(id);
    }

    private void showFragment() {
        PerdinFragment perdinFragment = new PerdinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, perdinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void makeDataRequest(String id) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        String tag_json_obj = "json_data_perdin_req";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("user_id", userId);
        Log.i("info-info", params.toString());

        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_PERDIN_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("success");
                    if (status){
                        JSONObject obj = response.getJSONObject("perdin");
                        JSONObject detailObj = obj.getJSONObject("detail");
                        String namaKlien = obj.getString("nama_klien");
                        String nomorPerdin = obj.getString("no_transaksi");
                        String tanggalPerdin = obj.getString("tanggal");
                        String nominalPerdin = detailObj.getString("nominal_perdin");
                        String nominalBs = detailObj.getString("nominal_bs");

                        try {
                            Date d = sdf.parse(tanggalPerdin);
                            sdf.applyPattern(patternIzin);
                            tanggalPerdin = sdf.format(d);
                        }
                        catch (ParseException p) {}

                        namaKlienTxt.setText(namaKlien);
                        nomorPerdinTxt.setText(nomorPerdin);
                        tanggalTxt.setText(tanggalPerdin);
                        nominalPerdinTxt.setText(numberFormat.format(Double.parseDouble(nominalPerdin)));
                        nominalBsTxt.setText(numberFormat.format(Double.parseDouble(nominalBs)));

                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        try {
                            JSONArray jArray = obj.getJSONArray("detailcomplete");

                            for (int i = 0; i < jArray.length(); i++){
                                JSONObject jObject = jArray.getJSONObject(i);
                                Perdin perdin = new Perdin();
                                String photoPerdin = jObject.getString("photo");
                                bitmap = ImageUtil.convert(photoPerdin);
                                perdin.setCatatanPerdin(jObject.getString("keterangan"));
                                perdin.setBitmapPerdin(bitmap);
                                data.add(perdin);
                            }
                            if (perdinDetailAdapter == null){
                                isFirst = false;
                                perdinDetailAdapter = new PerdinDetailAdapter(getContext(), data);
                                rvPerdinDetail.setAdapter(perdinDetailAdapter);
                            }
                            else {
                                perdinDetailAdapter.notifyDataSetChanged();
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
}

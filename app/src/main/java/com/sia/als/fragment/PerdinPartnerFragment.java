package com.sia.als.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.sia.als.adapter.PerdinPartnerAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.PerdinPartner;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.EndlessRecyclerViewScrollListener;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.kinst.jakub.view.StatefulLayout;

public class PerdinPartnerFragment extends Fragment {
    private View view;
    TextView toolbarTitle;
    SessionManagement sessionManagement;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvPerdinPartner;
    StatefulLayout statefulLayout;
    LinearLayoutManager linearLayoutManager;
    PerdinPartnerAdapter perdinPartnerAdapter;
    List<PerdinPartner> data = new ArrayList<>();
    Button retryBtn, notif;
    ImageButton backButton;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perdin_partner, container, false);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        sessionManagement = new SessionManagement(getContext());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        rvPerdinPartner = (RecyclerView) view.findViewById(R.id.perdin_partner_list_recycle);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        linearLayoutManager = new LinearLayoutManager(getContext());
        retryBtn = (Button) errorView.findViewById(R.id.button_retry);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        notif = (Button) getActivity().findViewById(R.id.button_general);

        toolbarTitle.setText("Perjalanan Dinas Partner");
        notif.setVisibility(View.GONE);
        rvPerdinPartner.setLayoutManager(linearLayoutManager);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        initScrollListener();
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
        rvPerdinPartner.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
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
        perdinPartnerAdapter = null;
        makeDataRequest(page);
    }

    private void makeDataRequest(int halaman) {
       if (isFirst)
       {
           statefulLayout.setState(Config.STATE_PROGRESS);
       }
       String tag_json_obj = "json_data_history_perdin_partner_req";
       String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
       Map<String, String> params = new HashMap<>();
       params.put("partner_id", "18");
       params.put("halaman", String.valueOf(halaman));
       params.put("limit", String.valueOf(limit));
       CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
               Config.PERDIN_PARTNER_URL, params, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               swipeRefreshLayout.setRefreshing(false);
               try {
                   if (isFirst)
                   {
                       statefulLayout.setState(Config.STATE_EMPTY);
                   }
                   Boolean status = response.getBoolean("status");
                   if (status){
                       statefulLayout.setState(StatefulLayout.State.CONTENT);
                       try {
                           JSONArray Jarray = response.getJSONArray("perdin");
                           for (int i = 0; i <Jarray.length(); i++){
                               JSONObject jObject = Jarray.getJSONObject(i);
                               PerdinPartner perdinPartner = new PerdinPartner();
                               perdinPartner.setId(jObject.getString("id"));
                               perdinPartner.setNamaKlien(jObject.getString("nama_klien"));
                               perdinPartner.setJumlahKaryawan(jObject.getString("jmlKaryawan"));
                               perdinPartner.setNomorPerdin(jObject.getString("no_transaksi"));
                               perdinPartner.setTanggalAwal(jObject.getString("tanggal_awal"));
                               perdinPartner.setTanggalAkhir(jObject.getString("tanggal_akhir"));
                               perdinPartner.setStatusId(jObject.getString("status_perdin"));
                               data.add(perdinPartner);
                           }
                           if (perdinPartnerAdapter == null)
                           {
                            isFirst = false;
                            perdinPartnerAdapter = new PerdinPartnerAdapter(getContext(), data);
                            perdinPartnerAdapter.setOnItemClickListener(new PerdinPartnerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, PerdinPartner obj, int position) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("perdin_id", obj.getId());
                                    DetailPerdinPartnerFragment detailPerdinPartnerFragment = new DetailPerdinPartnerFragment();
                                    detailPerdinPartnerFragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.m_frame, detailPerdinPartnerFragment)
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                            .commit();
                                }
                            });
                            rvPerdinPartner.setAdapter(perdinPartnerAdapter);
                            rvPerdinPartner.smoothScrollToPosition(0);
                           }
                           else{
                               perdinPartnerAdapter.notifyDataSetChanged();
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

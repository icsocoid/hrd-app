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

import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.util.EndlessRecyclerViewScrollListener;
import com.sia.als.util.SessionManagement;

import cz.kinst.jakub.view.StatefulLayout;

public class DetailPerdinPartnerFragment extends Fragment {
    private View view;
    SessionManagement sessionManagement;
    TextView toolbarTitle, noPerdinTxt, namaKlienTxt, tglAwalTxt, tglAkhirTxt, totPerdinTxt;
    Button retryBtn, notif, approveBtn;
    ImageButton backButton;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvPerdinPartnerDetail;
    StatefulLayout statefulLayout;
    LinearLayoutManager linearLayoutManager;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perdin_partner_detail, container, false);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        noPerdinTxt = (TextView) view.findViewById(R.id.nomor_perdin_txt);
        namaKlienTxt = (TextView) view.findViewById(R.id.nama_klien_txt);
        tglAwalTxt = (TextView) view.findViewById(R.id.tanggal_awal_txt);
        tglAkhirTxt = (TextView) view.findViewById(R.id.tanggal_akhir_txt);
        totPerdinTxt = (TextView) view.findViewById(R.id.total_perdin_txt);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        approveBtn = (Button) getActivity().findViewById(R.id.approve_btn);
        retryBtn = (Button) errorView.findViewById(R.id.button_retry);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        rvPerdinPartnerDetail = (RecyclerView) view.findViewById(R.id.perdin_partner_detail_list_recycler);
        linearLayoutManager = new LinearLayoutManager(getContext());
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);

        toolbarTitle.setText("Perjalanan Dinas Partner");
        notif.setVisibility(View.VISIBLE);
        rvPerdinPartnerDetail.setLayoutManager(linearLayoutManager);
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
        PerdinPartnerFragment perdinPartnerFragment = new PerdinPartnerFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, perdinPartnerFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void initScrollListener() {
        rvPerdinPartnerDetail.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
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

    }

    private void makeDataRequest(int halaman) {
    }
}

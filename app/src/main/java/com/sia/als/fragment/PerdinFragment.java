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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.sia.als.adapter.TaskAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Perdin;
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

public class PerdinFragment extends Fragment {
    private View view;
    TextView toolbarTitle, today1, date;
    SessionManagement sessionManagement;
    RecyclerView rvPerdin;
    PerdinAdapter perdinAdapter;
    StatefulLayout statefulLayout;
    Button retryBtn, btFilter, notif, filterDialogBtn;
    ImageButton backButton;
    SwipeRefreshLayout swipeRefreshLayout;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;
    List<Perdin> data = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private String dariTanggal, sampaiTanggal;
    private int mMonth, mYear, mDay;

    SearchView simpleSearchView;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    LinearLayout calendar1, calendar2;
    ImageButton close;
    private String searchTerm;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perdin, container, false);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Perjalanan Dinas");
        sessionManagement = new SessionManagement(getContext());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        rvPerdin = (RecyclerView) view.findViewById(R.id.perdin_list_recycle);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        linearLayoutManager = new LinearLayoutManager(getContext());
        retryBtn = (Button) errorView.findViewById(R.id.button_retry);
        filterDialogBtn = (Button) view.findViewById(R.id.layoutJournalChooseDate);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        notif = (Button) getActivity().findViewById(R.id.button_general);


        notif.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        rvPerdin.setLayoutManager(linearLayoutManager);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));
        statefulLayout.setStateView(Config.STATE_ERROR, errorView);

        setDariTanggal("");
        setSampaiTanggal("");
        setSearchTerm("");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetData();
                initScrollListener();
            }
        });

        filterDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Info", "Button Pressed");
                showFilterDialog();
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

    private void showFilterDialog()
    {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_filter);

        close = dialog.findViewById(R.id.bt_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btFilter = (Button) dialog.findViewById(R.id.bt_filter);
        today1 = dialog.findViewById(R.id.today1);
        today1.setText(simpleDateFormat.format(new Date()));
        calendar1 = dialog.findViewById(R.id.calender1);
        calendar1.setOnClickListener(new View.OnClickListener() {
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
                                String drTgl = year+ "-" + fm + "-" + fd;
                                today1.setText( drTgl);
                                setDariTanggal(drTgl);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });

        calendar2 = dialog.findViewById(R.id.calender2);
        date = dialog.findViewById(R.id.date);
        date.setText(simpleDateFormat.format(new Date()));
        calendar2.setOnClickListener(new View.OnClickListener() {
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

    private void setDariTanggal(String dariTanggal) {
        this.dariTanggal = dariTanggal;
    }
    public String getDariTanggal(){
        return dariTanggal;
    }
    private void setSampaiTanggal(String sampaiTanggal) {
        this.sampaiTanggal = sampaiTanggal;
    }
    public String getSampaiTanggal(){
        return sampaiTanggal;
    }

    private void initScrollListener() {
        rvPerdin.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
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
        perdinAdapter = null;
        makeDataRequest(page);
    }
    private void makeDataRequest(int halaman) {
        if (isFirst)
        {
            statefulLayout.setState(Config.STATE_PROGRESS);
        }
        String tag_json_obj = "json_data_history_perdin_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        params.put("halaman", String.valueOf(halaman));
        params.put("limit", String.valueOf(limit));
        params.put("dari_tanggal", getDariTanggal());
        params.put("sampai_tanggal", getSampaiTanggal());
        params.put("searchTerm", getSearchTerm());
        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.PERDIN_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    if (isFirst)
                    {
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        try {
                            JSONArray Jarray = response.getJSONArray("perdin");
                            for (int i = 0; i < Jarray.length(); i++){
                                JSONObject jsonObject = Jarray.getJSONObject(i);
                                Perdin perdin = new Perdin();
                                perdin.setId(jsonObject.getString("id"));
                                perdin.setNomorPerdin(jsonObject.getString("no_transaksi"));
//                                perdin.setNamaKlien(jsonObject.getString("nama_klien"));
                                perdin.setTanggal(jsonObject.getString("tanggal"));
                                perdin.setStatusId(jsonObject.getString("status_perdin"));
                                data.add(perdin);
                            }
                            if (perdinAdapter == null)
                            {
                                isFirst = false;
                                perdinAdapter = new PerdinAdapter(getContext(), data);
                                perdinAdapter.setmOnEditClickListener(new PerdinAdapter.OnEditClickListener() {
                                    @Override
                                    public void onItemClick(View view, String val) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("perdin_id", val);
                                        CompletionPerdinFragment completionPerdinFragment = new CompletionPerdinFragment();
                                        completionPerdinFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, completionPerdinFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });

                                perdinAdapter.setmOnDetailClickListener(new PerdinAdapter.OnDetailClickListener() {
                                    @Override
                                    public void onItemClick(View view, String val) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("perdin_id", val);
                                        DetailPerdinFragment detailPerdinFragment = new DetailPerdinFragment();
                                        detailPerdinFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, detailPerdinFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });

                                perdinAdapter.setOnItemClickListener(new PerdinAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, Perdin obj, int position) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("perdin_id", obj.getId());
                                        DetailPerdinFragment detailPerdinFragment = new DetailPerdinFragment();
                                        detailPerdinFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, detailPerdinFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });

                                rvPerdin.setAdapter(perdinAdapter);
                                rvPerdin.smoothScrollToPosition(0);
                            }
                            else{
                                perdinAdapter.notifyDataSetChanged();
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

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}

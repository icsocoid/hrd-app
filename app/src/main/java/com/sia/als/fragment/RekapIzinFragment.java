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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import com.sia.als.adapter.PengajuanAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Pengajuan;
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

public class RekapIzinFragment extends Fragment {
    private View view;
    ImageButton backButton, close;
    ImageView imageView;
    SessionManagement sessionManagement;
    TextView toolbarTitle, today1, date;
    RecyclerView rvIzin;
    PengajuanAdapter pengajuanAdapter;
    FloatingActionButton addIzinBtn;
    Button addBtn,saveBtn,notif,btFilter;
    StatefulLayout statefulLayout;
    Button retryBtn, btnAdd;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int page = 0;
    int limit = 10;
    boolean isLoading = false;
    boolean isFirst = true;
    List<Pengajuan> data = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private String dariTanggal;
    private String sampaiTanggal;
    private int mMonth, mYear, mDay;
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
    LinearLayout calender1, calender2,filterDialogBtn;
    String idIzin;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_izin, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        //saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setVisibility(View.GONE);
        notif.setText("");
        notif.setWidth(24);
        notif.setHeight(24);
        notif.setBackgroundResource(R.drawable.ic_add_black_24dp);
        if(getArguments() != null) {
            idIzin = getArguments().getString("id_izin");
            Log.i("idIzin", idIzin);
        }
        //saveBtn.setVisibility(View.GONE);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Histori IZIN");
        backButton.setVisibility(View.GONE);
        setDariTanggal("");
        setSampaiTanggal("");
        imageView = (ImageView) view.findViewById(R.id.img_choose_date);
        rvIzin = (RecyclerView) view.findViewById(R.id.list_izin);
        filterDialogBtn = (LinearLayout) view.findViewById(R.id.layoutJournalChooseDate);
        //emptyGroup = (Group) view.findViewById(R.id.group_empty_state);;
        addIzinBtn = (FloatingActionButton) view.findViewById(R.id.list_izin_floating_button);
        //addBtn = (Button) view.findViewById(R.id.button_add_izin);
        rvIzin.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        statefulLayout = (StatefulLayout) view.findViewById(R.id.stateful_layout);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvIzin.setLayoutManager(linearLayoutManager);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nested_content);
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

        sessionManagement = new SessionManagement(getContext());
//        addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddIzinFragment();
//                makeDataRequest(0);
//            }
//        });
        addIzinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddIzinFragment();
                makeDataRequest(0);
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddIzinFragment();

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

    private void showAddFragment() {
        AddIzinFragment addIzinFragment = new AddIzinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, addIzinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, homeFragment)
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

    private void initScrollListener()
    {
        rvIzin.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
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
        pengajuanAdapter = null;
        makeDataRequest(page);
    }

    private void showAddIzinFragment()
    {
        AddIzinFragment addIzinFragment = new AddIzinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, addIzinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void makeDataRequest(int halaman) {
        /*final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show(); */
        if(isFirst)
        {
            statefulLayout.setState(Config.STATE_PROGRESS);
        }
        String tag_json_obj = "json_data_history_izin_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        if(idIzin == null) {
            idIzin = "";
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        params.put("id_izin", idIzin);
        params.put("halaman", String.valueOf(halaman));
        params.put("limit", String.valueOf(limit));
        params.put("dari_tanggal", getDariTanggal());
        params.put("sampai_tanggal", getSampaiTanggal());
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.IZIN_HISTORY, params, new Response.Listener<JSONObject>() {

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
                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        //List<Pengajuan> data = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("izin");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Pengajuan izin = new Pengajuan();
                                izin.setId(json_data.getString("id"));
                                //  izin.setAls_employee_id(json_data.getString("als_employee_id"));
                                //izin.setAls_hrm_izin_id(json_data.getString("als_hrm_izin_id"));
                                izin.setNama_izin(json_data.getString("nama_izin"));
                                izin.setTanggal_awal(json_data.getString("tanggal_awal"));
                                izin.setTanggal_akhir(json_data.getString("tanggal_akhir"));
                                //    izin.setPhoto(json_data.getString("photo"));
                                //   izin.setLatitude(json_data.getString("latitude"));
                                //    izin.setLongitude(json_data.getString("longitude"));
                                izin.setTanggal(json_data.getString("tanggal"));
                                izin.setKeterangan(json_data.getString("keterangan"));
                                izin.setStatus_izin(json_data.getString("status_izin"));
                                data.add(izin);
                            }
                            if(pengajuanAdapter == null)
                            {
                                isFirst = false;
                                pengajuanAdapter = new PengajuanAdapter(getContext(),data);
                                pengajuanAdapter.setOnItemClickListener(new PengajuanAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, Pengajuan obj, int position) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id_izin",obj.getId());
                                        DetailIzinFragment detailIzinFragment = new DetailIzinFragment();
                                        detailIzinFragment.setArguments(bundle);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.m_frame, detailIzinFragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }
                                });
                                //isLoading = false;
                                //statefulLayout.setState(Config.STATE_EMPTY);
                                //emptyGroup.setVisibility(View.GONE);
//                            addIzinBtn.show();
                                rvIzin.setAdapter(pengajuanAdapter);
                                //rvIzin.refreshDrawableState();
                                rvIzin.smoothScrollToPosition(0);

                            }else{
                                pengajuanAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        notif.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showAddIzinFragment();
                            }
                        });
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
                    //dialog.hide();
                    addIzinBtn.show();
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onResume(){
        super.onResume();
        resetData();
    }

}

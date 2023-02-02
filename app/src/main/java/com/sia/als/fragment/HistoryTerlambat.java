package com.sia.als.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.sia.als.R;
import com.sia.als.adapter.HistoryAbsensiAdapter;
import com.sia.als.adapter.PtkpAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Absensi;
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

public class HistoryTerlambat extends Fragment {
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
        view = inflater.inflate(R.layout.fragment_history_terlambat, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        setDariTanggal("");
        setSampaiTanggal("");
        //saveBtn.setVisibility(View.GONE);//buat apa?
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Histori Terlambat");
        backButton.setVisibility(View.GONE);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setVisibility(View.GONE);
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


        initScrollListener();
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
        AbsensiFragment absensiFragment = new AbsensiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, absensiFragment)
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

//    private void attemptData() {
//    }

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

//        final ProgressDialog dialog = new ProgressDialog(getActivity());
//        dialog.setMessage("please wait...");
//        dialog.show();

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
                Config.LIST_TERLAMBAT, params, new Response.Listener<JSONObject>() {

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
                        try {
                            JSONArray Jarray = response.getJSONArray("absensi");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Absensi absensi = new Absensi();
                                absensi.setId(json_data.getString("id"));
                                //absensi.setAlamat_maps_masuk(json_data.getString("alamat_maps_masuk"));
                                //absensi.setAlamat_maps_pulang(json_data.getString("alamat_maps_pulang"));
                                //absensi.setAls_employee_id(json_data.getString("als_employee_id"));
                                // absensi.setDevice_id_masuk(json_data.getString("device_id_masuk"));
                                // absensi.setDevice_id_pulang(json_data.getString("device_id_pulang"));
                                absensi.setJam_masuk(json_data.getString("jam_masuk"));
                                absensi.setJam_pulang(json_data.getString("jam_pulang"));
                                //  absensi.setJenis(json_data.getString("jenis"));
                                //  absensi.setNote(json_data.getString("note"));
                                // absensi.setQrcode_masuk(json_data.getString("qrcode_masuk"));
                                //  absensi.setQrcode_pulang(json_data.getString("qrcode_pulang"));
                                absensi.setStatus_masuk(json_data.getString("status_masuk"));
                                absensi.setStatus_pulang(json_data.getString("status_pulang"));
                                // absensi.setPhoto_masuk(json_data.getString("photo_masuk"));
                                //  absensi.setPhoto_pulang(json_data.getString("photo_pulang"));
                                //  absensi.setMac_address_masuk(json_data.getString("mac_address_masuk"));
                                //  absensi.setMac_address_pulang(json_data.getString("mac_address_pulang"));
                                // absensi.setLatitude_pulang(json_data.getString("latitude_pulang"));
                                // absensi.setLatitude_masuk(json_data.getString("latitude_masuk"));
                                // absensi.setLongitude_masuk(json_data.getString("longitude_masuk"));
                                //  absensi.setLongitude_pulang(json_data.getString("longitude_pulang"));
                                absensi.setTanggal(json_data.getString("tanggal"));
                                data.add(absensi);
                            }
                            if(historyAbsensiAdapter == null)
                            {
                                isFirst = false;
                                historyAbsensiAdapter = new HistoryAbsensiAdapter(getContext(),data);
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
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
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
                    //dialog.hide();
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
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

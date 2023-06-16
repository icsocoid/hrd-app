package com.sia.als.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.BottomIzinAdapter;
import com.sia.als.adapter.BottomTerlambatAdapter;
import com.sia.als.config.Config;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.model.Absensi;
import com.sia.als.model.Izin;
import com.sia.als.model.Terlambat;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
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

public class AddTerlambatFragment extends Fragment {
    private View view;
    ImageButton backButton;
    TextView toolbarTitle;
    Button notif;
    SessionManagement sessionManagement;
    BottomTerlambatAdapter terlambatAdapter;
    EditText jenisTerlambatTxt;
    ImageButton pilihanIzinBtn;
    LinearLayout calender1;
    TextView today1;
    private int mMonth, mYear, mDay;
    List<Terlambat> terlambatList = new ArrayList<>();
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
    EditText keteranganTxt;
    private String jenisTerlambatId;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheet;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    StatefulLayout statefulLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_terlambat, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        backButton.setVisibility(View.GONE);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Pengajuan Izin");
        sessionManagement = new SessionManagement(getContext());
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setBackgroundResource(0);
        notif.setVisibility(View.VISIBLE);
        notif.setText("");
        notif.setBackgroundResource(R.drawable.ic_save);
        notif.setWidth(24);
        notif.setHeight(24);
        bottomSheet = (View) view.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        linearLayoutManager = new LinearLayoutManager(getContext());
        jenisTerlambatTxt = (EditText) view.findViewById(R.id.pilihan_pengajuan_txt);
        pilihanIzinBtn = (ImageButton) view.findViewById(R.id.pilihan_pengajuan_btn);
        today1 = view.findViewById(R.id.today1);
        keteranganTxt = view.findViewById(R.id.keterangan_txt);
        today1.setText(simpleDateFormat.format(new Date()));
        calender1 =  view.findViewById(R.id.calender1);
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
                                if (dayOfMonth<0){
                                    fd="0"+dayOfMonth;
                                }
                                today1.setText( year+ "-" + fm + "-" + fd);

                            }
                        }, mYear, mMonth, mDay);


                Calendar last7days=Calendar.getInstance();
                last7days.add(Calendar.DAY_OF_YEAR,-7);
                last7days=clearTimes(last7days);
                datePickerDialog.getDatePicker().setMinDate(last7days.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptData();
            }
        });

        pilihanIzinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        jenisTerlambatTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
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

        return view;
    }
    private Calendar clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c;
    }

    private void showFragment() {
        PengajuanAbsensiFragment pengajuanAbsensiFragment = new PengajuanAbsensiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, pengajuanAbsensiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void attemptData()
    {
        if(TextUtils.isEmpty(keteranganTxt.getText().toString()))
        {
            keteranganTxt.setTextColor(getResources().getColor(R.color.active_color));
            keteranganTxt.requestFocus();
        }
        else
        {
            makeRequestSubmitData();
        }

    }

    private void makeRequestSubmitData()
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_pengajuan_absensi_req";
        Map<String, String> params = new HashMap<String, String>();
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);

        params.put("tanggal_absensi", today1.getText().toString());
        params.put("user_id", userId);
        params.put("keterangan", keteranganTxt.getText().toString());
        params.put("terlambat_id", String.valueOf(getJenisTerlambatId()));
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADD_TERLAMBAT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {

                        SuksesDialog suksesDialog = new SuksesDialog(AddTerlambatFragment.this);
                        suksesDialog.show(getFragmentManager(),"");
                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_LONG,TastyToast.ERROR).show();

                    }
                }
                catch (JSONException e)
                {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    public void showListTerlambat()
    {
        PengajuanAbsensiFragment pengajuanAbsensiFragment = new PengajuanAbsensiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, pengajuanAbsensiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View viewDialog = getLayoutInflater().inflate(R.layout.sheet_pengajuan_list, null);
        statefulLayout = (StatefulLayout) viewDialog.findViewById(R.id.stateful);
        statefulLayout.setStateView(Config.STATE_PROGRESS, LayoutInflater.from(getContext()).inflate(R.layout.state_progress, null));
        statefulLayout.setStateView(Config.STATE_EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.activity_empty, null));
        statefulLayout.setStateView(Config.STATE_NO_CONNECTION, LayoutInflater.from(getContext()).inflate(R.layout.actvity_no_internet_connection, null));
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.state_error, null);
        statefulLayout.setStateView(Config.STATE_ERROR, errorView);
        recyclerView = (RecyclerView) viewDialog.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        makePengajuanRequest();

        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mBottomSheetDialog.setContentView(viewDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    private void makePengajuanRequest()
    {
        statefulLayout.setState(Config.STATE_PROGRESS);
        String tag_json_obj = "json_pengajuan_absensi_req";
        Map<String, String> params = new HashMap<String, String>();

        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        params.put("user_id", userId);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.TERLAMBAT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    statefulLayout.setState(Config.STATE_EMPTY);
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        statefulLayout.setState(StatefulLayout.State.CONTENT);
                        terlambatList = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("keterlambatan");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Terlambat terlambat = new Terlambat();
                                terlambat.setId(json_data.getString("id"));
                                terlambat.setNamaAbsensi(json_data.getString("nama_pengajuan"));
                                terlambat.setDescription(json_data.getString("deskripsi"));
                                terlambatList.add(terlambat);
                            }
                            terlambatAdapter = new BottomTerlambatAdapter(getContext(),terlambatList);
                            terlambatAdapter.setOnItemClickListener(new BottomTerlambatAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Terlambat obj, int position) {
                                    setJenisTerlambatId(obj.getId());
                                    jenisTerlambatTxt.setText(obj.getNamaAbsensi());
                                    mBottomSheetDialog.dismiss();
                                }
                            });
                            recyclerView.setAdapter(terlambatAdapter);
                            recyclerView.refreshDrawableState();
                            recyclerView.smoothScrollToPosition(0);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        statefulLayout.setState(Config.STATE_EMPTY);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    statefulLayout.setState(Config.STATE_ERROR);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    statefulLayout.setState(Config.STATE_NO_CONNECTION);
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public String getJenisTerlambatId() {
        return jenisTerlambatId;
    }

    public void setJenisTerlambatId(String jenisTerlambatId) {
        this.jenisTerlambatId = jenisTerlambatId;
    }

}

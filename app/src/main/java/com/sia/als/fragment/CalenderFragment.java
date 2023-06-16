package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.AdminIzinAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Pengajuan;
import com.sia.als.util.CustomVolleyJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalenderFragment extends Fragment {

    View view;
    TextView toolbarTitle;
    Button saveBtn;
    CompactCalendarView calendarView;
    TextView textViewShowDate;
    TextView textViewShowEvent;
    TextView showMonth;
    RecyclerView rvIzin;
    AdminIzinAdapter izinAdapter;
    SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    ConstraintLayout emptyLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.STATUS_BAR},1);
        view = inflater.inflate(R.layout.fragment_calender, container, false);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Kalender Izin Karyawan");
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        saveBtn.setVisibility(View.GONE);
        rvIzin = (RecyclerView) view.findViewById(R.id.izin_list_recycler);
        rvIzin.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvIzin.setItemAnimator(new DefaultItemAnimator());
        izinAdapter = new AdminIzinAdapter(getContext(),new ArrayList<Pengajuan>());
        rvIzin.setAdapter(izinAdapter);

        emptyLayout = (ConstraintLayout) view.findViewById(R.id.manage_karyawan_list_platform_empty);

        textViewShowDate= (TextView) view.findViewById(R.id.showDate);
        calendarView = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        textViewShowEvent= (TextView) view.findViewById(R.id.eventtxt);
        showMonth= (TextView) view.findViewById(R.id.showSrollMonth);

        //First Day of Week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.shouldSelectFirstDayOfMonthOnScroll(false);
        calendarView.displayOtherMonthDays(true);

        Calendar calendar = Calendar.getInstance();
        final String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        textViewShowDate.setText(currentDate);
        makeDataRequest(sdf.format(new Date()));

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                makeDataRequest(sdf.format(dateClicked));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                showMonth.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        return view;
    }

    public void makeDataRequest(String tanggal) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_admin_history_tanggal_izin_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("tanggal", tanggal);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADMIN_GET_STATUS_IZIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
               // Log.e("hasil",response.toString());
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        List<Pengajuan> data = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("izin");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Pengajuan izin = new Pengajuan();
                                izin.setId(json_data.getString("id"));
                                izin.setAls_employee_id(json_data.getString("als_employee_id"));
                                izin.setAls_hrm_izin_id(json_data.getString("als_hrm_izin_id"));
                                izin.setNama_izin(json_data.getString("nama_izin"));
                                izin.setTanggal_awal(json_data.getString("tanggal_awal"));
                                izin.setTanggal_akhir(json_data.getString("tanggal_akhir"));
                                izin.setPhoto(json_data.getString("photo"));
                                izin.setLatitude(json_data.getString("latitude"));
                                izin.setLongitude(json_data.getString("longitude"));
                                izin.setTanggal(json_data.getString("tanggal"));
                                izin.setKeterangan(json_data.getString("keterangan"));
                                izin.setStatus_izin(json_data.getString("status_izin"));
                                izin.setNamaKaryawan(json_data.getString("employee_name"));
                                data.add(izin);
                            }
                           // Log.e("jumlah",String.valueOf(data.size()));
                            izinAdapter.setOnItemClickListener(new AdminIzinAdapter.OnItemClickListener() {
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


                          //  rvIzin.refreshDrawableState();
                        //    rvIzin.smoothScrollToPosition(0);
                            izinAdapter.resetListData();
                            izinAdapter.insertData(data);
                            emptyLayout.setVisibility(View.GONE);
                            rvIzin.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        emptyLayout.setVisibility(View.VISIBLE);
                        rvIzin.setVisibility(View.GONE);

                    }

                } catch (JSONException e) {
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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}

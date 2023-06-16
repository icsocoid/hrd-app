package com.sia.als.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.HistoryAbsensiAdapter;
import com.sia.als.adapter.RekapAbsensiAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Absensi;
import com.sia.als.util.CustomVolleyJsonRequest;

import org.joda.time.LocalDate;
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

public class RekapAbsensiFragment extends Fragment {
    View view;
    TextView toolbarTitle;
    DayScrollDatePicker dayPicker;
    ConstraintLayout emptyLayout;
    RecyclerView rvAbsensi;
    RekapAbsensiAdapter historyAbsensiAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rekap_absensi, container, false);
        dayPicker = (DayScrollDatePicker) view.findViewById(R.id.day_date_picker);
        emptyLayout = (ConstraintLayout) view.findViewById(R.id.manage_absensi_list_platform_empty);
        rvAbsensi = (RecyclerView) view.findViewById(R.id.absensi_list_recycler);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Histori Absensi yang rekap");
        rvAbsensi.setLayoutManager(new LinearLayoutManager(getContext()));
        setStartAndEndDate();
        return view;
    }

    private void setStartAndEndDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date monthFirstDay = calendar.getTime();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date monthLastDay = new Date();

        SimpleDateFormat dfMonth = new SimpleDateFormat("M");
        SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat dfDay = new SimpleDateFormat("d");
        String startMonth = dfMonth.format(monthFirstDay);
        String startYear = dfYear.format(monthFirstDay);
        String endDate = dfDay.format(monthLastDay);

        dayPicker.setStartDate(1,Integer.parseInt(startMonth),Integer.parseInt(startYear));
        dayPicker.setEndDate(Integer.parseInt(endDate),Integer.parseInt(startMonth),Integer.parseInt(startYear));
        dayPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if(date != null){
                    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
                    String tglPilih = simpleDateFormat.format(date);
                    makeDataRequest(tglPilih);
                }
            }
        });

        dayPicker.onDateSelectedChild(new LocalDate(Integer.parseInt(startYear), Integer.parseInt(startMonth), Integer.parseInt(endDate)));

    }

    public void makeDataRequest(String tanggal) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_history_admin_absensi_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("tanggal", tanggal);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADMIN_REKAP_ABSENSI_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        List<Absensi> data = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("absensi");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Absensi absensi = new Absensi();
                                absensi.setId(json_data.getString("id"));
                                absensi.setAlamat_maps_masuk(json_data.getString("alamat_maps_masuk"));
                                absensi.setAlamat_maps_pulang(json_data.getString("alamat_maps_pulang"));
                                absensi.setAls_employee_id(json_data.getString("als_employee_id"));
                                absensi.setDevice_id_masuk(json_data.getString("device_id_masuk"));
                                absensi.setDevice_id_pulang(json_data.getString("device_id_pulang"));
                                absensi.setJam_masuk(json_data.getString("jam_masuk"));
                                absensi.setJam_pulang(json_data.getString("jam_pulang"));
                                absensi.setJenis(json_data.getString("jenis"));
                                absensi.setNote(json_data.getString("note"));
                                absensi.setQrcode_masuk(json_data.getString("qrcode_masuk"));
                                absensi.setQrcode_pulang(json_data.getString("qrcode_pulang"));
                                absensi.setStatus_masuk(json_data.getString("status_masuk"));
                                absensi.setStatus_pulang(json_data.getString("status_pulang"));
                                absensi.setPhoto_masuk(json_data.getString("photo_masuk"));
                                absensi.setPhoto_pulang(json_data.getString("photo_pulang"));
                                absensi.setMac_address_masuk(json_data.getString("mac_address_masuk"));
                                absensi.setMac_address_pulang(json_data.getString("mac_address_pulang"));
                                absensi.setLatitude_pulang(json_data.getString("latitude_pulang"));
                                absensi.setLatitude_masuk(json_data.getString("latitude_masuk"));
                                absensi.setLongitude_masuk(json_data.getString("longitude_masuk"));
                                absensi.setLongitude_pulang(json_data.getString("longitude_pulang"));
                                absensi.setTanggal(json_data.getString("tanggal"));
                                absensi.setEmployeeName(json_data.getString("employee_name"));
                                data.add(absensi);
                            }
                            historyAbsensiAdapter = new RekapAbsensiAdapter(getContext(),data);
                            rvAbsensi.setAdapter(historyAbsensiAdapter);
                            rvAbsensi.refreshDrawableState();
                            rvAbsensi.smoothScrollToPosition(0);
                            emptyLayout.setVisibility(View.GONE);
                            rvAbsensi.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        emptyLayout.setVisibility(View.VISIBLE);
                        rvAbsensi.setVisibility(View.GONE);

                    }
                    dialog.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.hide();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}

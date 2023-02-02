package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailAbsensiFragment extends Fragment {
    View view;
    ImageButton backButton;
    TextView toolbarTitle,tanggalTxt,jamMasukTxt,statusMasukTxt,alamatMasukTxt,jamPulangTxt,statusPulangTxt,alamatPulangTxt;
    String oldFormat = "yyyy-MM-dd";
    String patternIzin = "dd MMMM yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
    SessionManagement sessionManagement;
    String id;
    Button notif;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        view = inflater.inflate(R.layout.fragment_absen_detail, container, false);
        sessionManagement = new SessionManagement(getContext());
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        backButton.setVisibility(View.GONE);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setVisibility(View.GONE);
        tanggalTxt = (TextView) view.findViewById(R.id.tanggal_txt);
        jamMasukTxt = (TextView) view.findViewById(R.id.jam_masuk_txt);
        statusMasukTxt = (TextView) view.findViewById(R.id.status_masuk_txt);
        alamatMasukTxt = (TextView) view.findViewById(R.id.alamat_masuk_txt);
        jamPulangTxt = (TextView) view.findViewById(R.id.jam_pulang_txt);
        statusPulangTxt = (TextView) view.findViewById(R.id.status_pulang_txt);
        alamatPulangTxt = (TextView) view.findViewById(R.id.alamat_pulang_txt);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Detail Absensi");
        if(getArguments() != null)
        {
            id = getArguments().getString("absen_id");
            makeDataRequest(id);
        }

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

    private void showFragment() {
        AbsensiFragment absensiFragment = new AbsensiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, absensiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void makeDataRequest(String absenId) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_absen_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("absen_id", absenId);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_HISTORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject obj = response.getJSONObject("absensi");
                        String jamMasuk = obj.getString("jam_masuk");
                        String jamPulang = obj.getString("jam_pulang");
                        String alamatMapMasuk = obj.getString("alamat_maps_masuk");
                        String alamatMapPulang = obj.getString("alamat_maps_pulang");
                        String tanggal = obj.getString("tanggal");
                        String statusMasuk = obj.getString("status_masuk");
                        String statusPulang = obj.getString("status_pulang");

                        try {
                            Date d = sdf.parse(tanggal);
                            sdf.applyPattern(patternIzin);
                            tanggal = sdf.format(d);
                        }
                        catch (ParseException p)
                        {

                        }
                        tanggalTxt.setText(tanggal);
                        jamMasukTxt.setText(jamMasuk);
                        jamPulangTxt.setText(jamPulang);
                        statusMasukTxt.setText(statusMasuk);
                        statusPulangTxt.setText(statusPulang);
                        alamatMasukTxt.setText(alamatMapMasuk);
                        alamatPulangTxt.setText(alamatMapPulang);

                    } else {
                        String error = response.getString("message");
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();

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

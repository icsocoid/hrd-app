package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.AdminIzinAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Pengajuan;
import com.sia.als.util.CustomVolleyJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminIzinFragment extends Fragment {

    private View view;
    TextView toolbarTitle;
    RecyclerView rvIzin;
    ConstraintLayout emptyLayout;
    AdminIzinAdapter izinAdapter;
    EditText searchTxt;
    ImageView searchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_izin, container, false);
        emptyLayout = (ConstraintLayout) view.findViewById(R.id.manage_karyawan_list_platform_empty);
        rvIzin = (RecyclerView) view.findViewById(R.id.izin_list_recycler);
        searchTxt = (EditText) view.findViewById(R.id.karyawan_list_edittext_search);
        searchBtn = (ImageView) view.findViewById(R.id.karyawan_list_icon_search);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Daftar Pengajuan Izin");
        rvIzin.setLayoutManager(new LinearLayoutManager(getContext()));
        makeDataRequest("");
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cari = searchTxt.getText().toString();
                makeDataRequest(cari);

            }
        });
        return view;
    }

    public void makeDataRequest(String search) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_admin_history_izin_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("search", search);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADMIN_IZIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
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
                            izinAdapter = new AdminIzinAdapter(getContext(),data);
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
                            emptyLayout.setVisibility(View.GONE);
                            rvIzin.setAdapter(izinAdapter);
                            rvIzin.refreshDrawableState();
                            rvIzin.smoothScrollToPosition(0);
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

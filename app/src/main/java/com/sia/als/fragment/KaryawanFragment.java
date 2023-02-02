package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.media.Image;
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
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.KaryawanAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Karyawan;
import com.sia.als.util.CustomVolleyJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KaryawanFragment extends Fragment {
    private View view;
    TextView toolbarTitle;
    RecyclerView rvKaryawan;
    ConstraintLayout emptyLayout;
    KaryawanAdapter karyawanAdapter;
    EditText searchTxt;
    ImageView searchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_karyawan, container, false);
        emptyLayout = (ConstraintLayout) view.findViewById(R.id.manage_karyawan_list_platform_empty);
        rvKaryawan = (RecyclerView) view.findViewById(R.id.karyawan_list_recycler);
        searchTxt = (EditText) view.findViewById(R.id.karyawan_list_edittext_search);
        searchBtn = (ImageView) view.findViewById(R.id.karyawan_list_icon_search);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Daftar Karyawan");
        rvKaryawan.setLayoutManager(new LinearLayoutManager(getContext()));
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
        String tag_json_obj = "json_data_admin_karyawan_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("search", search);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.KARYAWAN_ALL_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        List<Karyawan> data = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("karyawan");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Karyawan karyawan = new Karyawan();
                                karyawan.setId(json_data.getString("id"));
                                karyawan.setEmployeeName(json_data.getString("employee_name"));
                                karyawan.setEmployeeCode(json_data.getString("employee_code"));
                                karyawan.setEmail(json_data.getString("email"));
                                karyawan.setPhone(json_data.getString("phone"));
                                karyawan.setKtp(json_data.getString("ktp"));
                                karyawan.setPhoto(json_data.getString("photo"));
                                karyawan.setStatusEmployee(json_data.getString("status_employee"));
                                data.add(karyawan);
                            }
                            karyawanAdapter = new KaryawanAdapter(getContext(),data);
                            karyawanAdapter.setOnItemClickListener(new KaryawanAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Karyawan obj, int position) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("user_id",obj.getId());
                                    KaryawanProfileFragment dataFragment = new KaryawanProfileFragment();
                                    dataFragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.m_frame, dataFragment)
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                            .commit();
                                }
                            });
                            rvKaryawan.setAdapter(karyawanAdapter);
                            rvKaryawan.refreshDrawableState();
                            rvKaryawan.smoothScrollToPosition(0);
                            emptyLayout.setVisibility(View.GONE);
                            rvKaryawan.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            TastyToast.makeText(getActivity(), e.toString(), TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                        }
                    }
                    else {
                        emptyLayout.setVisibility(View.VISIBLE);
                        rvKaryawan.setVisibility(View.GONE);

                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
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

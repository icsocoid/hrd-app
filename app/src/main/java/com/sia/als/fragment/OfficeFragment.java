package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.KantorAdapter;
import com.sia.als.adapter.KaryawanAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Kantor;
import com.sia.als.model.Karyawan;
import com.sia.als.util.CustomVolleyJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficeFragment extends Fragment {
    View view;
    TextView toolbarTitle;
    RecyclerView rvKantor;
    ConstraintLayout emptyLayout;
    KantorAdapter kantorAdapter;
    EditText searchTxt;
    ImageView searchBtn;
    Button addOfficeBtn;
    FloatingActionButton addActionBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_office, container, false);
        emptyLayout = (ConstraintLayout) view.findViewById(R.id.manage_karyawan_list_platform_empty);
        rvKantor = (RecyclerView) view.findViewById(R.id.kantor_list_recycler);
        searchTxt = (EditText) view.findViewById(R.id.kantor_list_edittext_search);
        searchBtn = (ImageView) view.findViewById(R.id.karyawan_list_icon_search);
        addOfficeBtn = (Button) view.findViewById(R.id.button_add_office);
        addActionBtn = (FloatingActionButton) view.findViewById(R.id.fab_office);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Daftar Kantor");
        rvKantor.setLayoutManager(new LinearLayoutManager(getContext()));
        addOfficeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddOffice(null);
            }
        });
        addActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddOffice(null);
            }
        });
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

    private void showAddOffice(Bundle bundle)
    {
        AddOfficeFragment dataFragment = new AddOfficeFragment();
        if(bundle != null)
        {
            dataFragment.setArguments(bundle);
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, dataFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void makeDataRequest(String search) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_admin_office_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("search", search);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADMIN_OFFICE_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("success");
                    if (status) {
                        List<Kantor> data = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("lokasi");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Kantor kantor = new Kantor();
                                kantor.setId(json_data.getString("id"));
                                kantor.setNamaKantor(json_data.getString("nama_lokasi"));
                                kantor.setLatitude(json_data.getString("latitude"));
                                kantor.setLongitude(json_data.getString("longitude"));
                                kantor.setAlamat(json_data.getString("alamat"));
                                kantor.setStatusKantor(json_data.getString("status_lokasi"));
                                data.add(kantor);
                            }
                            kantorAdapter = new KantorAdapter(getContext(),data);
                            kantorAdapter.setOnItemClickListener(new KantorAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Kantor obj, int position) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id_kantor",obj.getId());
                                    showAddOffice(bundle);
                                }
                            });
                            rvKantor.setAdapter(kantorAdapter);
                            rvKantor.refreshDrawableState();
                            rvKantor.smoothScrollToPosition(0);
                            emptyLayout.setVisibility(View.GONE);
                            addActionBtn.show();
                            rvKantor.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            TastyToast.makeText(getActivity(), e.toString(), TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                        }
                    }
                    else {
                        emptyLayout.setVisibility(View.VISIBLE);
                        rvKantor.setVisibility(View.GONE);
                        addActionBtn.hide();
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

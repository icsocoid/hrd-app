package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.KantorAdapter;
import com.sia.als.config.Config;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.model.Kantor;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddOfficeFragment extends Fragment {

    View view;
    TextView toolbarTitle;
    FloatingActionButton getLocationBtn;
    EditText namaKantorTxt,latitudeTxt,longitudeTxt,alamatTxt;
    Button saveBtn;
    String idKantor="0";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_office, container, false);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        saveBtn.setText("SUBMIT");
        saveBtn.setVisibility(View.VISIBLE);
        toolbarTitle.setText("Tambah Kantor");
        getLocationBtn = (FloatingActionButton) view.findViewById(R.id.fab_location);
        namaKantorTxt = (EditText) view.findViewById(R.id.nama_kantor_txt);
        latitudeTxt = (EditText) view.findViewById(R.id.latitude_txt);
        longitudeTxt = (EditText) view.findViewById(R.id.longitude_txt);
        alamatTxt = (EditText) view.findViewById(R.id.alamat_txt);
        if(getArguments() != null) {
            idKantor = getArguments().getString("id_kantor");
            makeDataRequest();
        }

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOffice();
            }
        });

        return view;
    }

    public void saveOffice()
    {
        boolean cek = false;
        String pesan = "";
        if(alamatTxt.getText().toString().equals(""))
        {
            cek = true;
            pesan = "Alamat tidak boleh kosong";
        }
        if(latitudeTxt.getText().toString().equals(""))
        {
            cek = true;
            pesan = "Latitude tidak boleh kosong";
        }
        if(longitudeTxt.getText().toString().equals(""))
        {
            cek = true;
            pesan = "Longitude tidak boleh kosong";
        }
        if(namaKantorTxt.getText().toString().equals(""))
        {
            cek = true;
            pesan = "Nama kantor tidak boleh kosong";
        }
        if(cek)
        {
            TastyToast.makeText(getContext(),pesan,TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
        }
        else
        {
            makeRequestSubmitData();
        }
    }

    public void makeDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_admin_office_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_kantor", idKantor);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADMIN_DETAIL_OFFICE_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject jsonObject = response.getJSONObject("office");
                        namaKantorTxt.setText(jsonObject.getString("nama_kantor"));
                        latitudeTxt.setText(jsonObject.getString("latitude"));
                        longitudeTxt.setText(jsonObject.getString("longitude"));
                        alamatTxt.setText(jsonObject.getString("alamat"));
                    }
                    else {
                        TastyToast.makeText(getActivity(), response.getString("message"), TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
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

    private void makeRequestSubmitData()
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_office_req";
        Map<String, String> params = new HashMap<String, String>();


        params.put("latitude", latitudeTxt.getText().toString());
        params.put("longitude", longitudeTxt.getText().toString());
        params.put("nama_kantor", namaKantorTxt.getText().toString());
        params.put("alamat", alamatTxt.getText().toString());
        params.put("id_kantor", idKantor);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADMIN_ADD_OFFICE_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        SuksesDialog suksesDialog = new SuksesDialog(AddOfficeFragment.this);
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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    public void backToList()
    {
        OfficeFragment dataFragment = new OfficeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, dataFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


    private void getCurrentLocation()
    {
        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocation.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    double lat = location.getLatitude();
                    double lang = location.getLongitude();
                    latitudeTxt.setText(String.valueOf(lat));
                    longitudeTxt.setText(String.valueOf(lang));
                    String alamatMaps = "";
                    try {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addressList = geocoder.getFromLocation(lat, lang, 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);
                            alamatMaps = address.getAddressLine(0)+ ", " + address.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();
                            alamatTxt.setText(alamatMaps);
                        }
                    }
                    catch (IOException e) {

                    }
                    //String alamatMaps = location.get(0)+ ", " + location.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();


                }
            }
        });
    }


}

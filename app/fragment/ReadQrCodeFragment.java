package com.sia.als.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.an.deviceinfo.device.model.Device;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.Result;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReadQrCodeFragment extends Fragment {
    private CodeScanner mCodeScanner;
    Device empDevice;
    SessionManagement sessionManagement;
    ImageButton backButton;
    TextView toolbarTitle;
    Button saveBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_qrcode, container, false);
        backButton =  (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("QrCode Scanner");
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        saveBtn.setVisibility(View.GONE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        sessionManagement = new SessionManagement(getContext());
        empDevice = new Device(getContext());
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Config.RES_QRCODE = result.getText();
                        try {
                            JSONObject jsonObject = new JSONObject(result.getText());
                            String selfie =  jsonObject.getString("selfie");
                            if(selfie.equals("yes"))
                            {
                                SubmitFragment submitFragment = new SubmitFragment();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.m_frame, submitFragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                            }
                            else
                            {
                                submitAttemptData();
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e("json_convert",e.getMessage());
                        }

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void submitAttemptData()
    {

        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocation.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    double lat = location.getLatitude();
                    double lang = location.getLongitude();
                    String alamatMaps = "";
                    try {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addressList = geocoder.getFromLocation(lat, lang, 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);
                            alamatMaps = address.getAddressLine(0)+ ", " + address.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();
                        }
                    }
                    catch (IOException e) {

                    }
                    //String alamatMaps = location.get(0)+ ", " + location.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();
                    makeRequestData(lat,lang,alamatMaps);

                }
            }
        });


    }

    public void makeRequestData(double lat,double longg,String alamat)
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_submit_absen_req";
        //WifiManager manager = (WifiManager) getActivity().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);
        // WifiInfo info = manager.getConnectionInfo();

        String deviceInfo = empDevice.getManufacturer()+" "+empDevice.getHardware()+" "+empDevice.getDevice();
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        String macAddress = sessionManagement.getUserDetails().get(Config.KEY_MAC);
        Map<String, String> params = new HashMap<String, String>();
        params.put("photo", "");
        params.put("latitude", String.valueOf(lat));
        params.put("longitude", String.valueOf(longg));
        params.put("mac_address", macAddress);
        params.put("device", deviceInfo);
        params.put("user_id", userId);
        params.put("alamat_map", alamat);
        params.put("qr_code", Config.RES_QRCODE);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ABSENSI_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    Bundle bundle = new Bundle();
                    if (status) {

                        bundle.putString("status","sukses");
                        bundle.putString("page","absensi");
                        bundle.putString("body_message",response.getString("message"));

                    } else {

                        bundle.putString("status","fail");
                        bundle.putString("page","absensi");
                        bundle.putString("body_message",response.getString("message"));

                    }
                    SuksesFragment suksesFragment = new SuksesFragment();
                    suksesFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.m_frame, suksesFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
                catch (JSONException e)
                {

                    e.printStackTrace();
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

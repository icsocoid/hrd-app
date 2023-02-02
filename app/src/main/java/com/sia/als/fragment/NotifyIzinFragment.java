package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.dialog.PictureDialog;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotifyIzinFragment extends Fragment {
    View view;
    TextView toolbarTitle;
    ImageButton backButton;
    Button saveBtn;
    SessionManagement sessionManagement;
    EditText keteranganTxt,tanggalTxt;
    ImageView imageAttach;
    Bitmap bitmap;
    ConstraintLayout loadImage;
    int REQUEST_CAMERA = 100;
    int REQUEST_GALLERY = 20;
    String patternIzin = "dd MMMM yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(patternIzin,new Locale("id","ID"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_izin, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Pengajuan Izin Notif");
        backButton.setVisibility(View.GONE);
        sessionManagement = new SessionManagement(getContext());
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        saveBtn.setText("SUBMIT");
        saveBtn.setVisibility(View.VISIBLE);
        keteranganTxt = (EditText) view.findViewById(R.id.keterangan_txt);
        tanggalTxt = (EditText) view.findViewById(R.id.tanggal_txt);
        loadImage = (ConstraintLayout) view.findViewById(R.id.constraint_add_picture_placeholder);
        imageAttach = (ImageView) view.findViewById(R.id.imageview_add_picture_placeholder);
        tanggalTxt.setText(sdf.format(new Date()));
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddImageDialog(REQUEST_CAMERA,REQUEST_GALLERY);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptData();
            }
        });
        return view;
    }

    private void showAddImageDialog(int reqCamera,int reqFile) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PictureDialog addPictureDialogFragment = new PictureDialog(reqCamera,reqFile);
        addPictureDialogFragment.setTargetFragment(this, reqCamera);
        addPictureDialogFragment.show(fm, getTag());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {

            //mengambil fambar dari Gallery
            bitmap = (Bitmap) data.getExtras().get("data");

            imageAttach.setImageBitmap(bitmap);

        }

        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                imageAttach.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());
            mFusedLocation.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        double lat = location.getLatitude();
                        double lang = location.getLongitude();

                        //String alamatMaps = location.get(0)+ ", " + location.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();
                        makeRequestSubmitData(lat,lang);

                    }
                }
            });
        }
    }

    private void makeRequestSubmitData(double lat,double longg)
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_izin_req";
        Map<String, String> params = new HashMap<String, String>();
        String photo = "";
        if(bitmap != null)
        {
            photo = ImageUtil.convert(bitmap);
        }
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);

        params.put("photo", photo);
        params.put("latitude", String.valueOf(lat));
        params.put("longitude", String.valueOf(longg));
        String oldFormat = "yyyy-MM-dd";
        sdf.applyPattern(oldFormat);
        params.put("mulai", sdf.format(new Date()));
        params.put("akhir", sdf.format(new Date()));
        params.put("user_id", userId);
        params.put("keterangan", keteranganTxt.getText().toString());
        params.put("izin_id", "3");
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADD_IZIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        dialog.hide();
                        SuksesDialog suksesDialog = new SuksesDialog(NotifyIzinFragment.this);
                        suksesDialog.show(getFragmentManager(),"");
                    } else {
                        dialog.hide();
                        String error = response.getString("message");
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();

                    }
                }
                catch (JSONException e)
                {
                    dialog.hide();
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

    public void showListIzin()
    {
        IzinFragment izinFragment = new IzinFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, izinFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}

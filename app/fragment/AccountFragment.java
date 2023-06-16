package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.hardware.Camera;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.an.deviceinfo.device.model.Device;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.activity.LoginActivity;
import com.sia.als.adapter.PtkpAdapter;
import com.sia.als.config.Config;
import com.sia.als.dialog.PictureDialog;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.model.Ptkp;
import com.sia.als.util.ConnectivityReceiver;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;
import com.smartlib.addresspicker.AddressPickerActivity;
import com.smartlib.addresspicker.MyLatLng;
import com.smartlib.addresspicker.Pin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AccountFragment extends Fragment{
    private View view;
    Button saveBtn,logoutBtn,notif;
    EditText alamatMapTxt,npwpTxt,namaTxt,ktpTxt,emailTxt,phoneTxt,alamatTxt,passwordTxt,konfPasswordTxt;
    ImageButton backButton;
    TextView toolbarTitle;
    ImageView pinLocBtn,loadImageGallery,imageViewPlaceholder,imageViewKTPPlaceholder,uploadKtpBtn;
    int REQUEST_ADDRESS = 132;
    int REQUEST_CAMERA_PROFILE = 100;
    int REQUEST_GALLERY_PROFILE = 20;
    int REQUEST_CAMERA_KTP = 200;
    int REQUEST_GALLERY_KTP = 40;
    private double mapsLatitude;
    private double mpasLongitude;
    Bitmap bitmap,bitmapKtp;
    Device empDevice;
    private String deviceName;
    SessionManagement sessionManagement;
    ConstraintLayout imageKTP;
    NestedScrollView profileScroll;
    EditText rtTxt,rwTxt,kelurahanTxt,kecamatanTxt,kotaTxt,kodePosTxt,propinsiTxt;
    EditText rtKtpTxt,rwKtpTxt,kelurahanKtpTxt,kecamatanKtpTxt,kotaKtpTxt,kodePosKtpTxt,propinsiKtpTxt,alamatKtpTxt;
    Spinner ptkpCmb;
    PtkpAdapter ptkpAdapter;
    List<Ptkp> ptkpList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_WIFI_STATE},1);
        view = inflater.inflate(R.layout.activity_account, container, false);
        backButton =  (ImageButton) getActivity().findViewById(R.id.button_back);
        backButton.setVisibility(View.GONE);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setBackgroundResource(0);
        notif.setVisibility(View.VISIBLE);
        notif.setText("");
        notif.setBackgroundResource(R.drawable.ic_save);
        notif.setWidth(24);
        notif.setHeight(24);
        toolbarTitle.setText("Account");

        pinLocBtn = (ImageView) view.findViewById(R.id.image_scan_barcode);
        alamatMapTxt = (EditText) view.findViewById(R.id.alamat_map_txt);
        npwpTxt = (EditText) view.findViewById(R.id.npwp_txt);
        ktpTxt = (EditText) view.findViewById(R.id.ktp_txt);
        emailTxt = (EditText) view.findViewById(R.id.email_txt);
        phoneTxt = (EditText) view.findViewById(R.id.phone_txt);
        alamatTxt = (EditText) view.findViewById(R.id.alamat_txt);
        imageKTP = (ConstraintLayout) view.findViewById(R.id.constraint_form_ktp);
        imageViewKTPPlaceholder = (ImageView) view.findViewById(R.id.imageview_add_ktp_placeholder);
        uploadKtpBtn = (ImageView) view.findViewById(R.id.image_upload_ktp);
        imageKTP.setVisibility(View.GONE);
        profileScroll = (NestedScrollView) view.findViewById(R.id.scrollview_form_product) ;

        rtTxt = (EditText) view.findViewById(R.id.rt_txt);
        rwTxt = (EditText) view.findViewById(R.id.rw_txt);
        kelurahanTxt = (EditText) view.findViewById(R.id.kelurahan_txt);
        kecamatanTxt = (EditText) view.findViewById(R.id.kec_txt);
        kotaTxt = (EditText) view.findViewById(R.id.kota_txt);
        kodePosTxt = (EditText) view.findViewById(R.id.kode_pos_txt);
        propinsiTxt = (EditText) view.findViewById(R.id.propinsi_txt);

        alamatKtpTxt = (EditText) view.findViewById(R.id.alamat_ktp_txt);
        rtKtpTxt = (EditText) view.findViewById(R.id.rt_ktp_txt);
        rwKtpTxt = (EditText) view.findViewById(R.id.rw_ktp_txt);
        kelurahanKtpTxt = (EditText) view.findViewById(R.id.kelurahan_ktp_txt);
        kecamatanKtpTxt = (EditText) view.findViewById(R.id.kec_ktp_txt);
        kotaKtpTxt = (EditText) view.findViewById(R.id.kota_ktp_txt);
        kodePosKtpTxt = (EditText) view.findViewById(R.id.kode_pos_ktp_txt);
        propinsiKtpTxt = (EditText) view.findViewById(R.id.propinsi_ktp_txt);
        ptkpCmb = (Spinner) view.findViewById(R.id.spinner_ptkp);
        sessionManagement = new SessionManagement(getContext());
        // npwpTxt.addTextChangedListener(new NumberTextWatcher(npwpTxt));
        pinLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLocation();
            }
        });

        uploadKtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddImageDialog(REQUEST_CAMERA_KTP,REQUEST_GALLERY_KTP);
            }
        });

        makePtkpRequest();

        empDevice = new Device(getContext());

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSendingData();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        makeDataRequest();
        return view;
    }

    private void showFragment() {
        NewProfileFragment newProfileFragment = new NewProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, newProfileFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showAddImageDialog(int reqCamera,int reqFile) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PictureDialog addPictureDialogFragment = new PictureDialog(reqCamera,reqFile);
        addPictureDialogFragment.setTargetFragment(this, reqCamera);
        addPictureDialogFragment.show(fm, getTag());
    }

    private void attempSendingData()
    {
        boolean cancel = false;
        View focusView = null;
        String message = "";

        if(TextUtils.isEmpty(emailTxt.getText().toString()))
        {
            emailTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = emailTxt;
            message = "Email tidak boleh kosong";
            cancel = true;
        }
        if(TextUtils.isEmpty(phoneTxt.getText().toString()))
        {
            phoneTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = phoneTxt;
            cancel = true;
            message = "No Telp tidak boleh kosong";
        }
        if(TextUtils.isEmpty(ktpTxt.getText().toString()))
        {
            ktpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = ktpTxt;
            cancel = true;
            message = "No KTP tidak boleh kosong";
        }

        if(TextUtils.isEmpty(alamatTxt.getText().toString()))
        {
            alamatTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = alamatTxt;
            cancel = true;
            message = "Alamat tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(rtTxt.getText().toString()))
        {
            rtTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = rtTxt;
            cancel = true;
            message = "RT tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(rwTxt.getText().toString()))
        {
            rwTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = rwTxt;
            cancel = true;
            message = "RW tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kelurahanTxt.getText().toString()))
        {
            kelurahanTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kelurahanTxt;
            cancel = true;
            message = "Kelurahan tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kecamatanTxt.getText().toString()))
        {
            kecamatanTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kecamatanTxt;
            cancel = true;
            message = "Kecamatan tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kotaTxt.getText().toString()))
        {
            kotaTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kotaTxt;
            cancel = true;
            message = "Kota tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kodePosTxt.getText().toString()))
        {
            kodePosTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kodePosTxt;
            cancel = true;
            message = "Kode pos tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(propinsiTxt.getText().toString()))
        {
            propinsiTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = propinsiTxt;
            cancel = true;
            message = "Propinsi tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(alamatMapTxt.getText().toString()))
        {
            alamatMapTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = alamatMapTxt;
            cancel = true;
            message = "Alamat pin point tidak boleh kosong";
        }
        if(TextUtils.isEmpty(alamatKtpTxt.getText().toString()))
        {
            alamatKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = alamatKtpTxt;
            cancel = true;
            message = "Alamat KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(rtKtpTxt.getText().toString()))
        {
            rtKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = rtKtpTxt;
            cancel = true;
            message = "RT KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(rwKtpTxt.getText().toString()))
        {
            rwKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = rwKtpTxt;
            cancel = true;
            message = "RW KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kelurahanKtpTxt.getText().toString()))
        {
            kelurahanKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kelurahanKtpTxt;
            cancel = true;
            message = "Kelurahan KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kecamatanKtpTxt.getText().toString()))
        {
            kecamatanKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kecamatanKtpTxt;
            cancel = true;
            message = "Kecamatan KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kotaKtpTxt.getText().toString()))
        {
            kotaKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kotaKtpTxt;
            cancel = true;
            message = "Kota KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kodePosKtpTxt.getText().toString()))
        {
            kodePosKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kodePosKtpTxt;
            cancel = true;
            message = "Kode pos KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(propinsiKtpTxt.getText().toString()))
        {
            propinsiKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = propinsiKtpTxt;
            cancel = true;
            message = "Propinsi KTP tidak boleh kosong";
        }
        if (cancel) {
            if (focusView != null)
            {
                focusView.requestFocus();
                cancel = false;
                TastyToast.makeText(getContext(),message,TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
            }

        }else {
            makeSubmitRequest();
        }

    }

    public void makeDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_profile_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();

                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject obj = response.getJSONObject("karyawan");
                        JSONObject objAlamat = obj.getJSONObject("addressdetail");
                        String email = obj.getString("email");
                        String ktp = obj.getString("ktp");
                        String pin_address = obj.getString("pin_address");
                        String address = obj.getString("address");
                        String phone = obj.getString("phone");
                        String npwp = obj.getString("npwp");
                        String photoKtp = obj.getString("photo_ktp");
                        Integer id_ptkp = obj.getInt("ptkp_id");
                        bitmapKtp = ImageUtil.convert(photoKtp);
                        ptkpCmb.setSelection(id_ptkp);
                        emailTxt.setText(email);
                        npwpTxt.setText(npwp);
                        phoneTxt.setText(phone);
                        alamatTxt.setText(address);
                        alamatMapTxt.setText(pin_address);
                        ktpTxt.setText(ktp);
                        imageViewKTPPlaceholder.setImageBitmap(bitmapKtp);
                        rtTxt.setText(objAlamat.getString("rt_tinggal"));
                        rwTxt.setText(objAlamat.getString("rw_tinggal"));
                        kelurahanTxt.setText(objAlamat.getString("kelurahan_tinggal"));
                        kecamatanTxt.setText(objAlamat.getString("kecamatan_tinggal"));
                        kotaTxt.setText(objAlamat.getString("kota_tinggal"));
                        kodePosTxt.setText(objAlamat.getString("kode_pos_tinggal"));
                        propinsiTxt.setText(objAlamat.getString("propinsi_tinggal"));
                        alamatKtpTxt.setText(objAlamat.getString("alamat_ktp"));
                        rtKtpTxt.setText(objAlamat.getString("rt_ktp"));
                        rwKtpTxt.setText(objAlamat.getString("rw_ktp"));
                        kelurahanKtpTxt.setText(objAlamat.getString("kelurahan_ktp"));
                        kecamatanKtpTxt.setText(objAlamat.getString("kecamatan_ktp"));
                        kotaKtpTxt.setText(objAlamat.getString("kota_ktp"));
                        kodePosKtpTxt.setText(objAlamat.getString("kode_pos_ktp"));
                        propinsiKtpTxt.setText(objAlamat.getString("propinsi_ktp"));
                        imageKTP.setVisibility(View.VISIBLE);

                        if(ptkpList.size() > 0)
                        {
                            for (int i = 0; i < ptkpList.size(); i++) {
                                if (ptkpList.get(i).getId().equals(obj.getString("ptkp_id"))) {
                                    ptkpCmb.setSelection(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    TastyToast.makeText(getActivity(), "" + e, TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void makeSubmitRequest()
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_profile_req";
        String user_id = sessionManagement.getUserDetails().get(Config.KEY_ID);
        int pos = ptkpCmb.getSelectedItemPosition();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("npwp", npwpTxt.getText().toString());
        params.put("email", emailTxt.getText().toString());
        params.put("ptkp_id", ptkpList.get(pos).getId());
        params.put("phone", phoneTxt.getText().toString());
        params.put("ktp", ktpTxt.getText().toString());
        params.put("photo_ktp", ImageUtil.convert(bitmapKtp));
        params.put("alamat", alamatTxt.getText().toString());
        params.put("rt", rtTxt.getText().toString());
        params.put("rw", rwTxt.getText().toString());
        params.put("kelurahan", kelurahanTxt.getText().toString());
        params.put("kecamatan", kecamatanTxt.getText().toString());
        params.put("kota", kotaTxt.getText().toString());
        params.put("kode_pos", kodePosTxt.getText().toString());
        params.put("propinsi", propinsiTxt.getText().toString());
        params.put("alamat_map", alamatMapTxt.getText().toString());
        params.put("alamat_ktp", alamatKtpTxt.getText().toString());
        params.put("rt_ktp", rtKtpTxt.getText().toString());
        params.put("rw_ktp", rwKtpTxt.getText().toString());
        params.put("kelurahan_ktp", kelurahanKtpTxt.getText().toString());
        params.put("kecamatan_ktp", kecamatanKtpTxt.getText().toString());
        params.put("kota_ktp", kotaKtpTxt.getText().toString());
        params.put("kode_pos_ktp", kodePosKtpTxt.getText().toString());
        params.put("propinsi_ktp", propinsiKtpTxt.getText().toString());

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.UPDATE_PROFILE_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        if(sessionManagement.getUserDetails().get(Config.KEY_ID) != null) {
                            SuksesDialog suksesDialog = new SuksesDialog(AccountFragment.this);
                            suksesDialog.show(getFragmentManager(),"");
                        }
                        else
                        {
                            TastyToast.makeText(getActivity(), "Data Karyawan tidak ada", Toast.LENGTH_SHORT,TastyToast.CONFUSING).show();
                        }

                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT,TastyToast.CONFUSING).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    TastyToast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT,TastyToast.CONFUSING).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
                }
            }
        });

        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void showAccount()
    {
        AccountFragment accountFragment = new AccountFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, accountFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void launchSelfieCam(){

        int id = getFrontCameraId();


        if(id==-1){


        }else {
            CameraFragment cameraFragment = CameraFragment.newInstance(this);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.m_data_frame, cameraFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    int getFrontCameraId(){

        Camera.CameraInfo ci = new Camera.CameraInfo();

        for(int i =0; i<Camera.getNumberOfCameras();i++){

            Camera.getCameraInfo(i,ci);

            if(ci.facing== Camera.CameraInfo.CAMERA_FACING_FRONT)
                return i;
        }
        return -1;

    }

    private void loadLocation()
    {
        Intent intLoc = new Intent(getActivity(), AddressPickerActivity.class);
        intLoc.putExtra("arg_lat_lng", new MyLatLng(-7.299693502, 112.645867578685));
        intLoc.putExtra("level_zoom",11.0f);
        ArrayList<Pin> pinList = new ArrayList<Pin>();
        pinList.add(new Pin(new MyLatLng(-7.299693502, 112.645867578685),"Work"));
        intLoc.putExtra("list_pins",pinList);
        startActivityForResult(intLoc,REQUEST_ADDRESS );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ADDRESS && resultCode == getActivity().RESULT_OK) {
            Address address = data.getParcelableExtra("address");
            if(address != null)
            {
                String alamatMaps = address.getAddressLine(0)+ ", " + address.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName()+ ", " +address.getPostalCode();
                alamatMapTxt.setText(alamatMaps);
                setMapsLatitude(address.getLatitude());
                setMapsLongitude(address.getLongitude());
            }

        }

        if (requestCode == REQUEST_CAMERA_KTP && resultCode == getActivity().RESULT_OK) {

            //mengambil fambar dari Gallery
            bitmapKtp = (Bitmap) data.getExtras().get("data");
            // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
            imageKTP.setVisibility(View.VISIBLE);
            imageViewKTPPlaceholder.setImageBitmap(bitmapKtp);

        }

        if (requestCode == REQUEST_GALLERY_KTP && resultCode == getActivity().RESULT_OK) {
            Uri filePath = data.getData();
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmapKtp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), filePath)) ;//here parsing a google account profile pic link
                } else {
                    bitmapKtp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                }
                //mengambil fambar dari Gallery
                //bitmapKtp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                imageKTP.setVisibility(View.VISIBLE);
                imageViewKTPPlaceholder.setImageBitmap(bitmapKtp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void makePtkpRequest()
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_ptkp_req";
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                Config.PTKP_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        ptkpList = new ArrayList<>();
                        try {
                            JSONArray Jarray = response.getJSONArray("ptkp");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject json_data = Jarray.getJSONObject(i);
                                Ptkp ptkp = new Ptkp();
                                ptkp.setId(json_data.getString("id"));
                                ptkp.setKodePtkp(json_data.getString("kode_ptkp"));
                                ptkp.setNamaPtkp(json_data.getString("nama_ptkp"));
                                ptkp.setNominal(json_data.getString("nominal"));
                                ptkp.setDescriptions(json_data.getString("descriptions"));
                                ptkpList.add(ptkp);
                            }
                            ptkpAdapter = new PtkpAdapter(getContext(),ptkpList);
                            ptkpCmb.setAdapter(ptkpAdapter);
                            // izinAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
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


    public double getMapsLatitude() {
        return mapsLatitude;
    }

    public void setMapsLatitude(double mapsLatitude) {
        this.mapsLatitude = mapsLatitude;
    }

    public double getMapsLongitude() {
        return mpasLongitude;
    }

    public void setMapsLongitude(double mpasLongitude) {
        this.mpasLongitude = mpasLongitude;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

}

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.an.deviceinfo.device.model.Device;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.activity.LoginActivity;
import com.sia.als.adapter.PtkpAdapter;
import com.sia.als.config.Config;
import com.sia.als.dialog.PictureDialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class KaryawanProfileFragment extends Fragment {
    private View view;
    Button saveBtn,logoutBtn;
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
    ConstraintLayout imageKTP,passwordLayout;
    CoordinatorLayout profileScroll;
    EditText rtTxt,rwTxt,kelurahanTxt,kecamatanTxt,kotaTxt,kodePosTxt,propinsiTxt;
    EditText rtKtpTxt,rwKtpTxt,kelurahanKtpTxt,kecamatanKtpTxt,kotaKtpTxt,kodePosKtpTxt,propinsiKtpTxt,alamatKtpTxt;
    Spinner ptkpCmb;
    PtkpAdapter ptkpAdapter;
    List<Ptkp> ptkpList;
    String idUser;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2,fab3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_WIFI_STATE},1);
        view = inflater.inflate(R.layout.fragment_karyawan_detail, container, false);
        backButton =  (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        saveBtn = (Button) getActivity().findViewById(R.id.button_general);
        backButton.setVisibility(View.GONE);
        saveBtn.setVisibility(View.VISIBLE);
        saveBtn.setText("SIMPAN");
        saveBtn.setVisibility(View.GONE);
        toolbarTitle.setText("Profile");
        loadImageGallery = (ImageView) view.findViewById(R.id.imageview_product_picture);
        imageViewPlaceholder = (ImageView) view.findViewById(R.id.imageview_add_picture_placeholder);
        pinLocBtn = (ImageView) view.findViewById(R.id.image_scan_barcode);
        alamatMapTxt = (EditText) view.findViewById(R.id.alamat_map_txt);
        npwpTxt = (EditText) view.findViewById(R.id.npwp_txt);
        namaTxt = (EditText) view.findViewById(R.id.edittext_nama);
        ktpTxt = (EditText) view.findViewById(R.id.ktp_txt);
        emailTxt = (EditText) view.findViewById(R.id.email_txt);
        phoneTxt = (EditText) view.findViewById(R.id.phone_txt);
        alamatTxt = (EditText) view.findViewById(R.id.alamat_txt);
        passwordTxt = (EditText) view.findViewById(R.id.password_txt);
        konfPasswordTxt = (EditText) view.findViewById(R.id.konf_password_txt);
        logoutBtn = (Button) view.findViewById(R.id.logout_btn);
        imageKTP = (ConstraintLayout) view.findViewById(R.id.constraint_form_ktp);
        passwordLayout = (ConstraintLayout) view.findViewById(R.id.constraint_variant_password_form);
        imageViewKTPPlaceholder = (ImageView) view.findViewById(R.id.imageview_add_ktp_placeholder);
        uploadKtpBtn = (ImageView) view.findViewById(R.id.image_upload_ktp);
        imageKTP.setVisibility(View.GONE);
        profileScroll = (CoordinatorLayout) view.findViewById(R.id.main_profile_koor) ;
        passwordLayout.setVisibility(View.GONE);

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




        if(getArguments() != null)
        {
            bitmap = getArguments().getParcelable("img");
            imageViewPlaceholder.setImageBitmap(bitmap);
            //CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)profileScroll.getLayoutParams();

           // lp.bottomMargin = 160;
            //profileScroll.setLayoutParams(lp);
            logoutBtn.setVisibility(View.GONE);
            idUser = getArguments().getString("user_id");
            makeDataRequest();
        }

        loadImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launchSelfieCam();
                showAddImageDialog(REQUEST_CAMERA_PROFILE,REQUEST_GALLERY_PROFILE);
            }
        });

        uploadKtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddImageDialog(REQUEST_CAMERA_KTP,REQUEST_GALLERY_KTP);
            }
        });

        empDevice = new Device(getContext());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSendingData();
            }
        });
        makePtkpRequest();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);
        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tidak aktif status
                new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Anda akan inaktf karyawan ini!")
                        .setConfirmText("Ya!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                makeUpdateStatusRequest("2");
                                sDialog.hide();
                            }
                        })
                        .show();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset device
                new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Anda akan reset device karyawan ini!")
                        .setConfirmText("Ya!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                makeUpdateStatusRequest("3");
                                sDialog.hide();
                            }
                        })
                        .show();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // aktif status
                new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Anda akan aktifkan karyawan ini!")
                        .setConfirmText("Ya!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                makeUpdateStatusRequest("1");
                                sDialog.hide();
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;


        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;


        }
    }


    private void showAddImageDialog(int reqCamera,int reqFile) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PictureDialog addPictureDialogFragment = new PictureDialog(reqCamera,reqFile);
        addPictureDialogFragment.setTargetFragment(this, reqCamera);
        addPictureDialogFragment.show(fm, getTag());
    }

    public void makeUpdateStatusRequest(String status)
    {
        //status setuju APPROVE, tolak DISAPPROVE
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_status_izin_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_karyawan", idUser);
        params.put("status", status);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.ADMIN_STATUS_KARYAWAN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                         TastyToast.makeText(getActivity(), "" + response.getString("message"), Toast.LENGTH_LONG,TastyToast.SUCCESS).show();

                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT,TastyToast.ERROR).show();

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


    private void attempSendingData()
    {
        boolean cancel = false;
        View focusView = null;
        String message = "";
        if(sessionManagement.getUserDetails().get(Config.KEY_ID) == null) {

            if (TextUtils.isEmpty(konfPasswordTxt.getText().toString())) {
                konfPasswordTxt.setTextColor(getResources().getColor(R.color.active_color));
                focusView = konfPasswordTxt;
                cancel = true;
            }
            if (TextUtils.isEmpty(passwordTxt.getText().toString())) {
                passwordTxt.setTextColor(getResources().getColor(R.color.active_color));
                focusView = passwordTxt;
                cancel = true;
            }
        }
        if(TextUtils.isEmpty(propinsiKtpTxt.getText().toString()))
        {
            propinsiKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = propinsiKtpTxt;
            cancel = true;
            message = "Propinsi KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kodePosKtpTxt.getText().toString()))
        {
            kodePosKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kodePosKtpTxt;
            cancel = true;
            message = "Kode pos KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kotaKtpTxt.getText().toString()))
        {
            kotaKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kotaKtpTxt;
            cancel = true;
            message = "Kota KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kecamatanKtpTxt.getText().toString()))
        {
            kecamatanKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kecamatanKtpTxt;
            cancel = true;
            message = "Kecamatan KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kelurahanKtpTxt.getText().toString()))
        {
            kelurahanKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kelurahanKtpTxt;
            cancel = true;
            message = "Kelurahan KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(rwKtpTxt.getText().toString()))
        {
            rwKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = rwKtpTxt;
            cancel = true;
            message = "RW KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(rtKtpTxt.getText().toString()))
        {
            rtKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = rtKtpTxt;
            cancel = true;
            message = "RT KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(alamatKtpTxt.getText().toString()))
        {
            alamatKtpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = alamatKtpTxt;
            cancel = true;
            message = "Alamat KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(propinsiTxt.getText().toString()))
        {
            propinsiTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = propinsiTxt;
            cancel = true;
            message = "Propinsi tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kodePosTxt.getText().toString()))
        {
            kodePosTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kodePosTxt;
            cancel = true;
            message = "Kode pos tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kotaTxt.getText().toString()))
        {
            kotaTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kotaTxt;
            cancel = true;
            message = "Kota tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kecamatanTxt.getText().toString()))
        {
            kecamatanTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kecamatanTxt;
            cancel = true;
            message = "Kecamatan tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(kelurahanTxt.getText().toString()))
        {
            kelurahanTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = kelurahanTxt;
            cancel = true;
            message = "Kelurahan tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(rwTxt.getText().toString()))
        {
            rwTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = rwTxt;
            cancel = true;
            message = "RW tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(rtTxt.getText().toString()))
        {
            rtTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = rtTxt;
            cancel = true;
            message = "RT tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(alamatMapTxt.getText().toString()))
        {
            alamatMapTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = alamatMapTxt;
            cancel = true;
            message = "Alamat pin point tidak boleh kosong";
        }
        if(TextUtils.isEmpty(alamatTxt.getText().toString()))
        {
            alamatTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = alamatTxt;
            cancel = true;
            message = "Alamat tempat tinggal tidak boleh kosong";
        }
        if(TextUtils.isEmpty(phoneTxt.getText().toString()))
        {
            phoneTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = phoneTxt;
            cancel = true;
            message = "No Telp tidak boleh kosong";
        }
        if(TextUtils.isEmpty(emailTxt.getText().toString()))
        {
            emailTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = emailTxt;
            message = "Email tidak boleh kosong";
            cancel = true;
        }
        if(TextUtils.isEmpty(ktpTxt.getText().toString()))
        {
            ktpTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = ktpTxt;
            cancel = true;
            message = "No KTP tidak boleh kosong";
        }
        if(TextUtils.isEmpty(namaTxt.getText().toString()))
        {
            namaTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = namaTxt;
            cancel = true;
            message = "Nama tidak boleh kosong";
        }
        if (cancel) {
            if (focusView != null)
            {
                focusView.requestFocus();
                cancel = false;
                TastyToast.makeText(getContext(),message,TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
            }

        } else {
            if(passwordTxt.getText().toString().equals(konfPasswordTxt.getText().toString()))
            {
                if(bitmap == null || bitmapKtp == null)
                {
                    //Toast.makeText(getContext(),"Anda belum melakukan pengambilan photo atau upload KTP",Toast.LENGTH_LONG).show();
                    TastyToast.makeText(getContext(),"Anda belum melakukan pengambilan photo atau upload KTP",TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                }
                else
                {
                    if (ConnectivityReceiver.isConnected()) {
                        makeSubmitRequest();
                    }
                }

            }
            else
            {
                TastyToast.makeText(getContext(),"Password dan Konfirmasi tidak sama", Toast.LENGTH_LONG,TastyToast.ERROR).show();

            }

        }
    }

    public void makeDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_profile_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", idUser);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();

                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject obj = response.getJSONObject("karyawan");
                        Log.i("info_account", obj.toString());

                        JSONObject objAlamat = obj.getJSONObject("addressdetail");
//                        String user_fullname = obj.getString("employee_name");
                        String email = obj.getString("email");
                        String ktp = obj.getString("ktp");
                        String pin_address = obj.getString("pin_address");
                        String address = objAlamat.getString("alamat_tinggal");
                        String phone = obj.getString("phone");
                        String npwp = obj.getString("npwp");
                        String photo = obj.getString("photo");
                        String photoKtp = obj.getString("photo_ktp");
                        bitmap = ImageUtil.convert(photo);
                        bitmapKtp = ImageUtil.convert(photoKtp);
//                        namaTxt.setText(user_fullname);
                        emailTxt.setText(email);
                        npwpTxt.setText(npwp);
                        phoneTxt.setText(phone);
                        alamatTxt.setText(address);
                        alamatMapTxt.setText(pin_address);
                        ktpTxt.setText(ktp);
                        imageViewPlaceholder.setImageBitmap(bitmap);
                        imageViewKTPPlaceholder.setImageBitmap(bitmapKtp);
                        alamatKtpTxt.setText(objAlamat.getString("alamat_ktp"));
                        rtTxt.setText(objAlamat.getString("rt_tinggal"));
                        rwTxt.setText(objAlamat.getString("rw_tinggal"));
                        kelurahanTxt.setText(objAlamat.getString("kelurahan_tinggal"));
                        kecamatanTxt.setText(objAlamat.getString("kecamatan_tinggal"));
                        kotaTxt.setText(objAlamat.getString("kota_tinggal"));
                        kodePosTxt.setText(objAlamat.getString("kode_pos_tinggal"));
                        propinsiTxt.setText(objAlamat.getString("propinsi_tinggal"));
                        rtKtpTxt.setText(objAlamat.getString("rt_ktp"));
                        rwKtpTxt.setText(objAlamat.getString("rw_ktp"));
                        kelurahanKtpTxt.setText(objAlamat.getString("keluarahan_ktp"));
                        kecamatanKtpTxt.setText(objAlamat.getString("kecamatan_ktp"));
                        kotaKtpTxt.setText(objAlamat.getString("kota_ktp"));
                        kodePosKtpTxt.setText(objAlamat.getString("kode_pos_ktp"));
                        propinsiKtpTxt.setText(objAlamat.getString("propinsi_ktp"));
                        imageKTP.setVisibility(View.VISIBLE);

                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG,TastyToast.INFO).show();

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

        String deviceInfo = empDevice.getManufacturer()+" "+empDevice.getHardware()+" "+empDevice.getDevice();
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Map<String, String> params = new HashMap<String, String>();
        params.put("photo", ImageUtil.convert(bitmap));
        params.put("photo_ktp", ImageUtil.convert(bitmapKtp));
        params.put("nama", namaTxt.getText().toString());
        params.put("ktp", ktpTxt.getText().toString());
        params.put("email", emailTxt.getText().toString());
        params.put("npwp", npwpTxt.getText().toString());
        params.put("phone", phoneTxt.getText().toString());
        params.put("alamat", alamatTxt.getText().toString());
        params.put("alamat_map", alamatMapTxt.getText().toString());
        params.put("password", passwordTxt.getText().toString());
        params.put("alamat_ktp", alamatKtpTxt.getText().toString());
        params.put("rt", rtTxt.getText().toString());
        params.put("rw", rwTxt.getText().toString());
        params.put("kelurahan", kelurahanTxt.getText().toString());
        params.put("kecamatan", kecamatanTxt.getText().toString());
        params.put("kota", kotaTxt.getText().toString());
        params.put("kode_pos", kodePosTxt.getText().toString());
        params.put("propinsi", propinsiTxt.getText().toString());
        params.put("rt_ktp", rtKtpTxt.getText().toString());
        params.put("rw_ktp", rwKtpTxt.getText().toString());
        params.put("kelurahan_ktp", kelurahanKtpTxt.getText().toString());
        params.put("kecamatan_ktp", kecamatanKtpTxt.getText().toString());
        params.put("kota_ktp", kotaKtpTxt.getText().toString());
        params.put("kode_pos_ktp", kodePosKtpTxt.getText().toString());
        params.put("propinsi_ktp", propinsiKtpTxt.getText().toString());
        params.put("latitude", String.valueOf(getMapsLatitude()));
        params.put("longitude", String.valueOf(getMapsLongitude()));
        params.put("device", deviceInfo);
        params.put("firebase_id", regId);
        String user_id = "0";
        if(sessionManagement.getUserDetails().get(Config.KEY_ID) != null)
        {
            user_id = sessionManagement.getUserDetails().get(Config.KEY_ID);
            String mac = sessionManagement.getUserDetails().get(Config.KEY_MAC);
            if(mac.equals(""))
            {
                String macAddress = UUID.randomUUID().toString();
                params.put("mac_address", macAddress);
            }
        }
        else
        {
            // WifiManager manager = (WifiManager) getActivity().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);
            //WifiInfo info = manager.getConnectionInfo();
            String macAddress = UUID.randomUUID().toString();
            params.put("mac_address", macAddress);
        }
        params.put("user_id", user_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.INSERT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {

                        if(sessionManagement.getUserDetails().get(Config.KEY_ID) == null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("status","sukses");
                            bundle.putString("page","login");
                            bundle.putString("body_message",response.getString("message"));
                            SuksesFragment suksesFragment = new SuksesFragment();
                            suksesFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.m_data_frame, suksesFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                        }
                        else
                        {
                            String error = response.getString("message");
                            //TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT,TastyToast.SUCCESS).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("status","sukses");
                            bundle.putString("page","login");
                            bundle.putString("body_message",response.getString("message"));
                            SuksesFragment suksesFragment = new SuksesFragment();
                            suksesFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.m_frame, suksesFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                        }

                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT,TastyToast.INFO).show();

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
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT,TastyToast.ERROR).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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

        if (requestCode == REQUEST_CAMERA_PROFILE && resultCode == getActivity().RESULT_OK) {

            //mengambil fambar dari Gallery
            bitmap = (Bitmap) data.getExtras().get("data");

            imageViewPlaceholder.setImageBitmap(bitmap);

        }

        if (requestCode == REQUEST_GALLERY_PROFILE && resultCode == getActivity().RESULT_OK) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), filePath)) ;//here parsing a google account profile pic link
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                }

                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                imageViewPlaceholder.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
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

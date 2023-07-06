package com.sia.als.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.an.deviceinfo.device.model.Device;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.activity.KodePerusahaanActivity;
import com.sia.als.activity.LoginActivity;
import com.sia.als.adapter.IzinAdapter;
import com.sia.als.adapter.KaryawanAdapter;
import com.sia.als.adapter.NotifikasiAdapter;
import com.sia.als.adapter.PtkpAdapter;
import com.sia.als.config.Config;
import com.sia.als.model.Absensi;
import com.sia.als.model.Izin;
import com.sia.als.model.Karyawan;
import com.sia.als.model.Notifikasi;
import com.sia.als.model.Ptkp;
import com.sia.als.util.ConnectivityReceiver;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NewProfileFragment extends Fragment {
    private View view;
    Button saveBtn,logoutBtn,notif, btnCancel, btnSave;
    ImageButton backButton,passBtn;
    TextView toolbarTitle, namaTxt, jabatanTxt;
    ImageView photoProfile;
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
    LinearLayout accountTxt, passwordTxt, taskTxt, slipTxt, peraturanTxt, privasiTxt, logoutTxt, emailTxt;
    List<Karyawan> karyawanList = new ArrayList<>();
    KaryawanAdapter karyawanAdapter;
    EditText inputPasswordTxt, konfPasswordTxt;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.account_profile, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setVisibility(View.GONE);
        notif.setBackgroundResource(R.drawable.ic_notif);
        backButton.setVisibility(View.GONE);
        toolbarTitle.setText("Profile");
        inputPasswordTxt = (EditText) view.findViewById(R.id.inputlayout_password);
        konfPasswordTxt = (EditText) view.findViewById(R.id.inputlayout_konf_password);
        namaTxt = (TextView) view.findViewById(R.id.nama_karyawan_txt);
        photoProfile = (ImageView) view.findViewById(R.id.profile_image);
        jabatanTxt = (TextView) view.findViewById(R.id.jabatan_txt);
        accountTxt = (LinearLayout) view.findViewById(R.id.account_btn);
        passwordTxt = (LinearLayout) view.findViewById(R.id.ganti_password_btn);
        taskTxt = (LinearLayout) view.findViewById(R.id.task_btn);
        slipTxt = (LinearLayout) view.findViewById(R.id.slip_gaji_btn);
        peraturanTxt = (LinearLayout) view.findViewById(R.id.peraturan_btn);
        privasiTxt = (LinearLayout) view.findViewById(R.id.privacy_policy_btn);
        logoutTxt = (LinearLayout) view.findViewById(R.id.logout_btn);
        sessionManagement = new SessionManagement(getContext());
        profileScroll = (NestedScrollView) view.findViewById(R.id.scrollview_form_product) ;


        passwordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.activity_password);
                btnCancel = dialog.findViewById(R.id.bt_cancel);
                btnSave = dialog.findViewById(R.id.bt_simpan);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogPassword();
                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnSave = (Button) dialog.findViewById(R.id.bt_simpan);
                inputPasswordTxt = (EditText) dialog.findViewById(R.id.password_txt);
                konfPasswordTxt = (EditText) dialog.findViewById(R.id.konf_password_txt);
                dialog.show();
            }
        });

        accountTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccount();
            }
        });

        taskTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showTask(); }
        });

        slipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSlip();
            }
        });

        peraturanTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPeraturan();
            }
        });

        privasiTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrivasi();
            }
        });

        if(sessionManagement.getUserDetails().get(Config.KEY_ID) != null)
        {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)profileScroll.getLayoutParams();

            lp.bottomMargin = 160;
            profileScroll.setLayoutParams(lp);

            makeDataRequest();
            logoutTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionManagement.logoutSession();
                    Intent logout = new Intent(getActivity(), KodePerusahaanActivity.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(logout);
                }
            });
        }
        else
        {
            logoutTxt.setVisibility(View.GONE);
        }

        return view;

    }

    //Peraturan
    private void showPeraturan() {
        PeraturanFragment peraturanFragment = new PeraturanFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, peraturanFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    //Privacy and Policy
    private void showPrivasi() {
        PrivacyPolicyFragment privacyPolicyFragment = new PrivacyPolicyFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, privacyPolicyFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    //Task
    private void showTask(){
        TaskFragment taskFragment = new TaskFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, taskFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    //Slip Gaji
    private void showSlip() {
        SlipGajiFragment slipGajiFragment = new SlipGajiFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, slipGajiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    //Ganti password
    private void dialogPassword() {
        boolean cancel = false;
        View focusView = null;
        String message = "";
        if (TextUtils.isEmpty(konfPasswordTxt.getText().toString())) {
            konfPasswordTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = konfPasswordTxt;
            cancel = true;
            message = "Konfirmasi tidak boleh kosong";
        }
        if (TextUtils.isEmpty(inputPasswordTxt.getText().toString())) {
            inputPasswordTxt.setTextColor(getResources().getColor(R.color.active_color));
            focusView = inputPasswordTxt;
            cancel = true;
            message = "Password tidak boleh kosong";
        }
        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
                cancel = false;
                TastyToast.makeText(getContext(), message, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
            }
        }
        else {
            if(inputPasswordTxt.getText().toString().equals(konfPasswordTxt.getText().toString()))
            {
                makeSubmitRequest();
            }
            else
            {
                TastyToast.makeText(getContext(),"Password dan Konfirmasi tidak sama",Toast.LENGTH_LONG,TastyToast.ERROR).show();
            }
        }
    }

    //Account
    private void showAccount() {
        AccountFragment accountFragment = new AccountFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, accountFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }



    private void makeSubmitRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_password_req";

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        Map<String, String> params = new HashMap<String, String>();
        params.put("password", inputPasswordTxt.getText().toString());
        String user_id = "0";
        if(sessionManagement.getUserDetails().get(Config.KEY_ID) != null)
        {
            user_id = sessionManagement.getUserDetails().get(Config.KEY_ID);
        }
        params.put("user_id", user_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.UPDATE_PASSWORD_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        String pesan = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + pesan, TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();

                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_SHORT,TastyToast.CONFUSING).show();

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
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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
                        karyawanList = new ArrayList<>();
                        try {
                            //JSONArray Jarray = response.getJSONArray("karyawan");
                            JSONObject obj = response.getJSONObject("karyawan");
                            int statusKaryawan = obj.getInt("status_employee");
                            if (statusKaryawan == 1)
                            {
                                JSONObject arr_jabatan = obj.getJSONObject("jabatan");
                                String user_fullname = obj.getString("employee_name");
                                String jabatan_user = arr_jabatan.getString("nama_jabatan");
                                String photo_user = obj.getString("photo");
                                namaTxt.setText(user_fullname);
                                if(jabatan_user.equals("null"))
                                {
                                    jabatanTxt.setText("");
                                }
                                else
                                {
                                    jabatanTxt.setText(jabatan_user);
                                }

                                bitmap = ImageUtil.convert(photo_user);
                                photoProfile.setImageBitmap(bitmap);
                            }
                            else
                            {
                                sessionManagement.logoutSession();
                                Intent logout = new Intent(getActivity(), KodePerusahaanActivity.class);
                                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                // Add new Flag to start new Activity
                                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(logout);
                            }

                        } catch (JSONException e) {
                            TastyToast.makeText(getActivity(), e.toString(), TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
                        }
                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();
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


}

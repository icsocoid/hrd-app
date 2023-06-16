package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.sia.als.activity.ResetDeviceActivity;
import com.sia.als.config.Config;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetDeviceFragment extends Fragment {
    private View view;
    SessionManagement sessionManagement;
    ImageView imagePlaceholder;
    TextView namaTxt, ktpTxt, jabatanTxt, backTxt, toolbarTitle;
    EditText emailTxt;
    Button resetBtn, notif;
    Bitmap bitmap;
    ImageButton backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_WIFI_STATE}, 1);
        view = inflater.inflate(R.layout.fragment_reset_device, container, false);
        sessionManagement = new SessionManagement(getContext());
        backButton =  (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle =  (TextView) getActivity().findViewById(R.id.text_title);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        imagePlaceholder = (ImageView) view.findViewById(R.id.imageview_placeholder);
        namaTxt = (TextView) view.findViewById(R.id.fullname_txt);
        ktpTxt = (TextView) view.findViewById(R.id.nik_txt);
        jabatanTxt = (TextView) view.findViewById(R.id.jabatan_txt);
        backTxt = (TextView) view.findViewById(R.id.back_txt);
        emailTxt = (EditText) view.findViewById(R.id.et_email);
        resetBtn = (Button) view.findViewById(R.id.btn_reset);
        backButton.setVisibility(View.GONE);
        notif.setVisibility(View.GONE);
        toolbarTitle.setText("Reset Device");

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailTxt.equals(""))
                {
                    TastyToast.makeText(getContext(), "Anda belum memasukkan email", TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                }
                else
                {
                    submitDataRequest();
                }

            }
        });

        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManagement.logoutSession();
                Intent logout = new Intent(getActivity(), LoginActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(logout);

            }
        });

        makeDataRequest();

        return view;
    }

    public void makeDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_reset_req";
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
                        int statusKaryawan = obj.getInt("status_employee");
                        if (statusKaryawan == 3)
                        {
                            JSONObject obj_jabatan = obj.getJSONObject("jabatan");
                            String user_fullname = obj.getString("employee_name");
                            String photo = obj.getString("photo");
                            String ktp = obj.getString("ktp");
                            String jabatan = obj_jabatan.getString("nama_jabatan");
                            bitmap = ImageUtil.convert(photo);
                            namaTxt.setText(user_fullname);
                            ktpTxt.setText(ktp);
                            jabatanTxt.setText(jabatan);
                            imagePlaceholder.setImageBitmap(bitmap);
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

                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getContext(), "" + error, TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();

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
                    TastyToast.makeText(getContext(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void submitDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_forgot_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        params.put("email",emailTxt.getText().toString());
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.RESET_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        JSONObject karyawan = response.getJSONObject("karyawan");
                        if(sessionManagement.getUserDetails().get(Config.KEY_ID) != null) {
                            //redirect ke halaman verifikasi
                            int karyawanId = karyawan.getInt("id");
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id",String.valueOf(karyawanId));
                            VerificationFragment suksesFragment = new VerificationFragment();
                            suksesFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.m_data_frame, suksesFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                        }
                        else
                        {
                            TastyToast.makeText(getActivity(), "Nama karyawan belum terdaftar", TastyToast.LENGTH_SHORT,TastyToast.CONFUSING).show();
                        }

                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(getActivity(), "" + error, TastyToast.LENGTH_SHORT,TastyToast.CONFUSING).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    TastyToast.makeText(getActivity(), "" + e, TastyToast.LENGTH_SHORT,TastyToast.CONFUSING).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    TastyToast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

}

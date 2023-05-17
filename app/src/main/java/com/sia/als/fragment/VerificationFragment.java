package com.sia.als.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.an.deviceinfo.device.model.Device;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.MainActivity;
import com.sia.als.R;
import com.sia.als.activity.LoginActivity;
import com.sia.als.activity.ProfileActivity;
import com.sia.als.config.Config;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VerificationFragment extends Fragment {

    private View view;
    TextView toolbarTitle;
    ImageButton backButton;
    Button notif;
    TextInputEditText verifTxt1,verifTxt2,verifTxt3,verifTxt4;
    AppCompatButton verifBtn;
    String id;
    Device empDevice;
    String newToken;
    String macAddress;
    SessionManagement sessionManagement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_verification, container, false);
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        toolbarTitle = (TextView) getActivity().findViewById(R.id.text_title);
        toolbarTitle.setText("Verifikasi Email");
        backButton.setVisibility(View.GONE);
        notif = (Button) getActivity().findViewById(R.id.button_general);
        notif.setVisibility(View.GONE);
        verifTxt1 = (TextInputEditText) view.findViewById(R.id.verif_txt_1);
        verifTxt2 = (TextInputEditText) view.findViewById(R.id.verif_txt_2);
        verifTxt3 = (TextInputEditText) view.findViewById(R.id.verif_txt_3);
        verifTxt4 = (TextInputEditText) view.findViewById(R.id.verif_txt_4);
        verifBtn = (AppCompatButton) view.findViewById(R.id.verif_btn);
        if(getArguments() != null)
        {
            id = getArguments().getString("user_id");
        }

        verifTxt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!verifTxt1.getText().toString().equals(""))
                {
                    verifTxt2.requestFocus();
                }
            }
        });
        verifTxt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(verifTxt2.getText().toString().equals(""))
                {
                    verifTxt1.requestFocus();
                }
                else
                {
                    verifTxt3.requestFocus();
                }
            }
        });
        verifTxt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(verifTxt3.getText().toString().equals(""))
                {
                    verifTxt2.requestFocus();
                }
                else
                {
                    verifTxt4.requestFocus();
                }
            }
        });
        verifTxt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(verifTxt4.getText().toString().equals(""))
                {
                    verifTxt3.requestFocus();
                }
                else
                {
                    makeDataRequest();
                }
            }
        });


        verifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id == null)
                {
                    TastyToast.makeText(getContext(), "Terjadi kesalahan pada proses verifikasi", TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                }
                else
                {
                    makeDataRequest();
                }

            }
        });

        empDevice = new Device(getContext());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    return;
                }
                newToken = task.getResult().getToken();
                macAddress = UUID.randomUUID().toString();
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("regId", newToken);
                editor.commit();
                Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("token", newToken);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(registrationComplete);
            }
        });

        return view;
    }

    private void makeDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_verfikasi_req";
        Map<String, String> params = new HashMap<String, String>();
        String verif1 = verifTxt1.getText().toString();
        String verif2 = verifTxt2.getText().toString();
        String verif3 = verifTxt3.getText().toString();
        String verif4 = verifTxt4.getText().toString();

        String deviceInfo = empDevice.getManufacturer()+" "+empDevice.getHardware()+" "+empDevice.getDevice();
        if(verif1.equals("") && verif2.equals("") && verif3.equals("") && verif4.equals(""))
        {
            TastyToast.makeText(getContext(), "Kode verifikasi masih belum lengkap", Toast.LENGTH_LONG,TastyToast.CONFUSING);
        }
        else
        {
            params.put("verfikasi", verif1+verif2+verif3+verif4);
            params.put("user_id",id);
            params.put("device", deviceInfo);
            params.put("firebase_id", newToken);
            params.put("mac_address", macAddress);
            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                    Config.VERIFIKASI_URL, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    try {
                        Boolean status = response.getBoolean("status");
                        if (status) {
                            JSONObject karyawan = response.getJSONObject("karyawan");
                            if(karyawan != null)
                            {
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
                                TastyToast.makeText(getContext(), "Terjadi kesalahan dalam proses verifikasi", TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();
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
                        //  Toast.makeText(LoginActivity.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                        TastyToast.makeText(getContext(), getResources().getString(R.string.connection_time_out), TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                    }
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }
}

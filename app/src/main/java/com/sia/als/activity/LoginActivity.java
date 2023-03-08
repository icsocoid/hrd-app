package com.sia.als.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.MainActivity;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    AppCompatEditText usernameTxt,passwordTxt;
    Button signinBtn;
    TextView signUpBtn,forgotBtn;
    String newToken;
    String macAddress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        usernameTxt = (AppCompatEditText) findViewById(R.id.et_email);
        passwordTxt = (AppCompatEditText) findViewById(R.id.et_password);
        signinBtn = (Button) findViewById(R.id.btn_sign_in);
        signUpBtn = (TextView) findViewById(R.id.daftar_txt);
        forgotBtn = (TextView) findViewById(R.id.forgot_txt);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newToken == null)
                {
                    TastyToast.makeText(LoginActivity.this, "" + "Tidak dapat mengambil firebase token", Toast.LENGTH_LONG,TastyToast.ERROR);
                }
                else
                {
                    if(usernameTxt.getText().toString().equals("") || passwordTxt.getText().toString().equals(""))
                    {
                        TastyToast.makeText(LoginActivity.this, "" + "Username atau password tidak boleh koosng", Toast.LENGTH_LONG,TastyToast.ERROR);
                    }
                    else
                    {
                        makeLoginRequest(usernameTxt.getText().toString(),passwordTxt.getText().toString());
                    }

                }

            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    return;
                }
                newToken = task.getResult().getToken();
                macAddress = UUID.randomUUID().toString();
                Log.i("token", newToken);
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("regId", newToken);
                editor.commit();
                Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("token", newToken);
                LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(registrationComplete);
            }
        });

        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("regId", newToken);
                editor.commit();
                Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("token", newToken);
                LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(registrationComplete);
            }
        });*/
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(LoginActivity.this,ProfileActivity.class);
                startActivity(profileIntent);
            }
        });
        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotIntent = new Intent(LoginActivity.this,ForgotActivity.class);
                startActivity(forgotIntent);
            }
        });
    }

    private void makeLoginRequest(String email, final String password) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("please wait...");
        dialog.show();

        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_email", email);
        params.put("password", password);
        params.put("firebase_id", newToken);
        params.put("mac_address", macAddress);
        SessionManagement sess = new SessionManagement(getApplicationContext());

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.LOGIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.i("hasil_login",response.toString());
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        if(response.getString("first_register").equals("yes"))
                        {
                            Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            JSONObject obj = response.getJSONObject("karyawan");
                            String user_id = obj.getString("id");
                            String user_fullname = obj.getString("employee_name");
                            String user_email = obj.getString("email");
                            String is_admin = obj.getString("is_admin");
                            String macAddress = obj.getString("employee_code");
                            int statusKaryawan = obj.getInt("status_employee");
                            if(statusKaryawan == 3)
                            {
                                SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
                                sessionManagement.createLoginSession(user_id, user_email, user_fullname,is_admin,macAddress);
                                Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                               // Log.i("tes_lanjut","OK");
                                SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
                                sessionManagement.createLoginSession(user_id, user_email, user_fullname,is_admin,macAddress);
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }



                        }


                    } else {
                        String error = response.getString("message");
                        TastyToast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_LONG,TastyToast.ERROR);
                      //  Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();

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
                    TastyToast.makeText(LoginActivity.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_LONG,TastyToast.CONFUSING);
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}

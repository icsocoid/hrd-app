package com.sia.als.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
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

public class CodeActivity extends AppCompatActivity {

    TextInputEditText codeTxt;
    AppCompatButton nextBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_code);
        codeTxt = (TextInputEditText) findViewById(R.id.code_txt);
        nextBtn = (AppCompatButton) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codeTxt.getText().toString().equals("")) {
                    TastyToast.makeText(CodeActivity.this, "Kode perusahaan belum dimasukkan", Toast.LENGTH_LONG,TastyToast.ERROR);
                } else {
                    makeLoginRequest();
                }
            }
        });
    }

    private void makeLoginRequest() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("please wait...");
        dialog.show();

        String tag_json_obj = "json_code_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("kode", codeTxt.getText().toString());
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.BASE_INIT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.i("hasil_login",response.toString());
                dialog.dismiss();
                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {

                        JSONObject obj = response.getJSONObject("user");
                        String user_id = obj.getString("id");
                        String subdomain = obj.getString("name");
                        String email = obj.getString("email");
                        SessionManagement sessionManagement = new SessionManagement(CodeActivity.this);
                        sessionManagement.createInitSession(subdomain);
                        Context ctx = getApplicationContext();
                        PackageManager pm = ctx.getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage(ctx.getPackageName());
                        Intent mainIntent = Intent.makeRestartActivityTask(intent.getComponent());
                        ctx.startActivity(mainIntent);
                        Runtime.getRuntime().exit(0);
                    } else{
                        TastyToast.makeText(CodeActivity.this, response.getString("message"), Toast.LENGTH_LONG,TastyToast.CONFUSING);
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
                    TastyToast.makeText(CodeActivity.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_LONG,TastyToast.CONFUSING);
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}

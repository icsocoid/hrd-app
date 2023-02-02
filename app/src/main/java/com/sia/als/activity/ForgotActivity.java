package com.sia.als.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.config.Config;
import com.sia.als.dialog.SuksesDialog;
import com.sia.als.fragment.AddIzinFragment;
import com.sia.als.util.CustomVolleyJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ForgotActivity extends AppCompatActivity {

    Button submitBtn;
    EditText emailTxt;
    TextView loginBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forgot);
        emailTxt = (EditText) findViewById(R.id.email_txt);
        loginBtn = (TextView) findViewById(R.id.login_btn);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequestSubmitData();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogin();
            }
        });
    }

    private void makeRequestSubmitData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_forgot_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("email",emailTxt.getText().toString());
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.FORGOT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    Boolean status = response.getBoolean("status");
                    if (status) {
                        dialog.hide();
                        SuksesDialog suksesDialog = new SuksesDialog(ForgotActivity.this);
                        suksesDialog.show(getSupportFragmentManager(),"");
                       // suksesDialog.bodyTxt.setText(response.getString("message"));
                    } else {
                        dialog.hide();
                        String error = response.getString("message");
                        Toast.makeText(null, "" + error, Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(null, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    public void showLogin()
    {
        Intent loginIntn = new Intent(ForgotActivity.this,LoginActivity.class);
        startActivity(loginIntn);
    }
}

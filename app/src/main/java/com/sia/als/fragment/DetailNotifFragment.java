package com.sia.als.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;
import com.sia.als.AppController;
import com.sia.als.R;
import com.sia.als.adapter.NotifikasiAdapter;
import com.sia.als.config.Config;
import com.sia.als.dialog.AlasanDialog;
import com.sia.als.model.Notifikasi;
import com.sia.als.util.CustomVolleyJsonRequest;
import com.sia.als.util.ImageUtil;
import com.sia.als.util.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import android.content.res.Resources;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.kinst.jakub.view.StatefulLayout;

public class DetailNotifFragment extends Fragment {
    View view;
    TextView titleTxt;
    ImageButton backButton;
    Bitmap bitmap;
    //SimpleDateFormat sdf = new SimpleDateFormat(oldFormat,new Locale("id","ID"));
    WebView webview;
    SessionManagement sessionManagement;
    List<Notifikasi> data = new ArrayList<>();
    String idNotifikasi = "";




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        sessionManagement = new SessionManagement(getContext());
        backButton = (ImageButton) getActivity().findViewById(R.id.button_back);
        backButton.setVisibility(View.GONE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment();
            }
        });

        if(getArguments() != null)
        {
            idNotifikasi = getArguments().getString("notifikasi_id");
            makeDataRequest();
        }
        titleTxt = (TextView) view.findViewById(R.id.title_txt);
        webview = (WebView) view.findViewById(R.id.news_description);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return view;
    }

    private void showFragment() {
        NotifikasiFragment notifikasiFragment = new NotifikasiFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.m_frame, notifikasiFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void makeDataRequest() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("please wait...");
        dialog.show();
        String tag_json_obj = "json_data_notifikasi_req";
        String userId = sessionManagement.getUserDetails().get(Config.KEY_ID);
        Map<String, String> params = new HashMap<String, String>();
        params.put("notifikasi_id", idNotifikasi);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                Config.DETAIL_NOTIFICATION_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                 dialog.dismiss();
                try {

                    Boolean status = response.getBoolean("status");
                    if (status) {
                        try {
                            JSONObject jObject = response.getJSONObject("data");
                            titleTxt.setText(jObject.getString("subject"));
                            webview.setBackgroundColor(Color.TRANSPARENT);
                            webview.getSettings().setDefaultTextEncodingName("UTF-8");
                            webview.setFocusableInTouchMode(false);
                            webview.setFocusable(false);

                            webview.setOnLongClickListener(v -> true);
                            webview.setLongClickable(false);


                            webview.getSettings().setJavaScriptEnabled(true);

                            WebSettings webSettings = webview.getSettings();
                            Resources res = getResources();
                            int fontSize = 16;
                            webSettings.setDefaultFontSize(fontSize);

                            String mimeType = "text/html; charset=UTF-8";
                            String encoding = "utf-8";
                            String htmlText = jObject.getString("content");

                             String  bg_paragraph = "<style type=\"text/css\">body{color: #000000;}";


                            String text_default = "<html><head>"
                                    + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                                    + bg_paragraph
                                    + "</style></head>"
                                    + "<body>"
                                    + htmlText
                                    + "</body></html>";


                            webview.loadDataWithBaseURL(null, text_default, mimeType, encoding, null);

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    //dialog.hide();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //dialog.hide();
                 dialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //statefulLayout.setState(Config.STATE_NO_CONNECTION);
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
